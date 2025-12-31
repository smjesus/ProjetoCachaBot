/**
 * Projeto:  CachaBot - BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2023
 * Equipe:   Murilo, Victor
 */
package br.com.aeroceti.cachaBot.configuracoes;

import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import br.com.aeroceti.cachaBot.componentes.CustomAuthenticationFailureHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * Configuracao para o Spring Security no sistema.
 * 
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomAuthenticationFailureHandler failureHandler;    
    
    public SecurityConfig(CustomAuthenticationFailureHandler failureHandler) {
        this.failureHandler = failureHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, "/").permitAll()
                        .requestMatchers(HttpMethod.GET, "/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/termos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/privacidade").permitAll()
                        .requestMatchers(HttpMethod.POST, "/faleconosco").permitAll()
                        .requestMatchers(HttpMethod.GET, "/documentacao").permitAll()
                        .requestMatchers(HttpMethod.GET, "/usuario/uuid/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/googlea3e4f03a4569e38f", "googlea3e4f03a4569e38f.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/sitemap.xml", "/robots.txt").permitAll()
                        .requestMatchers(HttpMethod.GET, "/.well-known/pki-validation/**").permitAll()
                        .requestMatchers("/content-scripts/**").permitAll()

                        
                        .requestMatchers(HttpMethod.GET, "/css/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/assets/**").permitAll()
                        .anyRequest().authenticated()
                )
                
                .formLogin( (formulario) -> formulario
                        .loginPage("/login")
                        .failureHandler(failureHandler)
                        .defaultSuccessUrl("/dashboard")
                        .permitAll() 
                )
                
                .rememberMe(remember -> remember
                        .key("key-Remember-Me-OnCachaBOT")
                        .tokenValiditySeconds(5 * 24 * 60 * 60) // 5 dias
                )
        
                .logout( (logout) -> logout
                        .logoutUrl("/logout").logoutSuccessUrl("/").permitAll() )
                
                //.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                //.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }    
   
}
/*                    End of Class                                            */