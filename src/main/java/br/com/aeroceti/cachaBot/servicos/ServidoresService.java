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
import br.com.aeroceti.cachaBot.entidades.Servidor;
import br.com.aeroceti.cachaBot.repositorios.ServidoresRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Classe de SERVICOS para o objeto Servidor
 *
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
@Service
public class ServidoresService {

    @Autowired
    private ServidoresRepository serverRepository;

    private final Logger logger = LoggerFactory.getLogger(ServidoresService.class);

    /**
     * Listagem de TODOS os Servidores cadastradas no Banco de dados.
     *
     * @param ordenarByNome - Boolean para indicar se ordena por nome o resultado da pesquisa
     * @return ArrayList em JSON com varios objetos Servidor
     */
    public List<Servidor> listar(boolean ordenarByNome) {
        if (ordenarByNome) {
            logger.info("Executado Servico de listagem ordenada ...");
        } else {
            logger.info("Executado Servico de listagem sem ordenacao ...");
        }
        return ( ordenarByNome ? serverRepository.findByOrderByNomeAsc() : serverRepository.findAll() );
    }
    
    /**
     * Listagem PAGINADA de todos os Servidores cadastrados no Banco de dados.
     * 
     * @param page     - pagina atual da requisicao
     * @param pageSize - tamanho de itens para apresentar na pagina
     * @return         - devolve pra View um objeto Pageable
     */
    public Page<Servidor> paginar(int page, int pageSize){ 
        Pageable pageRequest = PageRequest.of((page -1), pageSize);
        Page<Servidor> paginas = serverRepository.findAllServidores(pageRequest);
        // Força carregamento dos relacionamentos desejados
        paginas.forEach(c -> {
            c.getBoasVindas().getTitulo();                                       // inicializa Mensagem de Boas Vindas
            if (c.getColaborador() != null) c.getColaborador().getNomePessoal(); // inicializa Colaboradores
        });

        return paginas; 
    }
    
    /**
     * Busca um Servidor pelo ID do DISCORD que esta cadastrado no Banco de dados.
     *
     * @param  identidade - ID DO DISCORD do objeto desejado do banco de dados
     * @return OPTIONAL   - Objeto Optional contendo a Permissao encontrada (se houver)
     */
    public Optional<Servidor> buscar(Long identidade) {
        logger.info("Obtendo um Servidor pelo ID do discord: " + identidade);
        return serverRepository.findByServidorID(identidade);
    }
    
    /**
     * Busca um Servidor pelo ID da Tabela que esta cadastrado no Banco de dados.
     *
     * @param  identidade - ID DA TABELA do objeto desejado do banco de dados
     * @return OPTIONAL   - Objeto Optional contendo a Permissao encontrada (se houver)
     */
    public Optional<Servidor> procurar(Long identidade) {
        logger.info("Obtendo um Servidor pelo ID do BD " + identidade);
        return serverRepository.findByEntidadeID(identidade);
    }    
   
    public boolean isDonoDoServidor(String emailUsuario, Long servidorId) {
        return serverRepository.findById(servidorId)
                .map(s -> s.getColaborador() != null && emailUsuario.equals(s.getColaborador().getContaEmail()))
                .orElse(false);
    }
    
    /**
     * Metodo para cadastrar um Servidor na base de dados.
     *
     * @param servidor - Objeto Usuario com os dados a serem gravados
     * @return ResponseEntity contendo uma mensagem de erro OU um objeto Usuario cadastrado
     */
    public ResponseEntity<?> salvar(Servidor servidor) {
        logger.info("Persistindo Servidor no banco de dados...");
        servidor.setEntidadeID(null);
        serverRepository.save(servidor);
        logger.info("Servidor " + servidor.getNome()+ " salva no banco de dados!");
        return new ResponseEntity<>( servidor, HttpStatus.CREATED);
    }
 
    /**
     * Metodo para atualizar um Servidor na base de dados.
     *
     * @param servidor - Objeto Usuario com os dados a serem atualizados
     * @return ResponseEntity contendo uma mensagem de erro OU um objeto Usuario cadastrado
     */
    public ResponseEntity<?> atualizar(Servidor servidor) {
        // ATUALIZA o objeto do banco de dados
        logger.info("Servidor " + servidor.getNome()+ " atualizada no banco de dados!");
        return new ResponseEntity<>(serverRepository.save(servidor), HttpStatus.OK);
    }
    
    /**
     * DELETA um Servidor do banco de dados.
     *
     * @param servidor - objeto a ser deletado
     * @return ResponseEntity - Mensagem de Erro ou Sucesso na operacao
     */
    public ResponseEntity<?> remover(Servidor servidor) {
        logger.info("Excluindo Servidor {}, do banco de dados...", servidor.getNome());
        serverRepository.delete(servidor);
        logger.info("Requisicao executada: Servidor DELETADO no Sistema!");
        return new ResponseEntity<>("Servidor DELETADO no Sistema!", HttpStatus.OK);
    }
    
}
/*                    End of Class                                            */