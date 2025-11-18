/**
 * Projeto:  CachaBot - BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2024
 * Equipe:   Murilo, Victor, Allan
 */
package br.com.aeroceti.cachaBot.comandos;

import org.slf4j.Logger;
import java.util.Optional;
import org.slf4j.LoggerFactory;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import br.com.aeroceti.cachaBot.entidades.Servidor;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import br.com.aeroceti.cachaBot.entidades.MensagemBoasVindas;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import br.com.aeroceti.cachaBot.repositorios.ServidoresRepository;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;

/**
 * EVENTO para mudanca de Cargo no Discord
 * 
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
public class MemberWinRole  extends ListenerAdapter {

    private final ServidoresRepository servidoresDatabse;
    private final Logger logger = LoggerFactory.getLogger(MemberWinRole.class);

    public MemberWinRole( ServidoresRepository rep) {
        this.servidoresDatabse = rep;
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        Servidor server ;
        logger.info("O Bot entrou no Servidor " + event.getGuild().getName() + "(ID: " + event.getGuild().getIdLong() + ")");
        Optional servidorNovo = servidoresDatabse.findByServidorID(event.getGuild().getIdLong() );
        if( servidorNovo.isEmpty() ) {
            // Novo servidor, cadastrar...
            logger.info("Novo servidor! Salvando informacoes ...");
            server = new Servidor();
            server.setEntidadeID(null);
            server.setVersao(0L);
            server.setServidorID( event.getGuild().getIdLong());
            server.setNome( event.getGuild().getName());
            server.setAtivo(true);
            MensagemBoasVindas mensagem = new MensagemBoasVindas(null, "Titulo", "Conteudo da sua Mensagem", 0L);
            mensagem.setIcone("https://aeroceti.com.br/wp-content/uploads/icone-discord.png");
            mensagem.setLinha("https://aeroceti.com.br/wp-content/uploads/linha-divisoria.png");
            server.setBoasVindas(mensagem);
        } else {
            // Servidor ja cadastrado, entao reativa o status
            server = (Servidor) servidorNovo.get();
            server.setAtivo(true);
            logger.info("Servidor já está cadastrado! Atualizando status...");
        }
        servidoresDatabse.save(server);
        int contador = servidoresDatabse.countByAtivo(true);
        event.getJDA().getPresence().setActivity(Activity.customStatus("Ativo em " + contador + " servidores."));
        logger.info("Dados atualizados e CachaBOT ativo em " + contador + " servidores!");
    }

    @Override
    public void onGuildBan(GuildBanEvent event) {
        logger.info("O Bot foi BANIDO do Servidor " + event.getGuild().getName() + "(ID: " + event.getGuild().getIdLong() + ")");
        Optional servidorNovo = servidoresDatabse.findByServidorID(event.getGuild().getIdLong() );
        if( servidorNovo.isPresent() ) {
            Servidor server = (Servidor) servidorNovo.get();
            server.setAtivo(false);
            servidoresDatabse.save(server);
        }
        int contador = servidoresDatabse.countByAtivo(true);
        event.getJDA().getPresence().setActivity(Activity.customStatus("Ativo em " + contador + " servidores."));
        logger.info("Status do CachaBOT atualizado para " + contador + " servidores ativos.");
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        logger.info("O Bot saiu do Servidor " + event.getGuild().getName() + "(ID: " + event.getGuild().getIdLong() + ")");
        Optional servidorNovo = servidoresDatabse.findByServidorID(event.getGuild().getIdLong() );
        if( servidorNovo.isPresent() ) {
            Servidor server = (Servidor) servidorNovo.get();
            server.setAtivo(false);
            servidoresDatabse.save(server);
        }
        int contador = servidoresDatabse.countByAtivo(true);
        event.getJDA().getPresence().setActivity(Activity.customStatus("Ativo em " + contador + " servidores."));
        logger.info("Status do CachaBOT atualizado para " + contador + " servidores ativos.");
    }
    
//    @Override
//    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
//        logger.info("O usuario " + event.getMember().getEffectiveName() + " entrou no Servidor ");
//        Optional servidorProcurado = servidoresDatabse.findByServidorID(event.getGuild().getIdLong() );
//        if( servidorProcurado.isPresent() ) {
//            Servidor server = (Servidor) servidorProcurado.get();
//            event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(server.getCargoPadrao()) ).queue();
//            logger.info("O Cargo padrao foi atribuido ao usuario.");
//        }
//    }
//    

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        String nomeUsuario = event.getMember().getEffectiveName();
        String nomeServidor = event.getGuild().getName();
        long servidorId = event.getGuild().getIdLong();

        logger.info("Novo membro detectado: '{}' entrou no servidor '{}'", nomeUsuario, nomeServidor);

        Optional<Servidor> servidorProcurado = servidoresDatabse.findByServidorID(servidorId);
        if (servidorProcurado.isEmpty()) {
            logger.warn("Servidor '{}' (ID={}) não encontrado no banco de dados. Nenhum cargo será atribuído.", nomeServidor, servidorId);
            return;
        }

        Servidor servidor = servidorProcurado.get();
        Role cargoPadrao = event.getGuild().getRoleById(servidor.getCargoPadrao());

        if (cargoPadrao == null) {
            logger.error("Cargo padrão inválido (ID={}) configurado para o servidor '{}'.", servidor.getCargoPadrao(), nomeServidor);
            return;
        }

        // 🚀 Tenta atribuir o cargo, com até 3 tentativas
        atribuirCargoComRetry(event, cargoPadrao, 3, 1000);
    }

    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        logger.info("O usuario " + event.getMember().getEffectiveName() + " recebeu o Cargo: " + event.getRoles().toString());
        Optional servidorProcurado = servidoresDatabse.findByServidorID(event.getGuild().getIdLong() );
        if( servidorProcurado.isPresent() ) {
            Servidor server = (Servidor) servidorProcurado.get();
            // Quando receber o CARGODEFINITIVO remover CARGO-TEMPORARIO
            if( event.getRoles().get(0).getId().equalsIgnoreCase( server.getCargoDefinitivo()) ) {
                for (Role role : event.getMember().getRoles()) {
                    if( role.getId().equalsIgnoreCase( server.getCargoPadrao()) ) {
                        logger.info("Removendo cargo temporario (" + role.getName() + ") ...");
                        event.getGuild().removeRoleFromMember(event.getMember(),role).queue();
                    }
                }
            }
        }
    } 
    
    /**
     * Atribui um cargo ao membro, com número limitado de tentativas e backoff exponencial.
     */
    private void atribuirCargoComRetry(GuildMemberJoinEvent event, Role cargo, int tentativasRestantes, long atrasoBaseMs) {
        String nomeUsuario = event.getMember().getEffectiveName();
        String nomeServidor = event.getGuild().getName();

        event.getGuild()
            .addRoleToMember(event.getMember(), cargo)
            .queue(
                sucesso -> logger.info("✅Cargo '{}' atribuído com sucesso ao usuário '{}' no servidor '{}'.",
                                       cargo.getName(), nomeUsuario, nomeServidor),

                erro -> {
                    if (tentativasRestantes > 1) {
                       // int proximaTentativa = (4 - tentativasRestantes) + 1;
                        long proximoAtraso = atrasoBaseMs * 2; // backoff exponencial simples

                        logger.warn("⚠️Falha ao atribuir cargo '{}' ao usuário '{}' ({} tentativas restantes): {}. Tentando novamente em {} ms...",
                                    cargo.getName(), nomeUsuario, tentativasRestantes - 1, erro.getMessage(), proximoAtraso);

                        // agenda a nova tentativa usando o executor interno do JDA
                        event.getJDA().getGatewayPool().schedule(
                            () -> atribuirCargoComRetry(event, cargo, tentativasRestantes - 1, proximoAtraso),
                            proximoAtraso,
                            TimeUnit.MILLISECONDS
                        );
                    } else {
                        logger.error("❌Falha definitiva ao atribuir cargo '{}' ao usuário '{}' no servidor '{}': {}",
                                     cargo.getName(), nomeUsuario, nomeServidor, erro.getMessage(), erro);
                    }
                }
            );
    }


}
/*                    End of Class                                            */