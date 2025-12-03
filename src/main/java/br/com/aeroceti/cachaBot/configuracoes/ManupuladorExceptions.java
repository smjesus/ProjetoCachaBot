/**
 * Projeto:  CachaBot - BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2023
 * Equipe:   Murilo, Victor
 */
package br.com.aeroceti.cachaBot.configuracoes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Classe Controller Advice para manupular as excessões no sistema.
 *
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
@ControllerAdvice
public class ManupuladorExceptions extends RuntimeException {
    
    private final Logger logger = LoggerFactory.getLogger(ManupuladorExceptions.class);
    
//   // @ExceptionHandler(Exception.class)
//    public void generalExceptions(Exception ex) {
//        logger.info("Exception GENERICA ocorrida no Sistema: " + ex.getMessage() );
//    }
//    
    @ExceptionHandler(AuthenticationException.class)
    public void falhaAutenticacao(AuthenticationException error, HttpHeaders header) {
        logger.info("Exception Gerada: " + error.getMessage() + " - Tentativa de LOGIN com usuário não cadastrado!");
        logger.info("Host ({}) e Origem: {}", header.getHost().toString(), header.getOrigin());
    }
}
/*                    End of Class                                            */