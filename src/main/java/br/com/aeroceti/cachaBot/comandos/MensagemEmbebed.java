/**
 * Projeto:  CachaBot - BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2024
 * Equipe:   Murilo, Victor, Allan
 */
package br.com.aeroceti.cachaBot.comandos;

import java.util.List;
import java.awt.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.StringTokenizer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import br.com.aeroceti.cachaBot.entidades.Servidor;
import br.com.aeroceti.cachaBot.entidades.Colaborador;
import br.com.aeroceti.cachaBot.entidades.UsuarioVerificador;
import br.com.aeroceti.cachaBot.entidades.dto.EmailMessageDTO;
import br.com.aeroceti.cachaBot.repositorios.ServidoresRepository;
import br.com.aeroceti.cachaBot.servicos.ColaboradoresService;
import br.com.aeroceti.cachaBot.servicos.I18nService;
import br.com.aeroceti.cachaBot.servicos.MailSenderService;
import jakarta.mail.MessagingException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.transaction.annotation.Transactional;

/**
 * CLASSE de Tratamento dos COMANDO de BARRA relativos as Mensagens Embebed
 * 
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
public class MensagemEmbebed {

    private final String baseUrl;
    private final I18nService             i18svc;
    private final ServidoresRepository    serverDatabse;    
    private final ColaboradoresService    usersService;   
    private final MailSenderService       mailService;
    
    private final Logger logger = LoggerFactory.getLogger(MensagemEmbebed.class);    

    public MensagemEmbebed(I18nService i18svc, ServidoresRepository srvDatabse, ColaboradoresService usersSVC, MailSenderService mailSVC, String url) {
        this.i18svc = i18svc;
        this.baseUrl = url;
        this.mailService = mailSVC;
        this.usersService = usersSVC;
        this.serverDatabse = srvDatabse;
    }

    /**
     * Apresenta as configuracoes registradas para o Servidor no Banco de Dados
     * 
     * @param server - Servidor para o qual sera mostrado as configuracoes
     * @param event  - Evento do discord que disparou essa chamada
     */
    public void mostrarConfiguracao(Servidor server, SlashCommandInteractionEvent event) {

        if ( temPermissao(event, server.getCargoSuporte()) ) {
            logger.info("Apresentando os valores da configuração atual no Servidor..." );
            try {
                EmbedBuilder eb = new EmbedBuilder();
                StringBuilder sbmsg = new StringBuilder();
                eb.setTitle("CONFIGURAÇÕES NO CachaBOT");
                eb.setImage(server.getBoasVindas().getLinha().toLowerCase().trim());
                eb.setThumbnail(server.getBoasVindas().getIcone());
                
                // Formata o conteudo:
                sbmsg.append("Nome do Servidor ..........: ").append(server.getNome()).append("\n");
                var donoServer = (server.getColaborador() != null ) ? server.getColaborador().getNomePessoal() : " ";
                sbmsg.append("Dono do Servidor ..........: ").append(donoServer).append("\n");
                sbmsg.append("Cargo Provisório...........: ").append(server.getCargoPadrao()).append("\n");
                sbmsg.append("Cargo Definitivo...........: ").append(server.getCargoDefinitivo()).append("\n");
                sbmsg.append("Cargo SUPORTE .............: ").append(server.getCargoSuporte()).append("\n");
                sbmsg.append("Sala de Boas Vindas .......: ").append(server.getSalaBoasVindas()).append("\n");
                sbmsg.append("Serviço de CADASTRO .......: ").append( (server.isCadastro() ? "ATIVADO" : "DESATIVADO") ).append("\n");
                sbmsg.append("Site de ajuda do Cadastro .: ").append(server.getHelpCadastroPage()).append("\n");
                // Atribui a Descricao com o conteudo acima:
                eb.setDescription(sbmsg );
                
                // Configura a COR:
                switch (server.getBoasVindas().getCor()) {
                    case "VERDE"   -> eb.setColor(Color.GREEN);
                    case "AMARELO" -> eb.setColor(Color.YELLOW);
                    case "AZUL"    -> eb.setColor(Color.BLUE);
                    case "LARANJA" -> eb.setColor(Color.ORANGE);
                    default        -> eb.setColor(Color.WHITE);
                }                
                
                // Envia a Mensagem Embed:
                event.getChannel()
                        .sendMessageEmbeds(eb.build())
                        .queue();
                
                logger.info("Enviando as configuracoes ao usuario ... " );
                event.reply("Configurações listadas com sucesso!").setEphemeral(true).queue();
            } catch (Exception e) {
                logger.info("Erro na preparacao da mensagem das configurações: " + e.getMessage() );
                event.reply("Erro na preparacao da mensagem das configurações: " + e.getMessage() ).setEphemeral(true).queue();
            }
        } else {
            logger.info("Usuario(" + event.getMember().getEffectiveName() +") não tem permissão para ver as configuracoes do Servidor!" );
            event.reply("Você não tem permissão para usar este comando.").setEphemeral(true).queue();
        }
    }

    /**
     * Cadastra o dono do servidor na base de dados
     * 
     * @param event  - Evento gerado pelo Discord
     * @param server - Servidor enviado pelo Discord
     */
    @Transactional
    public void donoServidor(SlashCommandInteractionEvent event, Servidor server) {
        // Valida a senha fornecida:
        String REGEX_SENHA_FORTE = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9\\s]).{8,}$";
        String novaSenha = event.getOption("senha-do-usuario", OptionMapping::getAsString);
        if( novaSenha.isBlank() || novaSenha.isEmpty() || novaSenha == null ) {
            logger.info("Erro no Processamento:  Senha em branco ou nula." );
            event.reply("Ops!! precisa fornecer uma senha pra sua conta que será criada!!!").setEphemeral(true).queue();
        } else {
            Pattern pattern = Pattern.compile(REGEX_SENHA_FORTE);
            Matcher matcher = pattern.matcher(novaSenha);
            if( matcher.matches() ) {
                String contaEmail = event.getOption("email-do-usuario", OptionMapping::getAsString);
                Member member = event.getMember();
                Servidor serverDiscord = (Servidor) serverDatabse.findByEntidadeID(server.getEntidadeID()).get();
                Colaborador owner;
                if( isOwner( event.getGuild(), member ) ) {
                    if( serverDiscord.getColaborador() != null ) {
                        logger.info("Atualizando o dono do servidor ja cadastrado ..." );
                        owner = (Colaborador) serverDiscord.getColaborador();
                    } else {
                        logger.info("Criando um novo dono na Base de Dados ..." );
                        owner = new Colaborador(); owner.setEntidadeID(null);
                    }
                    owner.setAtivo(false);
                    owner.setNomePessoal(member.getEffectiveName());
                    owner.setContaEmail(contaEmail);
                    owner.setCodigoAcesso(novaSenha.trim());
                    owner.setConfirmarSenha(novaSenha.trim());
                    // salva o dono do servidor
                    usersService.atualizar(owner); 
                    // cria o token para validar a conta:
                    UsuarioVerificador verificador;
                    Optional<UsuarioVerificador> userVerificador = usersService.buscarVerificador(owner.getEntidadeID());
                    if( userVerificador.isPresent() ) {
                        logger.info("Encontrou um Token, validando por mais 25 minutos... " );
                        verificador = userVerificador.get();
                    } else {
                        logger.info("Criando um novo Token para o usuario ... " );
                        verificador = new UsuarioVerificador();
                        verificador.setVerificadorID(null);
                    }
                    verificador.setUsuario(owner);
                    verificador.setCodigoUUID(UUID.randomUUID());
                    verificador.setValidade(Instant.now().plusMillis(1500000));
                    usersService.atualizarVerificador(verificador);
                    // prepara o e-mail para ativar a conta:
                    String corpoMensagem = "<html><head><title>Ativação de Conta no CachaBOT</title></head><body><BR>".concat("<img width='10%' src='").concat(baseUrl)
                                           .concat("/assets/cacha_logo.png' alt='CACHABOT'/><br>").concat("<h2>Olá ").concat( owner.getNomePessoal() ).trim().concat("!</h2><BR>");
                    corpoMensagem = corpoMensagem.concat("<p>Para ativar sua conta, clique no link abaixo: ")
                                                 .concat("<BR><a href='").concat(baseUrl).concat("/usuario/uuid/").concat(verificador.getCodigoUUID().toString()).concat("' target='_blank'>Clique aqui</a><BR>").concat("</p>")
                                                 .concat("<HR><h5><i>Mensagem automatica do CachaBOT - Não responder.</i></h5>")
                                                 .concat("</body></html>");
                    EmailMessageDTO mensagem = new EmailMessageDTO(owner.getContaEmail(), null, owner.getNomePessoal(), "Ativação de Conta no CachaBOT", corpoMensagem);
                    // Atualiza o Servidor
                    serverDiscord.setColaborador(owner);
                    serverDatabse.save(serverDiscord);
                    // Envia o email numa thread:
                    event.deferReply(true).queue();
                    CompletableFuture.runAsync(() -> {
                        try{ 
                            mailService.sendHtmlEmail(mensagem);
                            logger.info("Enviado email para ativacao da conta! Atualizacao concuída. " );
                        } catch(MessagingException ex) {
                            logger.info("Encontrou no envio do email: " + ex.getMessage());
                        }                
                    });
                    logger.info("Colaborador atualizado na Base de Dados!" );
                    event.getHook().sendMessage( serverDiscord.getColaborador().getNomePessoal() + " está como dono do Servidor (" + serverDiscord.getNome() + ") na Base de Dados! Para validar a conta veja no email informado.").setEphemeral(true).queue();
                } else {
                    logger.info("Usuario(" + event.getMember().getEffectiveName() +") não tem permissão para modificar o Dono do Servidor!" );
                    event.reply("Você não tem permissão para usar este comando.").setEphemeral(true).queue();
                }
            } else {
                logger.info("Erro no processamento:  Senha não atende os requisitos de segurança!" );
                event.reply("Ops!! Senha inválida, deve conter no mínimo uma maiuscula, uma minuscula, um número e um simbolo, além de no mínimo 8 caracteres!!!").setEphemeral(true).queue();
            }
        }
    }

    /**
     * Apresenta a mensagem de Boas Vindas na sala especificada.
     * 
     * @param event  - Evento do Discord que disparou o comando
     * @param server - Servidor do Discord que originou o comando
     */
    public void imprimirBoasVindas(SlashCommandInteractionEvent event, Servidor server) {
        logger.info("Apresentando os valores da configuração atual no Servidor..." );
        String cargoId = server.getCargoSuporte();

        if ( temPermissao(event, cargoId) ) {
            try {
                String salaBoasVindas;
                EmbedBuilder eb = new EmbedBuilder();
                StringBuilder sbmsg = new StringBuilder();
                eb.setTitle(server.getBoasVindas().getTitulo());
                eb.setImage(server.getBoasVindas().getLinha().toLowerCase().trim());
                eb.setThumbnail(server.getBoasVindas().getIcone());

                salaBoasVindas = server.getSalaBoasVindas().equals("Sala-Boas-Vindas" ) ? event.getChannel().getId() : server.getSalaBoasVindas().trim();
                logger.info("Sala de Boas Vindas definida para Sala ID: " +  salaBoasVindas);

                // Formata o conteudo:
                StringTokenizer tokenizer = new StringTokenizer(server.getBoasVindas().getCorpo(), "||");
                while(tokenizer.hasMoreTokens()) {
                    sbmsg.append(tokenizer.nextToken());
                    sbmsg.append("\n");
                }
                eb.setDescription(sbmsg );

                // Configura a COR:
                switch (server.getBoasVindas().getCor()) {
                    case "VERDE"   -> eb.setColor(Color.GREEN);
                    case "AMARELO" -> eb.setColor(Color.YELLOW);
                    case "AZUL"    -> eb.setColor(Color.BLUE);
                    case "LARANJA" -> eb.setColor(Color.ORANGE);
                    default        -> eb.setColor(Color.WHITE);
                }

                // Prepara o(s) Botao(oes) de PARTICIPACAO:
                Button botaoParticipe  = Button.primary  ("botao-participar", "Confirmar sua Participação");
                logger.info("Adicioando os BOTOES na mensagem ... " );
                // Envia a Mensagem Embed:
                if( server.isCadastro() ) {
                    Button botaoExplicacao = Button.link( server.getHelpCadastroPage(), "  O que é o Cadastro da Comunidade?  ");
                    // Button botaoCadastrar  = Button.success  ("botao-cadastrar", "Realizar seu cadastro");
                    event.getGuild().getTextChannelById(salaBoasVindas).sendMessageEmbeds(eb.build())
                            .addActionRow(botaoParticipe,botaoExplicacao )
                            .queue();
                } else {
                    event.getGuild().getTextChannelById(salaBoasVindas).sendMessageEmbeds(eb.build())
                            .setActionRow( botaoParticipe )
                            .queue();
                }
                logger.info("Apresentando a mensagem de Boas Vindas no canal ... " );
                event.reply("Mensagem de Boas Vindas criada com sucesso!").setEphemeral(true).queue();
            } catch (Exception e) {
                logger.info("Erro na preparacao da mensagem: " + e.getMessage() );
                event.reply("Erro na preparacao da mensagem: " + e.getMessage() ).setEphemeral(true).queue();
            }
        } else {
            logger.info("Usuario(" + event.getMember().getEffectiveName() +") não tem permissão para imprimir a Mensagem de Boas Vindas no Servidor!" );
            event.reply("Você não tem permissão para usar este comando.").setEphemeral(true).queue();
        }
    }

    /**
     * Apresenta a mensagem de Ajuda ao usuario.
     * 
     * @param event  - Evento gerado pelo Discord
     * @param server - Servidor enviado pelo Discord
     */
    public void imprimirAjuda(SlashCommandInteractionEvent event, Servidor server) {
        String mensagem_i18n;
        StringBuilder sbmsg = new StringBuilder();
        logger.info("Apresentando Help ao usuario... " );
        EmbedBuilder eb = new EmbedBuilder();
        mensagem_i18n = i18svc.buscarMensagem("help_embed.titulo", event.getUserLocale().toLocale());
        eb.setTitle(mensagem_i18n);
        mensagem_i18n = i18svc.buscarMensagem("help_embed.descricao", event.getUserLocale().toLocale());
        // Formata o conteudo:
        StringTokenizer tokenizer = new StringTokenizer(mensagem_i18n, "||");
        while(tokenizer.hasMoreTokens()) {
            sbmsg.append(tokenizer.nextToken());
            sbmsg.append("\n");
        }
        eb.setDescription(sbmsg );
        // Configura a COR:
        switch (server.getBoasVindas().getCor()) {
            case "VERDE" -> eb.setColor(Color.GREEN);
            case "AMARELO" -> eb.setColor(Color.YELLOW);
            case "AZUL" -> eb.setColor(Color.BLUE);
            case "LARANJA" -> eb.setColor(Color.ORANGE);
            default -> eb.setColor(Color.WHITE);
        }
        // Envia a Mensagem Embed:
        event.replyEmbeds( eb.build() ).setEphemeral(true).queue();
    }
    
    /**
     * Deleta as mensagens de um canal de texto.
     * 
     * @param event - Evento gerado pelo discord. 
     */
    public void apagarMensagens(SlashCommandInteractionEvent event) {
        int contador;
        logger.info("Executando comando '"  + event.getName().toUpperCase() + "' ... Excluindo mensagens ...");
        contador = 0;
        List<Message> listagem = event.getChannel().getIterableHistory().complete();
        for( Message mensagem:  listagem ) {
            try {
                event.getChannel().deleteMessageById(mensagem.getId()).queue();
                contador++; 
            } catch (Exception e) {
                logger.info("Mensagem nao excluida: {}", e.getMessage());
            }
        }
        event.reply("Mensagens apagadas: " + contador).setEphemeral(true).queue();
    }
    
    public void setTituloMensagem(SlashCommandInteractionEvent event, Servidor server) {
        if( temPermissao(event, server.getCargoSuporte())){
            logger.info("Configurando o Titulo da mensagem de Boas Vindas do servidor... " );
            String parametro = event.getOption("titulo-da-mensagem", OptionMapping::getAsString);
            logger.info("Parametro recebido para o Titulo: " + parametro );
            server.getBoasVindas().setTitulo(parametro);
            serverDatabse.save(server);
            event.reply("Titulo da mensagem definido para: " + parametro).setEphemeral(true).queue();
        } else {
            logger.info("Usuario(" + event.getMember().getEffectiveName() +") não tem permissão para definir TITURLO de Boas Vindas! " );
            event.reply("Você não tem permissão para definir Titulo de Boas Vindas! ").setEphemeral(true).queue();
        }
    }
    
    public void setCorMensagem(SlashCommandInteractionEvent event, Servidor server) {
        if( temPermissao(event, server.getCargoSuporte())){
            String resposta;
            logger.info("Configurando a COR da mensagem de Boas Vindas do servidor... " );
            String parametro = event.getOption("cor-da-mensagem", OptionMapping::getAsString).trim().toUpperCase();
            logger.info("Parametro recebido para a COR: " + parametro );
            if( "VERDE AMARELO AZUL LARANJA".contains(parametro.trim() ) ) {
                server.getBoasVindas().setCor(parametro);
                resposta = "COR da mensagem definido para: " + parametro ;
            } else {
                resposta = "Desculpe!! Por enquanto selecione AMARELO, AZUL, LARANJA ou VERDE. ";
            }
            serverDatabse.save(server);
            logger.info("Informando ao usuario: " + resposta );
            event.reply( resposta ).setEphemeral(true).queue();
        } else {
            logger.info("Usuario(" + event.getMember().getEffectiveName() +") não tem permissão para definir COR da mensagem de Boas Vindas! " );
            event.reply("Você não tem permissão para definir COR da mensagem de Boas Vindas! ").setEphemeral(true).queue();
        }
    }

    public void setCorpoMensagem(SlashCommandInteractionEvent event, Servidor server) {
        if( temPermissao(event, server.getCargoSuporte())){
            String parametro;
            logger.info("Configurando o Conteudo da mensagem de Boas Vindas do servidor... " );
            parametro = event.getOption("corpo-da-mensagem", OptionMapping::getAsString);
            logger.info("Parametro recebido para o Conteudo: " + parametro );
            server.getBoasVindas().setCorpo(parametro);
            serverDatabse.save(server);
            event.reply("Conteudo da mensagem definido para: " + parametro).setEphemeral(true).queue();
        } else {
            logger.info("Usuario(" + event.getMember().getEffectiveName() +") não tem permissão para definir CONTEUDO da mensagem de Boas Vindas! " );
            event.reply("Você não tem permissão para definir o CONTEUDO da mensagem de Boas Vindas! ").setEphemeral(true).queue();
        }
    }

    public void setURLHelpCadastro(SlashCommandInteractionEvent event, Servidor server) {
        if ( temPermissao(event, server.getCargoSuporte()) ) {
            String parametro;
            logger.info("Configurando a URL da Página de Explicacao do Cadastro" );
            parametro = event.getOption("url-da-explicacao", OptionMapping::getAsString);
            server.setHelpCadastroPage(parametro.toLowerCase().trim());
            serverDatabse.save(server);
            event.reply("URL da Explicacao definido para: " + server.getHelpCadastroPage()).setEphemeral(true).queue();
        } else {
            logger.info("Usuario(" + event.getMember().getEffectiveName() +") não tem permissão para mudar a URL do HELP de Cadastro!" );
            event.reply("Você não tem permissão para usar este comando.").setEphemeral(true).queue();
        }
    }
    
    public void mudarStatusCadastro(SlashCommandInteractionEvent event, Servidor server, boolean status) {
        if ( temPermissao(event, server.getCargoSuporte()) ) {
            server.setCadastro(status);
            serverDatabse.save(server);
            logger.info("Funcao de Cadastro do servidor " + (status ? "HABILITADA." : "DESABILITADA.") );
            event.reply("Funcao de Cadastro do servidor " + (status ? "HABILITADA." : "DESABILITADA.") ).setEphemeral(true).queue();
        } else {
            logger.info("Usuario(" + event.getMember().getEffectiveName() +") não tem permissão para mudar o STATUS da Função Cadastro do Servidor!" );
            event.reply("Você não tem permissão para usar este comando.").setEphemeral(true).queue();
        }
    }

    public void setIconeMensagem(SlashCommandInteractionEvent event, Servidor server) {
        if ( temPermissao(event, server.getCargoSuporte()) ) {
            logger.info("Configurando o ICONE da mensagem de Boas Vindas do servidor... " );
            String parametro = event.getOption("icone-da-mensagem", OptionMapping::getAsString);
            logger.info("Parametro recebido para o ICONE: " + parametro );
            server.getBoasVindas().setIcone(parametro);
            serverDatabse.save(server);
            event.reply("ICONE da mensagem definido para: " + parametro).setEphemeral(true).queue();
        } else {
            logger.info("Usuario(" + event.getMember().getEffectiveName() +") não tem permissão para mudar o ICONE da Mensagem de Boas Vindas!" );
            event.reply("Você não tem permissão para usar este comando.").setEphemeral(true).queue();
        }
    }

    public void setLinhaMensagem(SlashCommandInteractionEvent event, Servidor server) {
        if ( temPermissao(event, server.getCargoSuporte()) ) {
            logger.info("Configurando a BARRA da mensagem de Boas Vindas do servidor... " );
            String parametro = event.getOption("linha-da-mensagem", OptionMapping::getAsString);
            logger.info("Parametro recebido para a BARRA: " + parametro );
            server.getBoasVindas().setLinha(parametro);
            serverDatabse.save(server);
            event.reply("BARRA da mensagem definido para: " + parametro).setEphemeral(true).queue();
        } else {
            logger.info("Usuario(" + event.getMember().getEffectiveName() +") não tem permissão para mudar a LINHA da Mensagem de Boas Vindas!" );
            event.reply("Você não tem permissão para usar este comando.").setEphemeral(true).queue();
        }
    }

    /**
     * Verifica se o usuario que executou o comando eh o dono do servidor.
     * 
     * @param guild  - Servidor enviado pelo Discord
     * @param member - Usuario que esta executando o comando
     * @return Verdadeiro se o usuario eh o dono do servidor e Falso caso contrario.
     */
    public boolean isOwner(Guild guild, Member member) {
        if (guild == null || member == null) {
            return false; 
        }
        // Obtém o ID do proprietário do servidor
        String ownerId = guild.getOwnerId().trim();
        // Compara com o ID do membro
        return ownerId.equals(member.getId().trim());
    }

    /**
     * Verifia se o usuario que executou o comando tem permissao (cargo Suporte) ou se proprietario do Servidor.
     * 
     * @param event           - Evento gerado pelo discord
     * @param cargoIdSuporte  - ID do Cargo de Suporte no Servidor
     * @return VERDADEIRO se for do Suporte ou Proprietário, caso contrario sera FALSO
     */
    public boolean temPermissao(SlashCommandInteractionEvent event, String cargoIdSuporte) {
        Member member = event.getMember();
        boolean proprietario = isOwner( event.getGuild(), member );
        if ( member != null && member.getRoles().stream().anyMatch(r -> r.getId().equals(cargoIdSuporte)) || proprietario ) {
            logger.info("Usuario atual " + (proprietario ? "é o " : "NÃO é ") + " dono do Servidor e está AUTORIZADO!" );
            return true;
        }
        return false;
    }

}
/*                    End of Class                                            */