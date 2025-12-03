/**
 * Projeto:  CachaBot - BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2024
 * Equipe:   Murilo, Victor, Allan
 */
package br.com.aeroceti.cachaBot.entidades.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * RECORD para encapsular os dados de um Colaborador.
 * 
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
public record ColaboradorDTO(
        
        Long        entidadeID,
        boolean     ativo,
        
        @NotBlank(message = "{form.user.nome.notblank}")
        String      nomePessoal,
        
        @NotBlank(message = "{form.user.email.notblank}")  @Email(message = "{form.user.email.notvalid}")
        @Pattern(
            regexp = "^[^@]+@[^@]+(\\.[a-zA-Z]{2,})+$",
            message = "{form.user.email.notcomplete}"
        )        
        String      contaEmail,
        
        Long        nivelAcessoId
        ) {

}
