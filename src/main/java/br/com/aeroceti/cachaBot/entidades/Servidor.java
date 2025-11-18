/**
 * Projeto:  CachaBot - BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2024
 * Equipe:   Murilo, Victor, Allan
 */
package br.com.aeroceti.cachaBot.entidades;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.io.Serializable;

/**
 *  Objeto base Rules (Niveis de usuarios).
 *
 * Esta classe representa um nivel de um usuario no sistema.
 * 
 * @author Sergio Murilo - smurilo at Gmail.com
 * @version 1.0
 */
@Entity
@Table(name = "Servidor")
public class Servidor  implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @Column(name = "entidadeID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long entidadeID;
    
    @Column(name = "servidorID")
    private Long servidorID;
    
    @Column(name = "nome")
    private String nome;
    
    @Column(name = "channelTicket")
    private Long channelTicket = 0L;
    
    @Column(name = "prefixCommand")
    private char prefixCommand = '!';

    @Column(name = "cadastro")
    private boolean cadastro = false;

    @Column(name = "entryPointAPI")
    private String entryPointAPI = "EntryPointAPI";

    @Column(name = "tokenAuthAPI")
    private String tokenAuthAPI = "Token-Auth-API";

    @Column(name = "helpCadastroPage")
    private String helpCadastroPage = "https://www.google.com/";

    @Column(name = "cargoPadrao")
    private String cargoPadrao = "CargoINICIAL";
    
    @Column(name = "cargoDefinitivo")
    private String cargoDefinitivo = "CargoDEFINITIVO";
    
    @Column(name = "cargoSuporte")
    private String cargoSuporte = "CargoSUPORTE";
    
    @Column(name = "salaBoasVindas")
    private String salaBoasVindas = "Sala-Boas-Vindas";

    @Column(name = "msgParticipacao")
    private String msgParticipacao = "Obrigado pela sua participacao!";

    @Column(name = "ativo")
    private boolean ativo = false;
    
    @Version
    @Column(name = "versao")
    private Long versao;
    
    @ManyToOne
    @JoinColumn(name = "colaborador", referencedColumnName = "entidadeID", nullable = true)
    private Colaborador colaborador;
    
    @OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "boasVindas", referencedColumnName = "mensagemID")
    private MensagemBoasVindas boasVindas;

    public Servidor() {
    }

    /**
     * @return the serialVersionUID
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * @return the entidadeID
     */
    public Long getEntidadeID() {
        return entidadeID;
    }

    /**
     * @param entidadeID the entidadeID to set
     */
    public void setEntidadeID(Long entidadeID) {
        this.entidadeID = entidadeID;
    }

    /**
     * @return the servidorID
     */
    public Long getServidorID() {
        return servidorID;
    }

    /**
     * @param servidorID the servidorID to set
     */
    public void setServidorID(Long servidorID) {
        this.servidorID = servidorID;
    }

    /**
     * @return the channelTicket
     */
    public Long getChannelTicket() {
        return channelTicket;
    }

    /**
     * @param channelTicket the channelTicket to set
     */
    public void setChannelTicket(Long channelTicket) {
        this.channelTicket = channelTicket;
    }

    /**
     * @return the prefixCommand
     */
    public char getPrefixCommand() {
        return prefixCommand;
    }

    /**
     * @param prefixCommand the prefixCommand to set
     */
    public void setPrefixCommand(char prefixCommand) {
        this.prefixCommand = prefixCommand;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCargoPadrao() {
        return cargoPadrao;
    }

    public void setCargoPadrao(String cargoPadrao) {
        this.cargoPadrao = cargoPadrao;
    }

    public String getCargoDefinitivo() {
        return cargoDefinitivo;
    }

    public void setCargoDefinitivo(String cargoDefinitivo) {
        this.cargoDefinitivo = cargoDefinitivo;
    }

    public String getCargoSuporte() {
        return cargoSuporte;
    }

    public void setCargoSuporte(String cargoSuporte) {
        this.cargoSuporte = cargoSuporte;
    }

    /**
     * @return the cadastro
     */
    public boolean isCadastro() {
        return cadastro;
    }

    /**
     * @param cadastro the cadastro to set
     */
    public void setCadastro(boolean cadastro) {
        this.cadastro = cadastro;
    }

    /**
     * @return the entryPointAPI
     */
    public String getEntryPointAPI() {
        return entryPointAPI;
    }

    /**
     * @param entryPointAPI the entryPointAPI to set
     */
    public void setEntryPointAPI(String entryPointAPI) {
        this.entryPointAPI = entryPointAPI;
    }

    /**
     * @return the tokenAuthAPI
     */
    public String getTokenAuthAPI() {
        return tokenAuthAPI;
    }

    /**
     * @param tokenAuthAPI the tokenAuthAPI to set
     */
    public void setTokenAuthAPI(String tokenAuthAPI) {
        this.tokenAuthAPI = tokenAuthAPI;
    }

    /**
     * @return the helpCadastroPage
     */
    public String getHelpCadastroPage() {
        return helpCadastroPage;
    }

    /**
     * @param helpCadastroPage the helpCadastroPage to set
     */
    public void setHelpCadastroPage(String helpCadastroPage) {
        this.helpCadastroPage = helpCadastroPage;
    }

    /**
     * @return the boasVindas
     */
    public MensagemBoasVindas getBoasVindas() {
        return boasVindas;
    }

    /**
     * @param boasVindas the boasVindas to set
     */
    public void setBoasVindas(MensagemBoasVindas boasVindas) {
        this.boasVindas = boasVindas;
    }

    /**
     * @return the salaBoasVindas
     */
    public String getSalaBoasVindas() {
        return salaBoasVindas;
    }

    /**
     * @param salaBoasVindas the salaBoasVindas to set
     */
    public void setSalaBoasVindas(String salaBoasVindas) {
        this.salaBoasVindas = salaBoasVindas;
    }

    public String getMsgParticipacao() {
        return msgParticipacao;
    }

    public void setMsgParticipacao(String msgParticipacao) {
        this.msgParticipacao = msgParticipacao;
    }

    /**
     * @return the ownerServer
     */
    public Colaborador getColaborador() {
        return colaborador;
    }

    /**
     * @param ownerServer the ownerServer to set
     */
    public void setColaborador(Colaborador ownerServer) {
        this.colaborador = ownerServer;
    }

    /**
     * @param ativo the ativo to set
     */
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    /**
     * @return the ativo
     */
    public boolean isAtivo() {
        return ativo;
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
        return "Servidor " + this.nome + "(" + this.servidorID + ")";
    }

}
/*                    End of Class                                            */