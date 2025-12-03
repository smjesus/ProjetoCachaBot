/**
 * Projeto:  CachaBot - BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2024
 * Equipe:   Murilo, Victor, Allan
 */
package br.com.aeroceti.cachaBot.servicos;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import br.com.aeroceti.cachaBot.entidades.Colaborador;
import br.com.aeroceti.cachaBot.entidades.NivelAcesso;
import br.com.aeroceti.cachaBot.repositorios.ColaboradoresRepository;
import br.com.aeroceti.cachaBot.repositorios.PermissoesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Classe de SERVICOS para o objeto Nivel de Acesso (Permissoes)
 *
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
@Service
public class PermissoesService {

    @Autowired
    private PermissoesRepository permisaoRepository;
    @Autowired
    private ColaboradoresRepository userRepository;
    
    private final Logger logger = LoggerFactory.getLogger(PermissoesService.class);

    /**
     * Listagem de TODOS as Permissoes cadastradas no Banco de dados.
     *
     * @param ordenarByNome - Boolean para indicar se ordena por nome o resultado da pesquisa
     * @return ArrayList em JSON com varios objetos Servidor
     */
    public List<NivelAcesso> listar(boolean ordenarByNome) {
        if (ordenarByNome) {
            logger.info("Executado Servico de listagem ordenada de Permissoes...");
        } else {
            logger.info("Executado Servico de listagem sem ordenacao de Permissoes...");
        }
        return ( ordenarByNome ? permisaoRepository.findByOrderByNomeAsc() : permisaoRepository.findAll() );
    }
    
    /**
     * Listagem PAGINADA de todos as Permissoes cadastradas no Banco de dados.
     * 
     * @param page     - pagina atual da requisicao
     * @param pageSize - tamanho de itens para apresentar na pagina
     * @return         - devolve pra View um objeto Pageable
     */
    public Page<NivelAcesso> paginar(int page, int pageSize){ 
        Pageable pageRequest = PageRequest.of((page -1), pageSize);
        Page<NivelAcesso> paginas = permisaoRepository.findAllPermissoes(pageRequest);
        // Força carregamento dos relacionamentos desejados
        paginas.forEach(c -> {
            c.getColaboradores().size();    // inicializa Colaboradores
        });

        return paginas; 
    }
    
    /**
     * Busca uma Permissao pelo ID que esta cadastrada no Banco de dados.
     *
     * @param  identidade - ID do objeto desejado do banco de dados
     * @return OPTIONAL   - Objeto Optional contendo a Permissao encontrada (se houver)
     */
    public Optional<NivelAcesso> buscar(Long identidade) {
        logger.info("Obtendo uma Permissao pelo ID do discord: " + identidade);
        return permisaoRepository.findByEntidadeID(identidade);
    }
    
    /**
     * Metodo para cadastrar uma Permissao na base de dados.
     *
     * @param permissao - Objeto Usuario com os dados a serem gravados
     * @return ResponseEntity contendo uma mensagem de erro OU um objeto Usuario cadastrado
     */
    public ResponseEntity<?> salvar(NivelAcesso permissao) {
        logger.info("Persistindo permissao no banco de dados...");
        permisaoRepository.save(permissao);
        logger.info("Permissao " + permissao.getNome()+ " salva no banco de dados!");
        return new ResponseEntity<>( permissao, HttpStatus.CREATED);
    }
    
    /**
     * DELETA uma Permissao do banco de dados.
     *
     * @param permissao - objeto a ser deletado
     * @return ResponseEntity - Mensagem de Erro ou Sucesso na operacao
     */
    @Transactional
    public ResponseEntity<?> remover(NivelAcesso permissao) {
        try {
            List<Colaborador> colaboradores = permissao.getColaboradores();
            if (colaboradores != null && !colaboradores.isEmpty()) {
                for (Colaborador colaborador : colaboradores) {
                    // Remove a referência do lado "many"
                    colaborador.setNivelAcesso(null); 
                }
            }
            userRepository.saveAll(colaboradores); 
            permisaoRepository.delete(permissao);
        } catch(Exception ex) {
            logger.info("DabaBASE Error: {}.", ex.getMessage());            
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Requisicao executada: Nivel de Acesso DELETADA no Sistema!");
        return new ResponseEntity<>("Nivel de Acesso DELETADA no Sistema!", HttpStatus.OK);
    }
    
}
/*                    End of Class                                            */