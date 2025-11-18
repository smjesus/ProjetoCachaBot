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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/**
 *  Objeto base Verificador (validacao de contas).
 *
 * Esta classe representa um UUID para verificar a conta do usuario.
 * 
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
@Entity
@Table(name = "UsuarioVerificador")
public class UsuarioVerificador {
    private static final long serialVersionUID = 5L;
    
    @Id
    @Basic(optional = false)
    @Column(name = "verificadorID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long verificadorID;
    
    private UUID codigoUUID;
    
    private Instant validade;
    
    @OneToOne(orphanRemoval = false)
    @JoinColumn(name = "usuario", referencedColumnName = "entidadeID", unique = true)
    private Colaborador usuario;

    /**
     * @return the serialVersionUID
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * @return the verificadorID
     */
    public Long getVerificadorID() {
        return verificadorID;
    }

    /**
     * @param verificadorID the verificadorID to set
     */
    public void setVerificadorID(Long verificadorID) {
        this.verificadorID = verificadorID;
    }

    /**
     * @return the codigoUUID
     */
    public UUID getCodigoUUID() {
        return codigoUUID;
    }

    /**
     * @param codigoUUID the codigoUUID to set
     */
    public void setCodigoUUID(UUID codigoUUID) {
        this.codigoUUID = codigoUUID;
    }

    /**
     * @return the validade
     */
    public Instant getValidade() {
        return validade;
    }

    /**
     * @param validade the validade to set
     */
    public void setValidade(Instant validade) {
        this.validade = validade;
    }

    /**
     * @return the usuario
     */
    public Colaborador getUsuario() {
        return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(Colaborador usuario) {
        this.usuario = usuario;
    }
    
    
}
/*                    End of Class                                            */