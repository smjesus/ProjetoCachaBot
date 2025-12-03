/**
 * Projeto:  CachaBot - BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2024
 * Equipe:   Murilo, Victor, Allan
 */
package br.com.aeroceti.cachaBot.configuracoes;

import java.util.EnumSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import jakarta.annotation.PreDestroy;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import br.com.aeroceti.cachaBot.comandos.MemberWinRole;
import br.com.aeroceti.cachaBot.repositorios.ServidoresRepository;
import br.com.aeroceti.cachaBot.servicos.ColaboradoresService;
import br.com.aeroceti.cachaBot.servicos.I18nService;
import br.com.aeroceti.cachaBot.servicos.MailSenderService;
import br.com.aeroceti.cachaBot.componentes.ComandosBarraEventListener;

/**
 * Classe de Configuracao do Discord4J.
 * 
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
@Configuration
public class GerenteBotConfiguration {

    @Value("${token}")
    private String token;
    
    @Value("${application.baseUrl}")
    private String baseUrl;
    
    private final I18nService i18svc;
    private final MailSenderService       mailService;
    private final ServidoresRepository    servidoresDatabse;
    private final ColaboradoresService    usersService;   
        
    private static JDA   jdaCliente    = null;
    private final Logger logger = LoggerFactory.getLogger(GerenteBotConfiguration.class);

    /**
     * Classe de Configuracao do Bot CachaBOT.
     * 
     * @param rep      - JPA Repositorio para a classe Servidor
     * @param userDB   - JPA Repositorio para a classe Colaborador (usuarios)
     * @param msv
     * @param i18serv  - Classe de Servico de Internacionalizacao
     */
    public GerenteBotConfiguration(ServidoresRepository rep, ColaboradoresService userDB, MailSenderService msv, I18nService i18serv) {
        this.i18svc = i18serv;
        this.mailService = msv;
        this.servidoresDatabse = rep;
        this.usersService = userDB;
    }
    
    /**
     * BEAN do JDA para o Spring Boot realizar a comunicacao com o Discord
     * 
     * @return Cliente de comunicacao com o Discord da biblioteca JDA 
     */
    @Bean
    public JDA gatewayDiscordClient() {
        try {
            try {
                jdaCliente.shutdownNow();
                jdaCliente = null;
                logger.info("JDA REINICIALIZADO com sucesso ...");    
            } catch(Exception ex) {
                jdaCliente = null;
            }
            int contador = servidoresDatabse.countByAtivo(true);
            jdaCliente = JDABuilder.create(token, EnumSet.allOf(GatewayIntent.class) ) .build();
            jdaCliente.getPresence().setActivity(Activity.customStatus("Ativo em " + contador + " servidores."));
            logger.info("JDA Inicializado com sucesso ...");            
            logger.info("Configurando o cliente da Aplicacao do Discord ...");
            // ADICIONA os Comandos de BARRA:
            jdaCliente.updateCommands().addCommands(
                // Adiciona comandos de Ajuda e de Cargos:
                Commands.slash("ajuda", "Apresenta uma orientação sobre o CachaBOT."),
                Commands.slash("listarcargos", "Apresenta os cargos registrados no servidor."),
                Commands.slash("cargoprovisorio", "Define o cargo padrão que todo usuário ira receber provisóriamente.")
                            .addOption(OptionType.STRING, "id-do-cargo", "Informe o ID do cargo.", true),
                Commands.slash("cargodefinitivo", "Define o cargo definitivo que os usuários devem receber.")
                            .addOption(OptionType.STRING, "id-do-cargo", "Informe o ID do cargo.", true),
                Commands.slash("cargosuporte", "Define o cargo de Suporte Tecnico no Servidor.")
                            .addOption(OptionType.STRING, "id-do-cargo", "Informe o ID do cargo.", true),
                Commands.slash("salaboasvindas", "Define a Sala Inicial que todo usuário terá acesso (Boas Vindas).")
                            .addOption(OptionType.STRING, "sala-boas-vindas", "Informe o ID da Sala de Boas Vindas.", true),
                
                // Adiciona comandos da Mensagem de Boas Vindas:
                Commands.slash("boasvindas", "Apresenta a Mensagem de boas vindas no canal de verificacao (Sala de Boas Vindas)."),
                Commands.slash("msgcor", "Define a COR da barra lateral da Mensagem de Boas Vindas.")
                            .addOption(OptionType.STRING, "cor-da-mensagem", "Informe a COR desejada.", true),
                Commands.slash("msglinha", "Define a imagem da barra no final da Mensagem de Boas Vindas.")
                            .addOption(OptionType.STRING, "linha-da-mensagem", "Informe a URL da imagem desejada.", true),
                Commands.slash("msgicone", "Define o ICONE da Mensagem de Boas Vindas.")
                            .addOption(OptionType.STRING, "icone-da-mensagem", "Informe a URL da imagem desejada.", true),
                Commands.slash("msgtitulo", "Define o TITULO da Mensagem de Boas Vindas.")
                            .addOption(OptionType.STRING, "titulo-da-mensagem", "Informe o TITULO desejado.", true),
                Commands.slash("msgcorpo", "Define o CONTEUDO da Mensagem de Boas Vindas.")
                            .addOption(OptionType.STRING, "corpo-da-mensagem", "Informe o CONTEUDO desejado.", true),
                Commands.slash("msgparticipacao", "Define a mensagem de confirmação da participação do usuário na comunidade.")
                        .addOption(OptionType.STRING, "txt-da-mensagem", "Informe o texto para a mensagem.", true),

                // Adiciona comandos diversos
                Commands.slash("clear",  "Apaga as mensagens do canal onde foi digitado o comando."),
                Commands.slash("config", "Apresenta os valores da configuração atual no Servidor aos gerentes do Servidor."),
                Commands.slash("nomeservidor", "Atualiza o nome do Servidor na Base de Dados ."),
                Commands.slash("donoservidor", "Atualiza o dono do Servidor na Base de Dados .")
                        .addOption(OptionType.STRING, "email-do-usuario", "Informe uma conta de e-mail válida do dono do Servidor.", true)
                        .addOption(OptionType.STRING, "senha-do-usuario", "Informe uma senha forte (minimo: 8 caracteres, uma maiuscula, um numero e um simbolo).", true),

                // Configura as informacoes para o Cadastro:
                Commands.slash("habilitacadastro", "Habilita a Funcao de Cadastro de usuarios."),
                Commands.slash("desligarcadastro", "Desativa a Funcao de Cadastro de usuarios."),
                Commands.slash("helpcadastro", "URL para a página de EXPLICACAO do Cadastro.")
                            .addOption(OptionType.STRING, "url-da-explicacao", "Informe a URL desejada.", true)
                    
            ).queue();

            // ADICIONA os LISTENERS:
            jdaCliente.addEventListener(new ComandosBarraEventListener(servidoresDatabse, usersService, mailService, i18svc, baseUrl));
            jdaCliente.addEventListener(new MemberWinRole  (servidoresDatabse)                                       );
            
            jdaCliente.awaitReady();
        } catch (InterruptedException e) {
            logger.info("NÃO foi possível iniciar o cliente: " + e.getMessage());
        }
        return jdaCliente; 
    }

    @PreDestroy
    public void onShutdown() {
        if (jdaCliente != null) {
            logger.info("Encerrando JDA antes do shutdown do Spring...");
            jdaCliente.shutdown(); // Encerra o bot e as threads internas do OkHttp
        }
    }
    
    @PreDestroy
    public void cleanup() {
        try {
            System.out.println("Encerrando AbandonedConnectionCleanupThread do MySQL...");
            com.mysql.cj.jdbc.AbandonedConnectionCleanupThread.checkedShutdown();
        } catch (Exception e) {
            logger.warn("Limpeza das conexoes do banco falhou: " + e.getMessage());
        }
    }
    
}
/*                    End of Class                                            */