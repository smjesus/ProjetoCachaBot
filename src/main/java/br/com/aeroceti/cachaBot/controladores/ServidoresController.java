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
import br.com.aeroceti.cachaBot.entidades.Servidor;
import br.com.aeroceti.cachaBot.servicos.ServidoresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Classe Controller para o objeto Servidor.
 *
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
@Controller
@RequestMapping("/servidor/")
public class ServidoresController {

    @Autowired
    private ServidoresService servidorService;
    
    private final Logger logger = LoggerFactory.getLogger(ServidoresController.class);

    /**
     * Listagem de TODOS os Servidores cadastradas no Banco de dados.
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
        modelo.addAttribute("servidor",servidorService.listar(ordenar));
        return"/dashboard/servidores-list";
    }

    /**
     * Listagem PAGINADA dos Servidores cadastrados no Banco de Dados.
     * 
     * @param page - numero da pagina a ser exibida
     * @param pageSize - total de itens na pagina a ser exibida
     * @return  Model an View do Thymeleaf para a listagem paginada
     */
    @RequestMapping("/paginar/{page}/{pageSize}")
    public ModelAndView listar( @PathVariable int page, @PathVariable int pageSize ) {
        logger.info("Servico de Solicitacao para listar os Servidores PAGINADOS ...");
        ModelAndView mv = new ModelAndView("/dashboard/servidores");
        mv.addObject("servidor", servidorService.paginar(page, pageSize));
        return mv;
    }    
    
    /**
     * Deleta um Servidor da Base de Dados do sistema.
     * 
     * @param id     - ID do servidor na base de dados para deletar
     * @param modelo - Model para encaminhar a View
     * @return       - Redireciona para a Listagem Paginada de Servidores
     */
    @GetMapping("/excluir/{id}")
    @PreAuthorize("hasAuthority('Administrador') or (hasAuthority('Gerente') and @servidoresService.isDonoDoServidor(principal.username, #id))")
    public String excluirServidor( @PathVariable("id") long id, Model modelo) {
        logger.info("Requisicao recebida: EXCLUIR um Servidor do Sistema: ID ={} ...", id);
        Optional<Servidor> serverSolicitado = servidorService.procurar(id);
        if( serverSolicitado.isPresent() ) {
            servidorService.remover(serverSolicitado.get());
        } else {
            logger.info("Processando requisicao: ALTERÇÃO NÃO REALIZADA - Referencia Invalida! ");
        }
        return "redirect:/servidor/paginar/1/10";
    }

}
/*                    End of Class                                            */
