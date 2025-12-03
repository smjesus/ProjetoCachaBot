/**
 * Projeto:  CachaBot  -  BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2024
 * Equipe:   Murilo, Victor, Allan
 */
package br.com.aeroceti.cachaBot.repositorios;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import br.com.aeroceti.cachaBot.entidades.NivelAcesso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Interface para o Repositorio de NivelAcesso (Niveis de Acesso).
 *
 * Esta classe abstrai diversos metodos de persistencia do JPA.
 *
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
@Repository
public interface PermissoesRepository extends JpaRepository<NivelAcesso, Long> {

    // obtem o numero de Permissoes por nome
    int countByNome(String ruleName);    

    // obtem o numero de Permissoes por ID
    int countByEntidadeID(Long entidadeID);

    // obtem uma Permissao atraves do ID
    Optional<NivelAcesso> findByEntidadeID(Long chavePesquisa);
    
    // obtem uma lista de Permissoes ORDENADA por nome
    List<NivelAcesso> findByOrderByNomeAsc();

    // obtem uma Permissao atraves do nome
    Optional<NivelAcesso> findByNome(String chavePesquisa);

    // obtem uma lista de Colaboradores com PAGINACAO
    @Query(value = "select * from NivelAcesso order by nome ASC", nativeQuery = true )
    Page<NivelAcesso> findAllPermissoes(Pageable page);

    // obtem uma Permissao atraves do nome
    @Query("SELECT n FROM NivelAcesso n LEFT JOIN FETCH n.colaboradores WHERE n.nome = :nome")
    Optional<NivelAcesso> findByNomeAndColaboradores(@Param("nome") String chavePesquisa);

}
/*                    End of Class                                            */