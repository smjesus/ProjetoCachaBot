/**
 * Projeto:  CachaBot  -  BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2024
 * Equipe:   Murilo, Victor, Allan
 */
package br.com.aeroceti.cachaBot.repositorios;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import br.com.aeroceti.cachaBot.entidades.Servidor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Interface para o Repositorio de Servidor.
 *
 * Esta classe abstrai diversos metodos de persistencia do JPA.
 *
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
@Repository
public interface ServidoresRepository extends JpaRepository<Servidor, Long> {

    // obtem uma Permissao atraves do ID do Discord
    Optional<Servidor> findByServidorID(Long chavePesquisa);

    // obtem uma Permissao atraves do ID do BD
    Optional<Servidor> findByEntidadeID(Long chavePesquisa);

    // obtem uma lista de Permissoes ORDENADA por nome
    List<Servidor> findByOrderByNomeAsc();

    // obtem uma lista de Colaboradores com PAGINACAO
    @Query(value = "select * from Servidor order by nome ASC", nativeQuery = true )
    Page<Servidor> findAllServidores(Pageable page);
    
    // obtem o numero de Servidores por ID do BD
    int countByEntidadeID(Long servidorID);
    
    // obtem o numero de Permissoes por nome
    int countByNome(String nome);    

    // obtem o numero de Servidores com o BOT
    int countByAtivo(boolean status);  

}
/*                    End of Class                                            */