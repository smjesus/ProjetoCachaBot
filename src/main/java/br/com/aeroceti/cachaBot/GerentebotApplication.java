/**
 * Projeto:  CachaBot - BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2024
 * Equipe:   Murilo, Victor, Allan
 */
package br.com.aeroceti.cachaBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da Aplicacao usando o Spring Boot.
 * 
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
@SpringBootApplication(/* exclude = {SecurityAutoConfiguration.class}*/)
public class GerentebotApplication {

	public static void main(String[] args) {
		SpringApplication.run(GerentebotApplication.class, args);
	}

}
/*                    End of Class                                            */