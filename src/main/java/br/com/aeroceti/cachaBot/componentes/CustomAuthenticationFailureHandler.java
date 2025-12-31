/**
 * Projeto:  CachaBot  -  BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2024
 * Equipe:   Murilo, Victor, Allan
 */
package br.com.aeroceti.cachaBot.componentes;

import br.com.aeroceti.cachaBot.servicos.I18nService;
import org.slf4j.Logger;
import java.io.IOException;
import org.slf4j.LoggerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
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

    @Autowired
    private I18nService i18svc; 
    
    private final Logger logger = LoggerFactory.getLogger(CustomAuthenticationFailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        Locale currentLocale = LocaleContextHolder.getLocale();
        String reason = i18svc.buscarMensagem("login.bad.credentials", currentLocale); 

        if (exception instanceof UsernameNotFoundException) {
            reason = i18svc.buscarMensagem("login.notfound", currentLocale);
        } else if (exception instanceof BadCredentialsException) {
            reason = i18svc.buscarMensagem("login.invalid", currentLocale); 
        }
        
        logger.info("Tentativa de LOGIN com usuário não cadastrado de " + request.getRemoteAddr() );
        logger.info("Provavel motivo da falha: " + reason);
        
        request.getSession().setAttribute("LOGIN_ERROR_MESSAGE", reason);
        response.sendRedirect(request.getContextPath().trim() + "/login");
    }

}
/*                    End of Class                                            */