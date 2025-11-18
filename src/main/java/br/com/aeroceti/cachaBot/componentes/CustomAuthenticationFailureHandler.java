/**
 * Projeto:  CachaBot  -  BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2024
 * Equipe:   Murilo, Victor, Allan
 */
package br.com.aeroceti.cachaBot.componentes;

import org.slf4j.Logger;
import java.io.IOException;
import org.slf4j.LoggerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/**
 * Esta classe realiza o Handler para Falhas de Autenticaçao.
 * Com esse handler obtemos no log da aplicacao o IP que tentou logar para analises futuras.
 * 
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final Logger logger = LoggerFactory.getLogger(CustomAuthenticationFailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String reason = "Credenciais inválidas";
        if (exception instanceof UsernameNotFoundException) {
            reason = "Usuário não encontrado";
        } else if (exception instanceof BadCredentialsException) {
            reason = "Usuário ou senha inválidos";
        }
        logger.info("Tentativa de LOGIN com usuário não cadastrado de " + request.getRemoteAddr() );
        logger.info("Provavel motivo da falha: " + reason);
        response.sendRedirect(request.getContextPath().trim() + "/login?error=true");
    }

}
/*                    End of Class                                            */