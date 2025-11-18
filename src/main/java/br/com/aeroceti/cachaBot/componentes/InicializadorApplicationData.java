/**
 * Projeto:  CachaBot  -  BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2024
 * Equipe:   Murilo, Victor, Allan
 */
package br.com.aeroceti.cachaBot.componentes;

import org.slf4j.Logger;
import java.util.Optional;
import org.slf4j.LoggerFactory;
import br.com.aeroceti.cachaBot.entidades.Colaborador;
import br.com.aeroceti.cachaBot.entidades.NivelAcesso;
import br.com.aeroceti.cachaBot.repositorios.ColaboradoresRepository;
import br.com.aeroceti.cachaBot.repositorios.PermissoesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Este Componente inicializa um usuario Administrador e o Nivel Admin.
 * 
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
@Component
public class InicializadorApplicationData implements CommandLineRunner {
    
    @Autowired
    private PermissoesRepository     nivelRepository;
    @Autowired
    private ColaboradoresRepository  userRepository;
    @Autowired
    private PasswordEncoder          passwdEncoder ;
    
    private final Logger logger = LoggerFactory.getLogger(InicializadorApplicationData.class);
    
    /**
     *  Metodo principal que inicializa um usuario padrao.
     * 
     * @param args - argumentos passados para a aplicacao
     * @throws java.lang.Exception
     */
    @Override
    public void run(String... args) throws Exception {
        Colaborador userAdmin = new Colaborador();
        NivelAcesso permissao = new NivelAcesso(null, "Administrador");
        NivelAcesso gerente   = new NivelAcesso(null, "Gerente");

        // Verifica se o Perfil Administrador existe no BD:
        int valor = nivelRepository.countByNome("Administrador");
        if( valor == 0 ) {
            // senao existe, cria um no BD:
            nivelRepository.save(permissao);
            logger.info("Sistema CachaBot inicializando (Criada permissao de Admin) ... ");
        }
        // Verifica se o Perfil Gerente existe no BD:
        valor = nivelRepository.countByNome("Gerente");
        if( valor == 0 ) {
            // senao existe, cria um no BD:
            nivelRepository.save(gerente);
            logger.info("Sistema CachaBot inicializando (Criada permissao de Gerente) ... ");
        }
        // Verifica se há administrador cadastrado:
        Optional<NivelAcesso> permissaoSolicitada = nivelRepository.findByNome(permissao.getNome());
        permissao = permissaoSolicitada.get();
        if( permissao.getColaboradores().isEmpty() ) {
            // Nao tem administrador, cadastrando um 'default':
            Optional userSolicitado = userRepository.findByContaEmail("cachabot@aeroceti.com.br");
            if (userSolicitado.isEmpty()) {
                userAdmin.setContaEmail("cachabot@aeroceti.com.br");
                userAdmin.setNomePessoal("Administrador do CachaBOT");
                userAdmin.setCodigoAcesso( passwdEncoder.encode("admin-cachabot") );
                userAdmin.setEntidadeID(null);
                logger.info("Sistema CachaBot Inicializando (Criado Admin default) ... ");
            } else {
                userAdmin = (Colaborador) userSolicitado.get();
            }
            userAdmin.setAtivo(true);
            userAdmin.setNivelAcesso(permissao);
            userRepository.save(userAdmin);
            logger.info("Sistema CachaBot Inicializando ((Admin default configurado)!");            
        }
        
        logger.info("Sistema CachaBot Inicializado. (Runner concluido)");
    }
    
}
/*                    End of Class                                            */