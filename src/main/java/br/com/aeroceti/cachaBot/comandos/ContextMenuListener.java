/**
 * Projeto:  CachaBot - BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2024
 * Equipe:   Murilo, Victor, Allan
 */
package br.com.aeroceti.cachaBot.comandos;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * CLASSE de Tratamento do COMANDO de CONTEXTO relativo aos itens do Menu "Apps"
 * 
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
@Component
public class ContextMenuListener extends ListenerAdapter {
    
    private static final Pattern YT_PATTERN = Pattern.compile(
        "(https?://)?(www\\.)?(youtube\\.com/watch\\?v=|youtu\\.be/)[\\w-]{11}"
    );
    
    private final Logger logger = LoggerFactory.getLogger(ContextMenuListener.class);

    public ContextMenuListener() {
        logger.info("Listerner de Contexto do CachaBOT inicializado ...");
    }
    
    @Override
    public void onMessageContextInteraction(MessageContextInteractionEvent event) {

        logger.info("Tratando evento de Contexto (Comando: {})...", event.getName());
        if (event.getName().equals("Compartilhar este Estudo ...")) {

            Optional<String> youtubeOpt = extrairYoutubeUrl(event.getTarget().getContentDisplay());
            // Se a mensagem nao contiver um link do Youtube, avisa ao usuario:
            if (youtubeOpt.isEmpty()) {
                event.reply("Você precisa selecionar um link válido do YouTube com um estudo para compartilhar!").setEphemeral(true).queue();
                return;
            }
            String waLink = "https://wa.me/?text=" + URLEncoder.encode(youtubeOpt.get(), StandardCharsets.UTF_8);
            String fbLink = "https://www.facebook.com/sharer/sharer.php?u=" + URLEncoder.encode(youtubeOpt.get(), StandardCharsets.UTF_8);

//            Message message = event.getTarget();
//            event.reply("Conteúdo da mensagem:\n" + message.getContentDisplay())
//                 .setEphemeral(true)
//                 .queue();
            logger.info("Enviando os botões de compartilhamento para o usuario...");
            event.reply("Compartilhar este link com:")
                 .setEphemeral(true)
                 .addActionRow(
                     Button.link(waLink, "WhatsApp").withEmoji(Emoji.fromUnicode("📱")),
                     Button.link(fbLink, "Facebook").withEmoji(Emoji.fromUnicode("📘"))
                 )
                 .queue();
        } else {
            event.reply("Não recebi nenhum comando de contexto válido ...").setEphemeral(true).queue();
        }
    }
    
    
    private Optional<String> extrairYoutubeUrl(String texto) {
        Matcher matcher = YT_PATTERN.matcher(texto);
        if (matcher.find()) {
            logger.info("Encontrou link do Youtube na mensagem recebida...");
            return Optional.of(matcher.group());
        }
        logger.info("Não encontrou um link válido do Youtube na mensagem recebida...");
        return Optional.empty();
    }
    
}
/*                    End of Class                                            */