/**
 * Projeto:  CachaBot - BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2024
 * Equipe:   Murilo, Victor, Allan
 */
package br.com.aeroceti.cachaBot.comandos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.dv8tion.jda.api.entities.Role;
import br.com.aeroceti.cachaBot.entidades.Servidor;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import br.com.aeroceti.cachaBot.repositorios.ServidoresRepository;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * CLASSE de Tratamento dos COMANDO de BARRA relativos aos diversos comandos do Bot
 * 
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
public class ComandosDiversos {

    private final ServidoresRepository    serverDatabse;    
    private final Logger logger = LoggerFactory.getLogger(ComandosDiversos.class);     

    public ComandosDiversos(ServidoresRepository rep) {
        this.serverDatabse = rep;
    }

    public void setMensagemParticipacao(SlashCommandInteractionEvent event, Servidor server) {
        if ( temPermissao(event, server.getCargoSuporte()) ) {
            String idCargo;
            idCargo = event.getOption("txt-da-mensagem", OptionMapping::getAsString);
            logger.info("Configurando a Mensagem de Paricipacao: {}", idCargo);
            server.setMsgParticipacao(idCargo);
            serverDatabse.save(server);
            event.reply("Configurada a mensagem de participacao: " + idCargo).setEphemeral(true).queue();
        } else {
            logger.info("Usuario(" + event.getMember().getEffectiveName() +") não tem permissão para mudar a Mensagem de PARTICIPACAO do Servidor!" );
            event.reply("Você não tem permissão para usar este comando.").setEphemeral(true).queue();
        }
    }

    public void setSalaBoasVindas(SlashCommandInteractionEvent event, Servidor server) {
        if ( temPermissao(event, server.getCargoSuporte()) ) {
            String idCargo;
            idCargo = event.getOption("sala-boas-vindas", OptionMapping::getAsString);
            logger.info("Configurando a Sala de Boas Vindas: {}", idCargo);
            server.setSalaBoasVindas(idCargo);
            serverDatabse.save(server);
            event.reply("Sala de Boas Vindas: " + idCargo).setEphemeral(true).queue();
        } else {
            logger.info("Usuario(" + event.getMember().getEffectiveName() +") não tem permissão para mudar a SALA de Boas Vindas do Servidor!" );
            event.reply("Você não tem permissão para usar este comando.").setEphemeral(true).queue();
        }
    }

    public void setNomeServidor(Servidor server, SlashCommandInteractionEvent event) {
        if ( temPermissao(event, server.getCargoSuporte()) ) {
            logger.info("Atualizando o Nome do Servidor...");
            server.setNome( event.getGuild().getName() );
            serverDatabse.save(server);
            event.reply("Nome atualizado para **" + server.getNome() + "** na Base de Dados").setEphemeral(true).queue();
        } else {
            logger.info("Usuario(" + event.getMember().getEffectiveName() +") não tem permissão para mudar o NOME do Servidor!" );
            event.reply("Você não tem permissão para usar este comando.").setEphemeral(true).queue();
        }
    }

    public void setCargoSuporte(SlashCommandInteractionEvent event, Servidor server) {
        if ( temPermissao(event, server.getCargoSuporte()) ) {
            String idCargo;
            idCargo = event.getOption("id-do-cargo", OptionMapping::getAsString);
            logger.info("Configurando o Cargo de Suporte Tecnico: {}", idCargo);
            server.setCargoSuporte(idCargo);
            serverDatabse.save(server);
            event.reply("Novo Cargo do Suporte Tecnico: " + idCargo).setEphemeral(true).queue();
        } else {
            logger.info("Usuario(" + event.getMember().getEffectiveName() +") não tem permissão para mudar o Cargo SUPORTE TECNICO no Servidor!" );
            event.reply("Você não tem permissão para usar este comando.").setEphemeral(true).queue();
        }
    }

    public void setCargoDefinitivo(SlashCommandInteractionEvent event, Servidor server) {
        if ( temPermissao(event, server.getCargoSuporte()) ) {
            String idCargo;
            idCargo = event.getOption("id-do-cargo", OptionMapping::getAsString);
            logger.info("Configurando o Cargo Definitivo: {}", idCargo);
            server.setCargoDefinitivo(idCargo);
            serverDatabse.save(server);
            event.reply("Novo Cargo Padrao: " + idCargo).setEphemeral(true).queue();
        } else {
            logger.info("Usuario(" + event.getMember().getEffectiveName() +") não tem permissão para mudar o Cargo DEFINITIVO no Servidor!" );
            event.reply("Você não tem permissão para usar este comando.").setEphemeral(true).queue();
        }
    }

    public void setCargoProvisorio(SlashCommandInteractionEvent event, Servidor server) {
        if ( temPermissao(event, server.getCargoSuporte()) ) {
            String idCargo;
            idCargo = event.getOption("id-do-cargo", OptionMapping::getAsString);
            logger.info("Configurando o Cargo Provisorio: {}", idCargo);
            server.setCargoPadrao(idCargo.trim());
            serverDatabse.save(server);
            event.reply("Configurando o Cargo Provisório: " + idCargo).setEphemeral(true).queue();
        } else {
            logger.info("Usuario(" + event.getMember().getEffectiveName() +") não tem permissão para mudar o Cargo PROVISORIO no Servidor!" );
            event.reply("Você não tem permissão para usar este comando.").setEphemeral(true).queue();
        }
    }

    public void listarCargos(SlashCommandInteractionEvent event, Servidor server) {
        int contador;
        StringBuilder sb = new StringBuilder();
        logger.info("Preparando a listagem com os cargos do servidor ...  " );
        contador = 0;
        for (Role cargo : event.getGuild().getRoles() ) {
            sb.append("Cargo: ").append(cargo.getName()).append(" - ID: ").append(cargo.getId()).append("\n" );
            contador++;
        }
        logger.info("Listados todos os "+ contador + " cargos do Servidor.");
        sb.append("\n").append("Total de cargos configurados: ").append(contador).append("\n\n");
        event.reply( sb.toString() ).setEphemeral(true).queue();
    }

    /**
     * Verifica se o usuario que executou o comando eh o dono do servidor.
     * 
     * @param guild  - Servidor enviado pelo Discord
     * @param member - Usuario que esta executando o comando
     * @return Verdadeiro se o usuario eh o dono do servidor e Falso caso contrario.
     */
    private boolean isOwner(Guild guild, Member member) {
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
    private boolean temPermissao(SlashCommandInteractionEvent event, String cargoIdSuporte) {
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