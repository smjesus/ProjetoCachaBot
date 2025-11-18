/**
 * Projeto:  CachaBot  -  BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2024
 * Equipe:   Murilo, Victor, Allan
 */
package br.com.aeroceti.cachaBot.servicos;

import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.context.MessageSource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Esta classe cria o servico para a internacionalizacao (i18n).
 * 
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
@Service
public class I18nService {

	@Autowired
	private MessageSource message; 
	
	public String buscarMensagem(String chave, Locale locale) {
		String msn = message.getMessage(chave, null, locale);
		return msn;
	}
        
}
/*                    End of Class                                            */