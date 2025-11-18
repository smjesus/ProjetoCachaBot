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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.util.List;

/**
 *  Objeto base NivelAcesso (Niveis de usuarios).
 *
 * Esta classe representa um nivel de um usuario no sistema.
 * 
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
@Entity
@Table(name = "NivelAcesso")
public class NivelAcesso {
    private static final long serialVersionUID = 3L;
    
    @Id
    @Basic(optional = false)
    @Column(name = "entidadeID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long entidadeID;
    
    @Column(name = "nome")
    private String nome;

    @OneToMany(mappedBy = "nivelAcesso")
    private List<Colaborador> colaboradores;

    public NivelAcesso() {
    }

    public NivelAcesso(Long entidadeID, String nome) {
        this.entidadeID = entidadeID;
        this.nome = nome;
    }
    
    @Version
    @Column(name = "versao")
    private Long versao;
    
    /**
     * @return long contendo o Serial Version UID do Objeto
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * @return Long contendo o Identificador unico do Nivel de Acesso
     */
    public Long getEntidadeID() {
        return entidadeID;
    }

    /**
     * @param entidadeID Identificador unico do Nivel de Acesso
     */
    public void setEntidadeID(Long entidadeID) {
        this.entidadeID = entidadeID;
    }

    /**
     * @return String contendo o Nome do Nivel de Acesso
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome Nome do Nivel de Acesso
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * @return the colaboradores
     */
    public List<Colaborador> getColaboradores() {
        return colaboradores;
    }

    /**
     * @param colaboradores the colaboradores to set
     */
    public void setColaboradores(List<Colaborador> colaboradores) {
        this.colaboradores = colaboradores;
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
        return this.getNome() + "[ID=" + this.getEntidadeID() + "]";
    }
    
}
/*                    End of Class                                            */