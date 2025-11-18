/**
 * Projeto:  CachaBot - BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2024
 * Equipe:   Murilo, Victor, Allan
 */
package br.com.aeroceti.cachaBot.controladores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import br.com.aeroceti.cachaBot.entidades.NivelAcesso;
import br.com.aeroceti.cachaBot.servicos.PermissoesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Classe Controller para o objeto Permissao (Niveis de Acesso).
 *
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
@Controller
@RequestMapping("/permissao/")
public class PermissoesController {

    @Autowired
    private final PermissoesService nivelService;
    
    private final Logger logger = LoggerFactory.getLogger(PermissoesController.class);

    public PermissoesController(PermissoesService servico) {
        this.nivelService = servico;
    }

    /**
     * Listagem de TODOS as Permissoes cadastradas no Banco de dados.
     * Caso desejar ordenar por nome em ordem alfabetica, 
     * passar o valor TRUE senao FALSE
     *
     * @param modelo - Objeto Model para injetar dados na View
     * @param ordenar - Verdadeiro se desejar ordenar os nomes em ordem alfabetica
     * @return String Padrao Spring para redirecionar a uma pagina
     */
    @RequestMapping("/listar/{ordenar}")
    public String listagem(Model modelo, @PathVariable boolean ordenar) {
        logger.info("Recebida requisicao para listar todas os servidores ...");
        modelo.addAttribute("permissao",nivelService.listar(ordenar));
        return"/dashboard/permissoes-list";
    }

    @RequestMapping("/paginar/{page}/{pageSize}")
    public ModelAndView listar( @PathVariable int page, @PathVariable int pageSize ) {
        logger.info("Servico de Solicitacao para listar as Permissoes PAGINADAS ...");
        ModelAndView mv = new ModelAndView("/dashboard/permissoes");
        mv.addObject("permissao", nivelService.paginar(page, pageSize));
        return mv;
    }

    @PostMapping("/salvar")
    public String salvarPermissao(@ModelAttribute NivelAcesso permissao) {
        nivelService.salvar(permissao);
        return "redirect:/permissao/paginar/1/10";
    }
    
    /**
     * Deleta um Servidor da Base de Dados do sistema.
     * 
     * @param id     - ID do servidor na base de dados para deletar
     * @param modelo - Model para encaminhar a View
     * @return       - Redireciona para a Listagem Paginada de Servidores
     */
    @GetMapping("/excluir/{id}")
    @PreAuthorize("hasAuthority('Administrador')") 
    public String excluir( @PathVariable("id") long id, Model modelo) {
        logger.info("Requisicao recebida: EXCLUIR PERMISSAO - ID: " + id);
        Optional<NivelAcesso> nivelSolicitado = nivelService.buscar(id);
        if( nivelSolicitado.isPresent() ) {
            logger.info("ACHOU " + nivelSolicitado.get().getNome());
            nivelService.remover(nivelSolicitado.get());
        } else {
            logger.info("Processando requisicao: ALTERÇÃO NÃO REALIZADA - Referencia Invalida! ");
        }
        return "redirect:/permissao/paginar/1/10";
    }

}
/*                    End of Class                                            */
