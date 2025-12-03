/**
 * Projeto:  CachaBot - BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2024
 * Equipe:   Murilo, Victor, Allan
 */
package br.com.aeroceti.cachaBot.entidades;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;
import jakarta.persistence.Id;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import br.com.aeroceti.cachaBot.componentes.SenhasIguais;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

/**
 *  Objeto base Colaborador (Usuario do Sistema).
 *
 * Esta classe representa um usuario no sistema.
 * 
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
@Entity
@SenhasIguais
@Table(name = "Colaborador")
public class Colaborador {
    
    private static final long serialVersionUID = 4L;
    
    @Id
    @Basic(optional = false)
    @Column(name = "entidadeID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long entidadeID;
    
    @Column(name = "nomePessoal")
    private String nomePessoal;
    
    @NotNull(message = "{form.user.nome.notnull}")
    @NotBlank(message = "{form.user.email.notblank}")  @Email(message = "{form.user.email.notvalid}")
    @Pattern(
        regexp = "^[^@]+@[^@]+(\\.[a-zA-Z]{2,})+$",
        message = "{form.user.email.notcomplete}"
    )            
    @Column(name = "contaEmail")
    private String contaEmail;
    
    @NotBlank(message = "{form.user.password.notblank}")
    @Size(min = 6, message = "{form.user.password.valid}")
    @Column(name = "codigoAcesso")
    private String codigoAcesso;
    
    @Column(name = "ativo")
    private boolean ativo;

    @ManyToOne
    @JoinColumn(name = "nivelAcesso", nullable = true)
    private NivelAcesso nivelAcesso;
    
    @OneToMany(mappedBy = "colaborador", cascade = CascadeType.PERSIST)
    private List<Servidor> servidores = new ArrayList<>();

    @Version
    @Column(name = "versao")
    private Long versao;

    @Transient // não é salvo no banco
    private String confirmarSenha;
    
    /**
     * @return the serialVersionUID
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * @return long contendo o Serial Version UID do Objeto
     */
    public Long getEntidadeID() {
        return entidadeID;
    }

    /**
     * @param  entidadeID contendo o Identificador unico do Usuario
     */
    public void setEntidadeID(Long entidadeID) {
        this.entidadeID = entidadeID;
    }

    /**
     * @return String contendo o Nome Pessoal do Usuario
     */
    public String getNomePessoal() {
        return nomePessoal;
    }

    /**
     * @param nome String contendo o Nome Pessoal do Usuario
     */
    public void setNomePessoal(String nome) {
        this.nomePessoal = nome;
    }

    /**
     * @return String contendo a Senha do Usuario
     */
    public String getCodigoAcesso() {
        return codigoAcesso;
    }

    /**
     * @param codigoAcesso String contendo a Senha do Usuario
     */
    public void setCodigoAcesso(String codigoAcesso) {
        this.codigoAcesso = codigoAcesso;
    }

    /**
     * @return String contendo o e-mail do Usuario
     */
    public String getContaEmail() {
        return contaEmail;
    }

    /**
     * @param contaEmail String contendo o e-mail do Usuario
     */
    public void setContaEmail(String contaEmail) {
        this.contaEmail = contaEmail;
    }

    /**
     * @return TRUE ou FALSE conforme esteja Ativo ou nao o Usuario no sistema
     */
    public boolean isAtivo() {
        return ativo;
    }

    /**
     * @param situacao Booleano contendo o Status do Usuario no sistema
     */
    public void setAtivo(boolean situacao) {
        this.ativo = situacao;
    }

    /**
     * @return Objeto NivelAcesso representando o Nivel do Usuario no sistema
     */
    public NivelAcesso getNivelAcesso() {
        return nivelAcesso;
    }

    /**
     * @param nivelAcesso Objeto representando o Nivel do Usuario no sistema
     */
    public void setNivelAcesso(NivelAcesso nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
    }
    
    /**
     * @return the versao
     */
    public Long getVersao() {
        return versao;
    }

    /**
     * @param versao the versao to set
     */
    public void setVersao(Long versao) {
        this.versao = versao;
    }

    /**
     * @return the servidores
     */
    public List<Servidor> getServidores() {
        return servidores;
    }

    /**
     * @param servidores the servidores to set
     */
    public void setServidores(List<Servidor> servidores) {
        this.servidores = servidores;
    }

    public void setConfirmarSenha(String confirmarSenha) {
        this.confirmarSenha = confirmarSenha;
    }

    public String getConfirmarSenha() {
        return confirmarSenha;
    }

    public void addServidor(Servidor server) {
        this.servidores.add(server);
        server.setColaborador(this);
    }

    public void removeServidor(Servidor server) {
        this.servidores.remove(server);
        server.setColaborador(null);
    }
    
    @Override
    public String toString() {
        return this.nomePessoal + "[ID=" + this.entidadeID + "]";
    }

    @Override
    public boolean equals(Object obj) {
        // 1. Verificação de referência (se é o mesmo objeto na memória)
        if (this == obj) {
            return true;
        }
        // 2. Verificação de nulidade do objeto 'obj' e compatibilidade de classe
        if (!(obj instanceof Colaborador)) {
            return false;
        }
        // 3. Comparação dos atributos usando Objects.equals() para segurança contra NullPointerException
        Colaborador other = (Colaborador) obj;
        return Objects.equals(this.getEntidadeID(), other.getEntidadeID());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getEntidadeID());
    }

}
/*                    End of Class                                            */