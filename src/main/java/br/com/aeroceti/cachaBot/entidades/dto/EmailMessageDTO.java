/**
 * Projeto:  CachaBot - BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2024
 * Equipe:   Murilo, Victor, Allan
 */
package br.com.aeroceti.cachaBot.entidades.dto;

/**
 * RECORD para encapsular os dados de um email.
 * 
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
public record EmailMessageDTO(String to, String from, String nomeUsuario, String subject, String textBody) {
    
}
/*                    End of Class                                            */