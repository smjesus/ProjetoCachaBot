/**
 * Projeto:  CachaBot - BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2024
 * Equipe:   Murilo, Victor, Allan
 */
package br.com.aeroceti.cachaBot.componentes;

import org.slf4j.Logger;
import java.util.Optional;
import org.slf4j.LoggerFactory;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.entities.UserSnowflake;
import br.com.aeroceti.cachaBot.entidades.Servidor;
import br.com.aeroceti.cachaBot.servicos.I18nService;
import br.com.aeroceti.cachaBot.comandos.MensagemEmbebed;
import br.com.aeroceti.cachaBot.comandos.ComandosDiversos;
import br.com.aeroceti.cachaBot.servicos.MailSenderService;
import br.com.aeroceti.cachaBot.repositorios.ServidoresRepository;
import br.com.aeroceti.cachaBot.servicos.ColaboradoresService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

/**
 * Classe de Listener de Comandos de Barra do Discord4J.
 * 
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
public class ComandosBarraEventListener extends ListenerAdapter{
    
    private final String baseUrl;
    private final I18nService i18svc;
    private final ServidoresRepository    servidoresDatabse;
    private final ColaboradoresService    usersService;   
    private final MailSenderService       mailService;
    
    private final Logger logger = LoggerFactory.getLogger(ComandosBarraEventListener.class);    

    public ComandosBarraEventListener(ServidoresRepository  sdb, ColaboradoresService cdb,MailSenderService msv, I18nService i18servico, String url) {
        this.baseUrl = url;
        this.i18svc = i18servico;
        this.mailService = msv;
        this.usersService = cdb;
        this.servidoresDatabse = sdb;
    }
    
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {        
        try {
            MensagemEmbebed  embebed = new MensagemEmbebed(this.i18svc, this.servidoresDatabse, this.usersService, this.mailService, this.baseUrl);
            ComandosDiversos command = new ComandosDiversos(this.servidoresDatabse);
            if (event.isFromGuild()) {
                Optional servidorProcurado = servidoresDatabse.findByServidorID( event.getGuild().getIdLong());
                if( servidorProcurado.isPresent() ) {
                    Servidor server = (Servidor) servidorProcurado.get();
                    logger.info("Tratando o Comando (" + event.getName() + ") para o servidor " + server.getNome() );
                    switch ( event.getName().trim() ) {
                        // COMANDOS RELATIVOS A MENSAGEM 
                        case "boasvindas" -> { 
                            embebed.imprimirBoasVindas(event, server);
                        }
                        case "ajuda" -> {
                            embebed.imprimirAjuda(event, server) ;
                        }
                        case "clear" -> {
                            embebed.apagarMensagens(event);
                        }
                        case "donoservidor" -> {
                            embebed.donoServidor(event, server);
                        }
                        case "config" -> {
                            embebed.mostrarConfiguracao(server, event);
                        }
                        case "msgtitulo" -> {
                            embebed.setTituloMensagem(event, server);
                        } 
                        case "msgcor" -> {
                            embebed.setCorMensagem(event, server);
                        }   
                        case "msgcorpo" -> {
                            embebed.setCorpoMensagem(event, server);
                        }
                        case "msglinha" -> {
                            embebed.setLinhaMensagem(event, server);
                        }                    
                        case "msgicone" -> {
                            embebed.setIconeMensagem(event, server);
                        }  
                        case "helpcadastro" -> {
                            embebed.setURLHelpCadastro(event, server);
                        }
                        case "habilitacadastro" -> {
                            embebed.mudarStatusCadastro(event, server, true);
                        }    
                        case "desligarcadastro" -> {
                            embebed.mudarStatusCadastro(event, server, false);
                        }
                        // COMANDOS DIVERSOS
                        case "listarcargos" -> {
                            command.listarCargos(event, server);
                        }
                        case "cargoprovisorio" -> {
                            command.setCargoProvisorio(event, server);
                        }
                        case "cargodefinitivo" -> {
                            command.setCargoDefinitivo(event, server);
                        }
                        case "cargosuporte" -> {
                            command.setCargoSuporte(event, server);
                        }
                        case "nomeservidor" -> {
                            command.setNomeServidor(server, event);
                        }
                        case "salaboasvindas" -> {
                            command.setSalaBoasVindas(event, server);
                        }
                        case "msgparticipacao" -> {
                            command.setMensagemParticipacao(event, server);
                        }
                    default -> {
                            // Executado se nenhuma das opções for atendida
                            logger.info("Comando desconhecido.");
                            event.reply("Comando desconhecido. Verifique e tente novamente.").setEphemeral(true).queue();
                        }
                    }
                } else {
                    logger.info("O Servidor ({}), pertencente a {}, não está cadastrado no sistema!", event.getGuild().getName(), event.getGuild().getOwner().getEffectiveName());
                    event.reply("Não conseguiu definir o Servidor em uso! Entre em contato com o Administrador do BOT. ").setEphemeral(true).queue();
                }   
            } else {
                logger.info("Comando executado fora de um servidor ... abortando execução!" );
                event.reply("Comando não permitido em Mensagem Direta (DM). ").setEphemeral(true).queue();             }
        } catch (Exception e) {
            logger.info("Falha na execucao do comando: " + e.getMessage() );
            event.reply("Erro na execução do comando!! Tente novamente depois. ").setEphemeral(true).queue();
        }
    } 

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        //CONFIRMA A PARTICIPACAO e atribui o cargo definitivo do usuario:
        if( event.getButton().getId().equals("botao-participar") ) {
            try {
                logger.info("O usuario " + event.getMember().getEffectiveName() + " aceitou PARTICIPAR da comunidade!");
                Optional servidorProcurado = servidoresDatabse.findByServidorID(event.getGuild().getIdLong() );
                if( servidorProcurado.isPresent() ) {
                    logger.info("Obtendo o cargo definitivao para o usuario (" + event.getMember().getEffectiveName() + ") ...");
                    Servidor server = (Servidor) servidorProcurado.get();
                    event.getGuild().addRoleToMember(UserSnowflake.fromId(event.getMember().getIdLong()), event.getGuild().getRoleById(server.getCargoDefinitivo()) ).queue();
                    event.reply(server.getMsgParticipacao()).setEphemeral(true).queue();
                }
            } catch (Exception e) {
                logger.info("Falha na execucao do comando: " + e.getMessage() );
                event.reply("Opa!! Desculpe, algo deu errado!! Tente novamente depois, por favor. ").setEphemeral(true).queue();
            }
        }
        //ABRE O FORMULARIO DE CADASTRO:
        if( event.getButton().getId().equals("botao-cadastrar") ) {
            try {
                logger.info("Apresentando o Formulario de CADASTRO.");
                Optional servidorProcurado = servidoresDatabse.findByServidorID(event.getGuild().getIdLong() );
                if( servidorProcurado.isPresent() ) {
                    Servidor server = (Servidor) servidorProcurado.get();
//                    FormularioCadastro form = new FormularioCadastro();
//  TO-DO
//                    event.replyModal(form.getFormCadastro() ).queue();
                }
            } catch (Exception e) {
                logger.info("Falha na execucao do comando: " + e.getMessage() );
                event.reply("Opa!! Desculpe, algo deu errado!! Tente novamente depois, por favor. ").setEphemeral(true).queue();
            }

        }

    }

}
/*                    End of Class                                            */