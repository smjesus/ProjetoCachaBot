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
import br.com.aeroceti.cachaBot.entidades.Colaborador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Interface para o Repositorio de Colaboradores (Usuarios).
 *
 * Esta classe abstrai diversos metodos de persistencia do JPA.
 *
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
@Repository
public interface ColaboradoresRepository extends JpaRepository<Colaborador, Long> {

    // 🔍 Busca um colaborador específico trazendo NivelAcesso e Servidores
    @Query("""
           SELECT c FROM Colaborador c
           LEFT JOIN FETCH c.nivelAcesso
           LEFT JOIN FETCH c.servidores
           WHERE c.entidadeID = :id
           """)
    Optional<Colaborador> findByIdComRelacionamentos(@Param("id") Long id);

    // 🔍 Busca todos os colaboradores com seus Niveis de Acesso e Servidores
    @Query("""
           SELECT DISTINCT c FROM Colaborador c
           LEFT JOIN FETCH c.nivelAcesso
           LEFT JOIN FETCH c.servidores
           """)
    List<Colaborador> findAllComRelacionamentos();    

    // obtem uma lista de Colaboradores com PAGINACAO
    @Query(value = "select * from Colaborador order by nomePessoal ASC", nativeQuery = true )
    Page<Colaborador> findAllColaboradores(Pageable page);
    
    // obtem uma Permissao atraves do nome pessoal
    Optional<Colaborador> findByNomePessoal(String chavePesquisa);

    // obtem uma Permissao atraves do email
    Optional<Colaborador> findByContaEmail(String chavePesquisa);

    // obtem uma Permissao atraves do ID
    Optional<Colaborador> findByEntidadeID(Long chavePesquisa);

    // obtem uma lista de Permissoes ORDENADA por nome
    List<Colaborador> findByOrderByNomePessoalAsc();

    // obtem o numero de Permissoes por ID
    int countByEntidadeID(Long entidadeID);
    
    // obtem o numero de Permissoes por nome
    int countByContaEmail(String contaEmail);    

}
/*                    End of Class                                            */