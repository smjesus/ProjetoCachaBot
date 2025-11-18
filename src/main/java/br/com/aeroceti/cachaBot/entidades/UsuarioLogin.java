/**
 * Projeto:  CachaBot - BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2023
 * Equipe:   Murilo, Victor
 */
package br.com.aeroceti.cachaBot.entidades;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Entidade do Spring Security para login no sistema.
 * 
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
public class UsuarioLogin implements UserDetails {

    private final Colaborador usuario;

    public UsuarioLogin(Colaborador colaborador) {
        this.usuario = colaborador;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList(usuario.getNivelAcesso().getNome());
    }
    
    public Long getId(){
        return usuario.getEntidadeID();
    }

    @Override
    public String getPassword() {
        return usuario.getCodigoAcesso();
    }

    @Override
    public String getUsername() {
        return usuario.getContaEmail();
    }

    @Override
    public boolean isEnabled() {
        return usuario.isAtivo(); 
    }
    
    public String getNomeUsuario() {
        return usuario.getNomePessoal();
    }
    
    public Colaborador getColaborador(){
        return this.usuario;
    }

}
/*                    End of Class                                            */