package br.com.sp.nava.cep.search.api.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import br.com.sp.nava.cep.search.api.dto.out.ViaCepResponseDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(
        name = "enderecos",
        indexes = {
                @Index(name = "idx_enderecos_cep", columnList = "cep"),
                @Index(name = "idx_enderecos_uf", columnList = "uf"),
                @Index(name = "idx_enderecos_localidade", columnList = "localidade"),
                @Index(name = "idx_enderecos_ultima_consulta", columnList = "ultima_consulta")
        }
)
public class EnderecoEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "cep", nullable = false, unique = true, length = 8)
    private String cep;

    @Column(name = "logradouro", length = 255)
    private String logradouro;

    @Column(name = "complemento", length = 255)
    private String complemento;

    @Column(name = "unidade", length = 100)
    private String unidade;

    @Column(name = "bairro", length = 150)
    private String bairro;

    @Column(name = "localidade", length = 150)
    private String localidade;

    @Column(name = "uf", length = 2)
    private String uf;

    @Column(name = "estado", length = 100)
    private String estado;

    @Column(name = "regiao", length = 50)
    private String regiao;

    @Column(name = "ibge", length = 20)
    private String ibge;

    @Column(name = "gia", length = 20)
    private String gia;

    @Column(name = "ddd", length = 5)
    private String ddd;

    @Column(name = "siafi", length = 20)
    private String siafi;

    @Column(name = "data_registro", nullable = false, updatable = false)
    private LocalDateTime dataRegistro;

    @Column(name = "ultima_consulta", nullable = false)
    private LocalDateTime ultimaConsulta;
    
    public EnderecoEntity() {
		// Constructor default JPA
	}

    public EnderecoEntity(ViaCepResponseDto enderecoDto) {
    	this.id = UUID.randomUUID();
        this.cep = normalizarCep(enderecoDto.cep());
        this.logradouro = enderecoDto.logradouro();
        this.complemento = enderecoDto.complemento();
        this.unidade = enderecoDto.unidade();
        this.bairro = enderecoDto.bairro();
        this.localidade = enderecoDto.localidade();
        this.uf = enderecoDto.uf();
        this.estado = enderecoDto.estado();
        this.regiao = enderecoDto.regiao();
        this.ibge = enderecoDto.ibge();
        this.gia = enderecoDto.gia();
        this.ddd = enderecoDto.ddd();
        this.siafi = enderecoDto.siafi();
        this.dataRegistro = LocalDateTime.now();
        this.ultimaConsulta = LocalDateTime.now();
    }
    
    //Helpers
    
    //nesse regex eu tiro tudo que é número
    private String normalizarCep(String cep) {
        return cep == null ? null : cep.replaceAll("\\D", "");
    }
    
    //Getters e Setters

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getLocalidade() {
		return localidade;
	}

	public void setLocalidade(String localidade) {
		this.localidade = localidade;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getRegiao() {
		return regiao;
	}

	public void setRegiao(String regiao) {
		this.regiao = regiao;
	}

	public String getIbge() {
		return ibge;
	}

	public void setIbge(String ibge) {
		this.ibge = ibge;
	}

	public String getGia() {
		return gia;
	}

	public void setGia(String gia) {
		this.gia = gia;
	}

	public String getDdd() {
		return ddd;
	}

	public void setDdd(String ddd) {
		this.ddd = ddd;
	}

	public String getSiafi() {
		return siafi;
	}

	public void setSiafi(String siafi) {
		this.siafi = siafi;
	}

	public LocalDateTime getDataRegistro() {
		return dataRegistro;
	}

	public void setDataRegistro(LocalDateTime dataRegistro) {
		this.dataRegistro = dataRegistro;
	}

	public LocalDateTime getUltimaConsulta() {
		return ultimaConsulta;
	}

	public void setUltimaConsulta(LocalDateTime ultimaConsulta) {
		this.ultimaConsulta = ultimaConsulta;
	}

	@Override
	public int hashCode() {
		return Objects.hash(cep, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EnderecoEntity other = (EnderecoEntity) obj;
		return Objects.equals(cep, other.cep) && Objects.equals(id, other.id);
	}
    
    
}
