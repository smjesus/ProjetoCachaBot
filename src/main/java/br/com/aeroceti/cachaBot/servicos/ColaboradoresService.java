/**
 * Projeto:  CachaBot - BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2024
 * Equipe:   Murilo, Victor, Allan
 */
package br.com.aeroceti.cachaBot.servicos;

import java.util.List;
import java.util.UUID;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import br.com.aeroceti.cachaBot.entidades.Colaborador;
import br.com.aeroceti.cachaBot.entidades.UsuarioLogin;
import br.com.aeroceti.cachaBot.entidades.UsuarioVerificador;
import br.com.aeroceti.cachaBot.repositorios.ColaboradoresRepository;
import br.com.aeroceti.cachaBot.repositorios.UsuarioVerificadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Classe de SERVICOS para o objeto Colaborador
 *
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
@Service
public class ColaboradoresService implements UserDetailsService {

    @Autowired
    private PasswordEncoder          passwdEncoder ;
    @Autowired
    private ColaboradoresRepository  userRepository;
    @Autowired
    private UsuarioVerificadorRepository  uuidRepository;
    
    private final Logger logger = LoggerFactory.getLogger(ColaboradoresService.class);
    
    /**
     * Metodo para o SpringSecurity carregar o usuario logado.
     * 
     * @param username  -  nome de usuario que realizou o login
     * @return          -  registra um UserDetails
     * @throws UsernameNotFoundException 
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Colaborador usuario = userRepository.findByContaEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
        return new UsuarioLogin(usuario);
    }
    
    /**
     * Listagem de TODOS os Servidores cadastradas no Banco de dados.
     *
     * @param ordenarByNome - Boolean para indicar se ordena por nome o resultado da pesquisa
     * @return ArrayList em JSON com varios objetos Servidor
     */
    public List<Colaborador> listar(boolean ordenarByNome) {
        if (ordenarByNome) {
            logger.info("Executado Servico de listagem ordenada ...");
        } else {
            logger.info("Executado Servico de listagem sem ordenacao ...");
        }
        return ( ordenarByNome ?  userRepository.findByOrderByNomePessoalAsc() : userRepository.findAll() );
    }
    
    /**
     * Listagem PAGINADA de todos os Colaboradores cadastrados no Banco de dados.
     * 
     * @param page     - pagina atual da requisicao
     * @param pageSize - tamanho de itens para apresentar na pagina
     * @return         - devolve pra View um objeto Pageable
     */
    public Page<Colaborador> paginar(int page, int pageSize){ 
        Pageable pageRequest = PageRequest.of((page -1), pageSize);
        Page<Colaborador> paginas = userRepository.findAllColaboradores(pageRequest);
        // Força carregamento dos relacionamentos desejados
        paginas.forEach(c -> {
            if(c.getNivelAcesso() != null) c.getNivelAcesso().toString(); // inicializa Permissoes
            c.getServidores().size();                                     // inicializa Servidores
        });
        return paginas; 
    }

    /**
     * Busca um Colaborador pelo ID que esta cadastrado no Banco de dados.
     *
     * @param  identidade - ID do objeto desejado do banco de dados
     * @return OPTIONAL   - Objeto Optional contendo o Colaborador encontrado (se houver)
     */
    public Optional<Colaborador> buscar(Long identidade) {
        logger.info("Obtendo um Colaborador pelo ID: " + identidade);
        return userRepository.findByEntidadeID(identidade);
    }

    public Optional<UsuarioVerificador> buscarVerificador(Long identidade) {
        logger.info("Obtendo um Colaborador pelo ID: " + identidade);
       // Colaborador usuario = (Colaborador) buscar(identidade).get();
        return uuidRepository.findByUsuarioId(identidade);
    }
    
    public Optional<UsuarioVerificador> buscarVerificadorByUUID(UUID identidade) {
        logger.info("Obtendo um Colaborador pelo UUID: " + identidade);
       // Colaborador usuario = (Colaborador) buscar(identidade).get();
        return uuidRepository.findBycodigoUUID(identidade);
    }
    
    
    /**
     * Metodo para atualizar um Colaborador na base de dados (Principalmente a senha).
     *
     * @param usuario - Objeto Usuario com os dados a serem atualizados
     * @return ResponseEntity contendo uma mensagem de erro OU um objeto Usuario cadastrado
     */
    public ResponseEntity<?> atualizar(Colaborador usuario) {
        // Criptografa a senha:
        String senhaCriptografada =  passwdEncoder.encode(usuario.getCodigoAcesso()).trim();
        usuario.setCodigoAcesso( senhaCriptografada );
        usuario.setConfirmarSenha(senhaCriptografada);
        // ATUALIZA o objeto do banco de dados
        logger.info("Usuario " + usuario.getNomePessoal() + " atualizado no banco de dados!");
        return new ResponseEntity<>(userRepository.save(usuario), HttpStatus.OK);
    }
    
    /**
     * Salva um Colaborador que tenha alguma propriedade alterada (exceto senha)
     * 
     * @param usuario - objeto para atualizar no banco de dados
     */
    public void salvarColaborador(Colaborador usuario) {
        userRepository.save(usuario);
    }
    
    public ResponseEntity<?> atualizarVerificador(UsuarioVerificador verificador) {
        // ATUALIZA o objeto do banco de dados
        logger.info("Verificador atualizado no banco de dados!");
        return new ResponseEntity<>(uuidRepository.save(verificador), HttpStatus.OK);
    }
    
    public ResponseEntity<?> deletarVerificador(UsuarioVerificador verificador) {
        // ATUALIZA o objeto do banco de dados
        logger.info("Verificador deletado do banco de dados!");
        verificador.setUsuario(null);
        uuidRepository.delete(verificador);
        return new ResponseEntity<>("Verificador deletado do banco de dados!", HttpStatus.OK);
    }
    
    
    /**
     * DELETA um Usuario do banco de dados.
     *
     * @param usuario - objeto a ser deletado
     * @return ResponseEntity - Mensagem de Erro ou Sucesso na operacao
     */
    public ResponseEntity<?> remover(Colaborador usuario) {
        logger.info("Excluindo Usuario do banco de dados...");
        userRepository.delete(usuario);
        logger.info("Requisicao executada: usuario DELETADO no Sistema!");
        return new ResponseEntity<>("Usuario DELETADO no Sistema!", HttpStatus.OK);
    }
    
}
/*                    End of Class                                            */