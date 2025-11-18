/**
 * Projeto:  CachaBot  -  BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2024
 * Equipe:   Murilo, Victor, Allan
 */
package br.com.aeroceti.cachaBot.componentes;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * ANOTACAO para a confirmacao de senha no cadastro
 * 
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
@Documented
@Constraint(validatedBy = SenhasIguaisValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface SenhasIguais {
    String message() default "{form.user.validar.senha}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
/*                    End of Class                                            */