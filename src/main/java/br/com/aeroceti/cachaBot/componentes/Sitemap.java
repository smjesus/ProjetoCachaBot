/**
 * Projeto:  CachaBot  -  BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2024
 * Equipe:   Murilo, Victor, Allan
 */
package br.com.aeroceti.cachaBot.componentes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ANOTACAO para a mapear o SiteMAP
 * 
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Sitemap {
    double priority() default 0.5;
    String changefreq() default "monthly";
    
}
/*                    End of Class                                            */