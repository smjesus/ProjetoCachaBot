/**
 * Projeto:  CachaBot  -  BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2024
 * Equipe:   Murilo, Victor, Allan
 */
package br.com.aeroceti.cachaBot.repositorios;

import java.util.UUID;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import br.com.aeroceti.cachaBot.entidades.UsuarioVerificador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Interface para o Repositorio dos Codigos de Verificacao de Contas (UsuarioVerificador).
 *
 * Esta classe abstrai diversos metodos de persistencia do JPA.
 *
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
@Repository
public interface UsuarioVerificadorRepository extends JpaRepository<UsuarioVerificador, Long> {

    // obtem uma Permissao atraves do UUID
    Optional<UsuarioVerificador> findBycodigoUUID(UUID chavePesquisa);

    @Query("SELECT v FROM UsuarioVerificador v WHERE v.usuario.entidadeID = :usuarioId")
    Optional<UsuarioVerificador> findByUsuarioId(@Param("usuarioId") Long usuarioId);
    
}
/*                    End of Class                                            */