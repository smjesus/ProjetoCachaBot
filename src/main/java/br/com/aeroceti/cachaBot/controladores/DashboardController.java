/**
 * Projeto:  CachaBot - BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2023
 * Equipe:   Murilo, Victor
 */
package br.com.aeroceti.cachaBot.controladores;

import br.com.aeroceti.cachaBot.componentes.Sitemap;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Controller;
import br.com.aeroceti.cachaBot.entidades.dto.EmailMessageDTO;
import org.springframework.web.bind.annotation.GetMapping;
import br.com.aeroceti.cachaBot.servicos.I18nService;
import br.com.aeroceti.cachaBot.servicos.MailSenderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller para a apresentacao do Dashboard do sistema.
 * 
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
@Controller
public class DashboardController {

    @Autowired
    private I18nService i18svc;
    
    @Autowired
    private MailSenderService mailSender;
    
    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    Logger logger = LoggerFactory.getLogger(DashboardController.class);

    /*--------------------------------------------------------------------------
     *                 PAGINAS DE ACESSO PUBLICO
     *--------------------------------------------------------------------------
     */

    @GetMapping("/")
    @Sitemap(priority = 1.0, changefreq = "daily")
    public String getHomePage(Model model){
        logger.info("Redirecionando view para Pagina Inicial...");
        return "index";        
    }   

    @GetMapping( {"/googlea3e4f03a4569e38f", "/googlea3e4f03a4569e38f.html"})
    public String google(){
        logger.info("Respondendo ao Google ...");
        return "googlea3e4f03a4569e38f";        
    }   
    
    @ResponseBody
    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String sitemap() {
        Map<RequestMappingInfo, HandlerMethod> map = handlerMapping.getHandlerMethods();
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"            \n");
        xml.append("        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"          \n");
        xml.append("        xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 \n"); 
        xml.append("            http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\">   \n");
        
        map.forEach((info, handler) -> {

            Sitemap sitemap = handler.getMethodAnnotation(Sitemap.class);
            if (sitemap == null) return;

            info.getPathPatternsCondition().getPatterns().forEach(pattern -> {

                String path = pattern.getPatternString();

                xml.append("""
                    <url>
                        <loc>https://cachabot.aeroceti.com.br%s</loc>
                        <changefreq>%s</changefreq>
                        <priority>%s</priority>
                    </url>
                """.formatted(
                    path,
                    sitemap.changefreq(),
                    sitemap.priority()
                ));
            });
        });
        
        xml.append("</urlset>");
        logger.info("Apresentando o SiteMap ...");
        return xml.toString();        
    }    
    
    @GetMapping("/login")
    public String getLoginPage(HttpServletRequest request, Model model){
        logger.info("Redirecionando view para pagina de Login ...");
        HttpSession session = request.getSession(false);

        if (session != null) {
            Object msg = session.getAttribute("LOGIN_ERROR_MESSAGE");
            if (msg != null) {
                model.addAttribute("loginError", msg);
                session.removeAttribute("LOGIN_ERROR_MESSAGE"); 
            }
        }
        return "login";
    }

    @GetMapping("/termos")
    @Sitemap(priority = 1.0, changefreq = "daily")
    public String getTermos(Model model, Locale locale){
        logger.info("Redirecionando view para pagina dos Termos de Uso ...");
        switch(locale.getLanguage()) {
                case "en" -> {
                    return "i18n/termos_en";
            }
                case "pt" -> {
                    return "i18n/termos_pt";
            }
        }
        return "i18n/termos_en";  
    }
    
    @GetMapping("/privacidade")
    @Sitemap(priority = 1.0, changefreq = "daily")
    public String getPrivacidade(Model model, Locale locale){
        logger.info("Redirecionando view para pagina da Politica de Privacidade ...");
        switch(locale.getLanguage()) {
                case "en" -> {
                    return "i18n/privacidade_en";
            }
                case "pt" -> {
                    return "i18n/privacidade_pt";
            }
        }
        return "i18n/privacidade_en"; 
    }
    
    @GetMapping("/documentacao")
    @Sitemap(priority = 1.0, changefreq = "daily")
    public String getDocumentation(Model model, Locale locale){
        logger.info("Redirecionando view para pagina de Documentacao ...");
        switch(locale.getLanguage()) {
                case "en" -> {
                    return "i18n/manual_en";
            }
                case "pt" -> {
                    return "i18n/manual_pt";
            }
        }
        return "i18n/manual_en"; 
    }

    @PostMapping("/faleconosco")
    @Sitemap(priority = 1.0, changefreq = "daily")
    public String enviarMensagem(   @RequestParam String nome, @RequestParam String telefone,
                                    @RequestParam String email, @RequestParam String mensagem, RedirectAttributes ra){
        
        logger.info("Tratando dados para enviar mensagem do Fale Conosco ...");
        String corpo = String.format("""
                <html><header><title>Fale Conosco - CachaBOT</title></header><body>
                <h3>Informa&ccedil;&otilde;es do Contato:</h3>
                <p>Nome: %s</p>
                <p>Telefone: %s</p>
                <p>Email: %s</p>
                <hr>
                <p>%s</p>
                <hr>
                <small>(c)AeroCETI - CachaBOT - 2025.</small>
                </body></html>
                """, nome, telefone, email, mensagem);

        try {        
            EmailMessageDTO mailMessage = new EmailMessageDTO(email, null, nome, "Formulário de Contado do CachaBOT", corpo);
            logger.info("Enviando email de contado do Site CacahBOT produzido por {} ({}).", nome, email );
            mailSender.sendHtmlEmail(mailMessage);
            // mensagem de sucesso na View:
              ra.addFlashAttribute("sucessoContato", "contato.sucesso");
        } catch (MessagingException e) {
            logger.info("Falha no Envio da mensagem de contato: {}", e.getMessage());
            // mensagem de erro para a View:
            ra.addFlashAttribute("erroContato", "contato.falha");
        }
        logger.info("Tratamento da mensagem e solicitacao de envio concluidos ...");
        return "redirect:/";
    }
    
    
    /*--------------------------------------------------------------------------
     *                 PAGINAS DE ACESSO RESTRITO
     *--------------------------------------------------------------------------
     */

    @GetMapping("/dashboard")
    public String getDashboard(Model model){
        logger.info("Redirecionando view para o Dashboard ...");
        return "dashboard";
    }
        
}
/*                    End of Class                                            */