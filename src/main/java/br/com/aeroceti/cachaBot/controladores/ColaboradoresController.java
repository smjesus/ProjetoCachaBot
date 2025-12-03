/**
 * Projeto:  CachaBot - BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2024
 * Equipe:   Murilo, Victor, Allan
 */
package br.com.aeroceti.cachaBot.controladores;

import java.util.UUID;
import java.util.Locale;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import org.springframework.ui.Model;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import br.com.aeroceti.cachaBot.entidades.Colaborador;
import br.com.aeroceti.cachaBot.entidades.UsuarioVerificador;
import br.com.aeroceti.cachaBot.entidades.dto.ColaboradorDTO;
import br.com.aeroceti.cachaBot.entidades.dto.EmailMessageDTO;
import br.com.aeroceti.cachaBot.servicos.ColaboradoresService;
import br.com.aeroceti.cachaBot.servicos.I18nService;
import br.com.aeroceti.cachaBot.servicos.MailSenderService;
import br.com.aeroceti.cachaBot.servicos.PermissoesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Classe Controller para o objeto Colaborador (usuarios).
 *
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
@Controller
@RequestMapping("/usuario/")
public class ColaboradoresController {

    @Autowired
    private I18nService i18svc;
    @Autowired
    private ColaboradoresService usuariosService;
    @Autowired
    private MailSenderService    mailService;
    @Autowired
    private PermissoesService    permissaoService;

    private final Logger logger = LoggerFactory.getLogger(ServidoresController.class);

    /**
     * Listagem de TODOS os Usuarios cadastrados no Banco de dados.
     * Caso desejar ordenar por nome em ordem alfabetica, 
     * passar o valor TRUE senao FALSE
     *
     * @param modelo - Objeto Model para injetar dados na View
     * @param ordenar - Verdadeiro se desejar ordenar os nomes em ordem alfabetica
     * @return String Padrao Spring para redirecionar a uma pagina
     */
    @RequestMapping("/listar/{ordenar}")
    public String listagem(Model modelo, @PathVariable boolean ordenar) {
        logger.info("Servico de Solicitacao para listar todos os usuarios ...");
        modelo.addAttribute("usuariosList",usuariosService.listar(ordenar));
        return"/dashboard/usuarios-list";
    }

    /**
     * Listagem PAGINADA de TODOS os Usuarios cadastrados no Banco de dados.
     *
     * @param page     -  numero da pagina solicitada
     * @param pageSize -  total de itens para apresentar na pagina
     * @return String Padrao Spring para redirecionar a uma pagina
     */
    @RequestMapping("/paginar/{page}/{pageSize}")
    public ModelAndView listar( @PathVariable int page, @PathVariable int pageSize ) {
        logger.info("Servico de Solicitacao para listar os usuarios PAGINADOS ...");
        ModelAndView mv = new ModelAndView("/dashboard/usuarios");
        mv.addObject("usuariosList", usuariosService.paginar(page, pageSize));
        return mv;
    }   
    
    /**
     * Ativa a conta do usuario apos o mesmo clicar no link recebido via email
     * 
     * Este medodo confirma o email e ativa a conta cadastrada no sistema.
     * 
     * @param token - Token UUID gerado pelo sistema para validar conta/email
     * @param model - Objeto do Thymeleaf para renderizar a pagina ao usuario
     * @param locale - Objeto de internacionalizacao da aplicacao
     * @return String contendo o nome da pagina para o Thymeleaf renderizar
     */
    @GetMapping("/uuid/{token}")
    public String ativarContaByUUID(@PathVariable UUID token, Model model, Locale locale) {
        logger.info("Requisição para ativar uma conta com o UUID: {}", token);
        model.addAttribute("errorPage1", i18svc.buscarMensagem("login.ativar.falhou", locale));
        model.addAttribute("errorOrigem", "Public");
        Optional<UsuarioVerificador> solicitado = usuariosService.buscarVerificadorByUUID(token);
        if( solicitado.isPresent() ) {
            UsuarioVerificador verificador = solicitado.get();
            if( verificador.getValidade().compareTo(Instant.now()) < 0 ) {
                // EXPIROU O TOKEN:
                logger.info("Ativacao da Conta - FALHOU!! - Token expirado");
                model.addAttribute("errorType", "NOTFOUND");
            } else {
                // TOKEN VALIDO
                Colaborador usuario = usuariosService.buscar( verificador.getUsuario().getEntidadeID() ).get();
                usuario.setAtivo(true);
                usuariosService.salvarColaborador(usuario);
                model.addAttribute("errorType", "SUCESSO");
                model.addAttribute("errorOrigem", "Public");
                model.addAttribute("errorPage1", i18svc.buscarMensagem("login.ativar.conta.1", locale));
                model.addAttribute("errorPage2", i18svc.buscarMensagem("login.ativar.conta.2", locale));
                logger.info("Ativacao da Conta {} - Realizada com Sucesso!", usuario.getContaEmail());
            }
            usuariosService.deletarVerificador(verificador);
        } else {
            logger.info("Ativacao da Conta - FALHOU!! - TOKEN nao encontrado");
            model.addAttribute("errorType", "NOTFOUND");
        }
        return "error";
    }

    /**
     * Envia um email para que o usuario ative sua conta confirmando assim o email utilizado
     * no seu cadastro no sistema (usado para o login). 
     * 
     * @param id  - ID do usuario no banco de dados
     * @param modelView - objeto de ambiente do Spring
     * @param request - objeto de contexto HTTP manipulado pelo Spring
     * @return - String para o Spring retornar pro Dashboard
     */
    @GetMapping("/ativar/{id}")
    public String ativarConta(@PathVariable("id") long id, Model modelView, HttpServletRequest request){
        logger.info("Servico de Ativacao de Contas ...");
        String baseUrl = request.getRequestURL().toString()
            .replace(request.getRequestURI(), request.getContextPath());

        gerarNovoTokenByEmail(id, modelView, baseUrl);
        return "redirect:/usuario/paginar/1/10";

    }
    
    /**
     * Desativa uma conta de usuario no sistema
     * 
     * @param id - ID do usuario no Banco de Dados
     * @return - String para o Spring enviar para o Dashboard
     */
    @GetMapping("/desativar/{id}")
    public String desativarConta(@PathVariable("id") long id){
        logger.info("Servico de DESATIVACAO do Status de uma Conta ...");
        Optional<Colaborador> userSolicitado = usuariosService.buscar(id);
        if( userSolicitado.isPresent() ) {
            userSolicitado.get().setAtivo(false);
            usuariosService.salvarColaborador(userSolicitado.get());
        }        
         return "redirect:/usuario/paginar/1/10";
    }
    
    /**
     * Apresenta o FORMULARIO para atualizar um Colaborador na base de dados.
     * NAO ATUALIZA A SENHA DO USUARIO.
     * 
     * @param id - ID do objeto a ser atualizado
     * @param modelo - objeto de manipulacao da view pelo Spring
     * @return String Padrao Spring para redirecionar a uma pagina
     */
    @GetMapping("/atualizar/{id}")
    @PreAuthorize("hasAuthority('Administrador') or (hasAuthority('Gerente') and #id == principal.id)")
    public String atualizar( @PathVariable("id") long id, Model modelo ) {
        Colaborador usuario;
        logger.info("Recebida requisicao para editar um Colaborador...");
        Optional<Colaborador> userSolicitado = usuariosService.buscar(id);
        if( userSolicitado.isPresent() ) {
            usuario = userSolicitado.get();
            // cria o DTO com os atributos a serem editados:
            ColaboradorDTO dto = new ColaboradorDTO(  usuario.getEntidadeID(), usuario.isAtivo(), 
                                                      usuario.getNomePessoal(),usuario.getContaEmail(),
                                                      usuario.getNivelAcesso() != null ? usuario.getNivelAcesso().getEntidadeID() : null );
            
            modelo.addAttribute("colaboradorDTO", dto);
            modelo.addAttribute("permissoesList", permissaoService.listar(true));
        } else {
            logger.info("Requisicao recebida: ALTERÇÃO NÃO REALIZADA - Referencia Invalida! ");
            return "redirect:/usuario/paginar/1/10";
        }
        logger.info("APRESENTANDO Formulário de Edição para alteracao de " + usuario.getNomePessoal());
        return "/dashboard/usuario/editar"; 
    }
    
    /**
     * Atualiza as informacoes de um Colaborador da base de dados
     * NAO ATUALIZA A SENHA DO USUARIO.
     * 
     * @param dto     - RECORD com os dados parciais a serem atualizados
     * @param result  - Objeto com o Result da Requisicao HTTP 
     * @param modelo  - objeto de manipulacao da view pelo Spring
     * @param request - objeto de contexto HTTP manipulado pelo Spring
     * @return String - Padrao Spring para redirecionar a uma pagina
     */
    @PostMapping("/atualizar")
    @PreAuthorize("hasAuthority('Administrador') or (hasAuthority('Gerente') and #dto.entidadeID == principal.id)")
    public String atualizar(
            @Valid @ModelAttribute("colaboradorDTO") ColaboradorDTO dto,
            BindingResult result, Model modelo, HttpServletRequest request) { 
        logger.info("Tratando requisicao para ATUALIZAR um Colaborador...");
        String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath());
        if (result.hasErrors()) {
            modelo.addAttribute("permissoesList", permissaoService.listar(true));
            logger.info("Falha no processamento: Objeto recebido nao respeita os parametros de VALIDACAO.");
            return "/dashboard/usuario/editar";
        }

        boolean atualizouMail = usuariosService.atualizarDados(dto);
        if( atualizouMail ) {
            gerarNovoTokenByEmail(dto.entidadeID(), modelo, baseUrl);
        }
        logger.info("Atualizacao realizada com sucesso para o usuario {}.", dto.nomePessoal());
        return "redirect:/usuario/paginar/1/10";
    }

    /**
     * FORMULARIO para atualizar a SENHA do Colaborador.
     * ATUALIZA SOMENTE A SENHA DO USUARIO.
     * 
     * @param id      - ID do usuario que pediu a mudanca da senha 
     * @param modelo  - objeto de manipulacao da view pelo Spring
     * @return String - Padrao Spring para redirecionar a uma pagina
     */
    @GetMapping("/senha/{id}")
    public String atualizarSenha( @PathVariable("id") long id, Model modelo ) {
        logger.info("APRESENTANDO Formulário de ALTERACAO da senha do usuario ID = {}.", id);
        modelo.addAttribute("entidadeID", id);
        return "/dashboard/usuario/senha"; 
    }

    /**
     * ATUALIZA a SENHA do Colaborador no Banco de Dados.
     * ATUALIZA SOMENTE A SENHA DO USUARIO. A senha sera criptografada no SERVICE.
     * 
     * @param entidadeID     - ID do usuario que pediu a mudanca da senha 
     * @param novaSenha      - NOVA senha digitada no formulario
     * @param confirmarSenha - Nova senha CONFIRMADA no formulario
     * @param modelo         - objeto de manipulacao da view pelo Spring
     * @return String        - Padrao Spring para redirecionar a uma pagina
     */
    @PostMapping("/senha")
    @PreAuthorize("hasAuthority('Administrador') or principal.id == #entidadeID")
    public String atualizarSenha(Long entidadeID, String novaSenha, String confirmarSenha, Model modelo) {
        logger.info("Tratando requisicao para ATUALIZAR a SENHA de um Colaborador...");
        if( novaSenha.isBlank() || novaSenha.isEmpty() ) {
            modelo.addAttribute("mensagem", "Informe senhas válidas e seguras!");
            logger.info("Retornando para o formulario: Senha em branco!");
            modelo.addAttribute("entidadeID", entidadeID);
            return "/dashboard/usuario/senha"; 
        }
        if( !novaSenha.trim().equals(confirmarSenha.trim()) ) {
            modelo.addAttribute("mensagem", "Sua confirmação da senha não confere!! Verifique!");
            logger.info("Retornando para o formulario: Senhas digitadas não conferem!");
            modelo.addAttribute("entidadeID", entidadeID);
            return "/dashboard/usuario/senha";
        }
        Optional<Colaborador> userSolicitado = usuariosService.buscar(entidadeID);
        if( userSolicitado.isPresent() ) {
            logger.info("Encontrou Colaborador com nome: {} - Alterando sua senha!! ", userSolicitado.get().getNomePessoal());
            userSolicitado.get().setCodigoAcesso(novaSenha.trim());
            userSolicitado.get().setConfirmarSenha(confirmarSenha.trim());
            usuariosService.atualizar(userSolicitado.get());
        } else {
            logger.info("Processando requisicao: ALTERÇÃO NÃO REALIZADA - Referencia Invalida! ");
        }
        return "redirect:/usuario/paginar/1/10";
    }
    
    /**
     * Deleta um Colaborador da Base de Dados do sistema.
     * 
     * @param id     - ID do usuario na base de dados para deletar
     * @param modelo - Model para encaminhar a View
     * @return       - Redireciona para a Listagem Paginada dos Usuarios
     */
    @GetMapping("/excluir/{id}")
    @PreAuthorize("hasAuthority('Administrador') or (hasAuthority('Gerente') and #id == principal.id)")
    public String excluirColaborador( @PathVariable("id") long id, Model modelo) {
        logger.info("Requisicao recebida: EXCLUIR USUARIO: " + id);
        Optional<Colaborador> userSolicitado = usuariosService.buscar(id);
        if( userSolicitado.isPresent() ) {
            logger.info("Encontrou Colaborador com nome: " + userSolicitado.get().getNomePessoal());
            usuariosService.remover(userSolicitado.get());
        } else {
            logger.info("Processando requisicao: ALTERÇÃO NÃO REALIZADA - Referencia Invalida! ");
        }
        return "redirect:/usuario/paginar/1/10";
    }
    

    private void gerarNovoTokenByEmail(long id, Model modelView, String baseUrl) {
        Optional<Colaborador> userSolicitado = usuariosService.buscar(id);
        if( userSolicitado.isPresent() ) {
            logger.info("Encontrou Colaborador com nome: " + userSolicitado.get().getNomePessoal());
           
            // cria ou atualiza o token para validar a senha:
            UsuarioVerificador verificador;
            Optional<UsuarioVerificador> userVerificador = usuariosService.buscarVerificador(userSolicitado.get().getEntidadeID());
            if( userVerificador.isPresent() ) {
                logger.info("Encontrou um Token, validando por mais 25 minutos... " );
                verificador = userVerificador.get();
            } else {
                logger.info("Criando um novo Token para o usuario ... " );
                verificador = new UsuarioVerificador();
                verificador.setVerificadorID(null);
            }
            verificador.setUsuario(userSolicitado.get());
            verificador.setCodigoUUID(UUID.randomUUID());
            verificador.setValidade(Instant.now().plusMillis(1500000));
            usuariosService.atualizarVerificador(verificador);
            
            try{ 
                EmailMessageDTO mensagem = new EmailMessageDTO(userSolicitado.get().getContaEmail(), null, userSolicitado.get().getNomePessoal(), "Ativação de Conta no CachaBOT", "");
                logger.info("Enviando email para ativacao da conta... " );
                mailService.sendTemplateEmail(mensagem, "dashboard/email/ativarConta", modelView, baseUrl, verificador.getCodigoUUID().toString().trim());
            } catch(MessagingException ex) {
                logger.info("Encontrou no envio do email: " + ex.getMessage());
            }
        } else {
            logger.info("Processando requisicao: ALTERÇÃO NÃO REALIZADA - Referencia Invalida! ");
        }
    }
    
}
/*                    End of Class                                            */