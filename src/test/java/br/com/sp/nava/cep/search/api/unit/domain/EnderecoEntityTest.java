package br.com.sp.nava.cep.search.api.unit.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.sp.nava.cep.search.api.domain.EnderecoEntity;
import br.com.sp.nava.cep.search.api.dto.out.ViaCepResponseDto;

public class EnderecoEntityTest {

	private EnderecoEntity enderecoEntity;

	@Test
	@DisplayName("1.0 - Deve Testar Getters e Setters")
	public void deveTestarGettersSetters() {
		// Arrange
		enderecoEntity = new EnderecoEntity();
		UUID id = UUID.randomUUID();
		String cep = "01001000";
		String logradouro = "Praça da Sé";
		String complemento = "lado ímpar";
		String unidade = "Unidade Centro";
		String bairro = "Sé";
		String localidade = "São Paulo";
		String uf = "SP";
		String estado = "São Paulo";
		String regiao = "Sudeste";
		String ibge = "3550308";
		String gia = "1004";
		String ddd = "11";
		String siafi = "7107";
		LocalDateTime dataRegistro = LocalDateTime.of(2026, 6, 23, 10, 30);
		LocalDateTime ultimaConsulta = LocalDateTime.of(2026, 6, 23, 11, 45);

		// Act
		enderecoEntity.setId(id);
		enderecoEntity.setCep(cep);
		enderecoEntity.setLogradouro(logradouro);
		enderecoEntity.setComplemento(complemento);
		enderecoEntity.setUnidade(unidade);
		enderecoEntity.setBairro(bairro);
		enderecoEntity.setLocalidade(localidade);
		enderecoEntity.setUf(uf);
		enderecoEntity.setEstado(estado);
		enderecoEntity.setRegiao(regiao);
		enderecoEntity.setIbge(ibge);
		enderecoEntity.setGia(gia);
		enderecoEntity.setDdd(ddd);
		enderecoEntity.setSiafi(siafi);
		enderecoEntity.setDataRegistro(dataRegistro);
		enderecoEntity.setUltimaConsulta(ultimaConsulta);

		// Assert
		assertEquals(id, enderecoEntity.getId());
		assertEquals(cep, enderecoEntity.getCep());
		assertEquals(logradouro, enderecoEntity.getLogradouro());
		assertEquals(complemento, enderecoEntity.getComplemento());
		assertEquals(unidade, enderecoEntity.getUnidade());
		assertEquals(bairro, enderecoEntity.getBairro());
		assertEquals(localidade, enderecoEntity.getLocalidade());
		assertEquals(uf, enderecoEntity.getUf());
		assertEquals(estado, enderecoEntity.getEstado());
		assertEquals(regiao, enderecoEntity.getRegiao());
		assertEquals(ibge, enderecoEntity.getIbge());
		assertEquals(gia, enderecoEntity.getGia());
		assertEquals(ddd, enderecoEntity.getDdd());
		assertEquals(siafi, enderecoEntity.getSiafi());
		assertEquals(dataRegistro, enderecoEntity.getDataRegistro());
		assertEquals(ultimaConsulta, enderecoEntity.getUltimaConsulta());
	}
	
	@Test
	@DisplayName("1.1 - Deve Testar Construtor com Parâmetros")
	public void deveTestarConstrutorComParametros() {
		// Arrange
		String cep = "01001000";
		String logradouro = "Praça da Sé";
		String complemento = "lado ímpar";
		String unidade = "Unidade Centro";
		String bairro = "Sé";
		String localidade = "São Paulo";
		String uf = "SP";
		String estado = "São Paulo";
		String regiao = "Sudeste";
		String ibge = "3550308";
		String gia = "1004";
		String ddd = "11";
		String siafi = "7107";
		ViaCepResponseDto cepResponseDto = new ViaCepResponseDto(cep, logradouro, complemento, unidade, bairro, localidade, uf, estado, regiao, ibge, gia, ddd, siafi);
		
		// Act
		enderecoEntity = new EnderecoEntity(cepResponseDto);
		
		// Assert
		assertEquals(cep, enderecoEntity.getCep());
		assertEquals(logradouro, enderecoEntity.getLogradouro());
		assertEquals(complemento, enderecoEntity.getComplemento());
		assertEquals(unidade, enderecoEntity.getUnidade());
		assertEquals(bairro, enderecoEntity.getBairro());
		assertEquals(localidade, enderecoEntity.getLocalidade());
		assertEquals(uf, enderecoEntity.getUf());
		assertEquals(estado, enderecoEntity.getEstado());
		assertEquals(regiao, enderecoEntity.getRegiao());
		assertEquals(ibge, enderecoEntity.getIbge());
		assertEquals(gia, enderecoEntity.getGia());
		assertEquals(ddd, enderecoEntity.getDdd());
		assertEquals(siafi, enderecoEntity.getSiafi());
	}
	
	@Test
	@DisplayName("1.2 - Deve retornar CEP Normalizado")
	public void deveRetornarCepNormalizado() {
		// Arrange
		String cep = "'CEP: 01001-000";
		String logradouro = "Praça da Sé";
		String complemento = "lado ímpar";
		String unidade = "Unidade Centro";
		String bairro = "Sé";
		String localidade = "São Paulo";
		String uf = "SP";
		String estado = "São Paulo";
		String regiao = "Sudeste";
		String ibge = "3550308";
		String gia = "1004";
		String ddd = "11";
		String siafi = "7107";
		ViaCepResponseDto cepResponseDto = new ViaCepResponseDto(cep, logradouro, complemento, unidade, bairro, localidade, uf, estado, regiao, ibge, gia, ddd, siafi);
		
		// Act
		enderecoEntity = new EnderecoEntity(cepResponseDto);
		
	    // Assert
	    assertEquals("01001000", enderecoEntity.getCep());
	}
	
	@Test
	@DisplayName("1.3 - Deve manter CEP nulo quando informado null")
	public void deveManterCepNuloQuandoInformadoNull() {
		// Arrange
		String cep = null;
		String logradouro = "Praça da Sé";
		String complemento = "lado ímpar";
		String unidade = "Unidade Centro";
		String bairro = "Sé";
		String localidade = "São Paulo";
		String uf = "SP";
		String estado = "São Paulo";
		String regiao = "Sudeste";
		String ibge = "3550308";
		String gia = "1004";
		String ddd = "11";
		String siafi = "7107";
		ViaCepResponseDto cepResponseDto = new ViaCepResponseDto(cep, logradouro, complemento, unidade, bairro, localidade, uf, estado, regiao, ibge, gia, ddd, siafi);
		
		// Act
		enderecoEntity = new EnderecoEntity(cepResponseDto);

	    // Assert
	    assertNull(enderecoEntity.getCep());
	}
	
	@Test
	@DisplayName("1.4 - Deve testar as ramificações do Equals")
	public void deveTestarRamificacoesEquals() {
	    // Arrange
	    UUID id = UUID.randomUUID();
	    String cep = "01001000";

	    EnderecoEntity enderecoValido = new EnderecoEntity();
	    enderecoValido.setId(id);
	    enderecoValido.setCep(cep);

	    EnderecoEntity outroEnderecoIgual = new EnderecoEntity();
	    outroEnderecoIgual.setId(id);
	    outroEnderecoIgual.setCep(cep);

	    EnderecoEntity outroEnderecoDiferente = new EnderecoEntity();
	    outroEnderecoDiferente.setId(UUID.randomUUID());
	    outroEnderecoDiferente.setCep("02002000");

	    EnderecoEntity enderecoNulo = null;
	    Object objetoDeOutraClasse = new Object();
		
	    // Act
	    boolean cenarioMesmoObjeto = enderecoValido.equals(enderecoValido);
	    boolean cenarioNulo = enderecoValido.equals(enderecoNulo);
	    boolean cenarioClasseDiferente = enderecoValido.equals(objetoDeOutraClasse);
	    boolean cenarioMesmoIdECep = enderecoValido.equals(outroEnderecoIgual);
	    boolean cenarioIdECepDiferentes = enderecoValido.equals(outroEnderecoDiferente);
	    
	    // Assert
	    assertTrue(cenarioMesmoObjeto);
	    assertFalse(cenarioNulo);
	    assertFalse(cenarioClasseDiferente);
	    assertTrue(cenarioMesmoIdECep);
	    assertFalse(cenarioIdECepDiferentes);
	}
}
