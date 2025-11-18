/**
 * Projeto:  CachaBot - BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2024
 * Equipe:   Murilo, Victor, Allan
 */
package br.com.aeroceti.cachaBot.entidades;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.io.Serializable;

/**
 *  Objeto Mensagem de Boas Vindas.
 *
 * Esta classe representa os parametros de uma mensagem de boas vindas.
 * 
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
@Entity
@Table(name = "BoasVindas")
public class MensagemBoasVindas  implements Serializable {
    
    private static final long serialVersionUID = 2L;
    
    @Id
    @Basic(optional = false)
    @Column(name = "mensagemID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mensagemID;
    
    @Column(name = "titulo")
    private String titulo;
    
    @Lob
    @Column(name = "corpo", length=2048)
    private String corpo;

    @Column(name = "linha")
    private String linha = "";

    @Column(name = "icone")
    private String icone = "";

    @Column(name = "cor")
    private String cor = "BRANCA";

    @Version
    @Column(name = "versao")
    private Long versao;

    public MensagemBoasVindas() {
    }

    public MensagemBoasVindas(Long mensagemID, String titulo, String corpo, Long versao) {
        this.mensagemID = mensagemID;
        this.titulo = titulo;
        this.corpo = corpo;
        this.versao = versao;
    }
    
    /**
     * @return the serialVersionUID
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * @return the mensagemID
     */
    public Long getMensagemID() {
        return mensagemID;
    }

    /**
     * @param mensagemID the mensagemID to set
     */
    public void setMensagemID(Long mensagemID) {
        this.mensagemID = mensagemID;
    }

    /**
     * @return the titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * @param titulo the titulo to set
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * @return the corpo
     */
    public String getCorpo() {
        return corpo;
    }

    /**
     * @param corpo the corpo to set
     */
    public void setCorpo(String corpo) {
        this.corpo = corpo;
    }

    /**
     * @return the linha
     */
    public String getLinha() {
        return linha;
    }

    /**
     * @param linha the linha to set
     */
    public void setLinha(String linha) {
        this.linha = linha;
    }

    /**
     * @return the icone
     */
    public String getIcone() {
        return icone;
    }

    /**
     * @param icone the icone to set
     */
    public void setIcone(String icone) {
        this.icone = icone;
    }

    /**
     * @return the cor
     */
    public String getCor() {
        return cor;
    }

    /**
     * @param cor the cor to set
     */
    public void setCor(String cor) {
        this.cor = cor;
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

    @Override
    public String toString() {
        return this.titulo + "(Versao: " + this.getVersao() + ")";
    }

}
/*                    End of Class                                            */