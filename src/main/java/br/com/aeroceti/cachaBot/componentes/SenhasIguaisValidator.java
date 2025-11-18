/**
 * Projeto:  CachaBot  -  BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2024
 * Equipe:   Murilo, Victor, Allan
 */
package br.com.aeroceti.cachaBot.componentes;

import br.com.aeroceti.cachaBot.entidades.Colaborador;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Componente para realizar a validacao de senha no formulario de cadastro.
 * 
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
public class SenhasIguaisValidator implements ConstraintValidator<SenhasIguais, Colaborador> {

    public SenhasIguaisValidator() {
    }
    
    @Override
    public boolean isValid(Colaborador usuario, ConstraintValidatorContext context) {
        // evita erro em formulários parciais
        if (usuario.getCodigoAcesso() == null || usuario.getConfirmarSenha() == null) {
            return true; 
        }

        boolean iguais = usuario.getCodigoAcesso().equals(usuario.getConfirmarSenha());
        if (!iguais) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("confirmarSenha")
                .addConstraintViolation();
        }
        return iguais;
    }
    
}
/*                    End of Class                                            */