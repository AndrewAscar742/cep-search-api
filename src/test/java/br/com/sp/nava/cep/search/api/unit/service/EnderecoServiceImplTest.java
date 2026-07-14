package br.com.sp.nava.cep.search.api.unit.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.sp.nava.cep.search.api.domain.EnderecoEntity;
import br.com.sp.nava.cep.search.api.dto.out.EnderecoResponseDto;
import br.com.sp.nava.cep.search.api.dto.out.ViaCepResponseDto;
import br.com.sp.nava.cep.search.api.repository.EnderecoRepository;
import br.com.sp.nava.cep.search.api.service.exceptions.CepJaCadastradoException;
import br.com.sp.nava.cep.search.api.service.gateway.EnderecoViaCepGateway;
import br.com.sp.nava.cep.search.api.service.impl.EnderecoServiceImpl;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class EnderecoServiceImplTest {
	
	@Mock
	private EnderecoRepository enderecoRepository;
	
	@Mock
	private EnderecoViaCepGateway enderecoViaCepGateway;
	
	@InjectMocks
	private EnderecoServiceImpl enderecoServiceImpl;
	
	private EnderecoEntity enderecoEntity;
	
	@BeforeEach
	private void setUp() {
		enderecoEntity = new EnderecoEntity();
		
		UUID enderecoId = UUID.fromString("5f2bd087-f06a-40b0-ac5c-198da13375d4");
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
		
		enderecoEntity.setId(enderecoId);
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
		
	}
	
	@Test
	@DisplayName("1.0 - Deve buscar endereco na base por CEP valido")
	public void deveBuscarEnderecoPorCepValido() {
	    // Arrange
	    String cep = enderecoEntity.getCep();

	    when(enderecoRepository.findByCep(cep))
	        .thenReturn(Optional.of(enderecoEntity));
		
		//Act
		EnderecoResponseDto enderecoResponseDto = enderecoServiceImpl.buscarPorCep(enderecoEntity.getCep());
		
		//Assert
	    assertNotNull(enderecoResponseDto);
	    assertEquals(enderecoEntity.getId().toString(), enderecoResponseDto.id());
	    assertEquals(enderecoEntity.getCep(), enderecoResponseDto.cep());
	    assertEquals(enderecoEntity.getLogradouro(), enderecoResponseDto.logradouro());
	    assertEquals(enderecoEntity.getComplemento(), enderecoResponseDto.complemento());
	    assertEquals(enderecoEntity.getBairro(), enderecoResponseDto.bairro());
	    assertEquals(enderecoEntity.getLocalidade(), enderecoResponseDto.localidade());
	    assertEquals(enderecoEntity.getUf(), enderecoResponseDto.uf());
	    assertEquals(enderecoEntity.getIbge(), enderecoResponseDto.ibge());
	    assertEquals(enderecoEntity.getGia(), enderecoResponseDto.gia());
	    assertEquals(enderecoEntity.getDdd(), enderecoResponseDto.ddd());
	    assertEquals(enderecoEntity.getSiafi(), enderecoResponseDto.siafi());
	    assertEquals(enderecoEntity.getDataRegistro(), enderecoResponseDto.dataRegistro());

	    assertNotNull(enderecoResponseDto.ultimaAtualizacao());

	    verify(enderecoRepository).findByCep(cep);
	    verify(enderecoRepository).save(enderecoEntity);
	    verifyNoInteractions(enderecoViaCepGateway);
		
	}
	
	@Test
	@DisplayName("1.1 - Deve lançar EntityNotFoundException quando CEP não existir na base")
	public void deveLancarEntityNotFoundExceptionQuandoCepNaoExistirNaBase() {    
	    // Arrange
	    String cep = "99999999";

	    when(enderecoRepository.findByCep(cep))
	        .thenReturn(Optional.empty());

	    // Act & Assert
	    assertThrows(EntityNotFoundException.class, () -> enderecoServiceImpl.buscarPorCep(cep));

	    verify(enderecoRepository).findByCep(cep);
	    verify(enderecoRepository, never()).save(any(EnderecoEntity.class));
	    verifyNoInteractions(enderecoViaCepGateway);
	}
	
	@Test
	@DisplayName("1.2 - Deve atualizar a data de última consulta ao buscar endereço existente")
	public void deveAtualizarUltimaConsultaBuscandoEndereco() {
	    // Arrange
	    String cep = enderecoEntity.getCep();
	    LocalDateTime ultimaConsultaAnterior = enderecoEntity.getUltimaConsulta();

	    when(enderecoRepository.findByCep(cep))
	        .thenReturn(Optional.of(enderecoEntity));
		
		//Act
		EnderecoResponseDto enderecoResponseDto = enderecoServiceImpl.buscarPorCep(enderecoEntity.getCep());
		
		//Assert
	    assertNotNull(enderecoResponseDto.ultimaAtualizacao());
	    assertNotEquals(ultimaConsultaAnterior, enderecoResponseDto.ultimaAtualizacao());
	    assertTrue(enderecoResponseDto.ultimaAtualizacao().isAfter(ultimaConsultaAnterior));
		
	    verify(enderecoRepository).findByCep(cep);
	    verify(enderecoRepository).save(enderecoEntity);
	    verifyNoInteractions(enderecoViaCepGateway);
	}
	
	@Test
	@DisplayName("1.3 - Deve buscar CEP no gateway ViaCEP")
	public void deveBuscarCepNoGatewayViaCep() {
		// Arrange
		String cep = "01001000";
		ViaCepResponseDto viaCepResponseDto = new ViaCepResponseDto(
			    "01001000",
			    "Praça da Sé",
			    "lado ímpar",
			    "Unidade Centro",
			    "Sé",
			    "São Paulo",
			    "SP",
			    "São Paulo",
			    "Sudeste",
			    "3550308",
			    "1004",
			    "11",
			    "7107"
			);
		
		when(enderecoViaCepGateway.buscarPorCep(cep)).thenReturn(viaCepResponseDto);
		
		//Act
		ViaCepResponseDto resultado = enderecoServiceImpl.buscarPorCepGateway(cep);
		
		//Assert
	    assertNotNull(resultado);
	    assertSame(viaCepResponseDto, resultado);

	    verify(enderecoViaCepGateway).buscarPorCep(cep);
	    verifyNoInteractions(enderecoRepository);
	}
	
	
	@Test
	@DisplayName("1.4 - Deve lançar CepJaCadastradoException quando CEP já existir")
	public void deveLancarCepJaCadastradoException() {
		// Arrange
		String cep = "01001000";
		when(enderecoRepository.findByCep(cep)).thenReturn(Optional.of(new EnderecoEntity()));
		
		//Act & Assert
		assertThrows(CepJaCadastradoException.class, () ->  enderecoServiceImpl.validarCepNaBase(cep));
		
		verify(enderecoRepository).findByCep(cep);
		verify(enderecoRepository, never()).save(any(EnderecoEntity.class));
		verifyNoInteractions(enderecoViaCepGateway);
		
		
	}
	
	@Test
	@DisplayName("1.5 - Deve concluir validação sem erro quando CEP não existir")
	public void deveConcluirValidacaoSemErroAoCepNaoExistir() {
		// Arrange
		String cep = "01001000";
		when(enderecoRepository.findByCep(cep)).thenReturn(Optional.empty());
		
		
		//Act & Assert
		assertDoesNotThrow(() -> enderecoServiceImpl.validarCepNaBase(cep));
		 
		verify(enderecoRepository).findByCep(cep);
		verify(enderecoRepository, never()).save(any(EnderecoEntity.class));
		verifyNoInteractions(enderecoViaCepGateway);
	}
	
	@Test
	@DisplayName("1.6 - Deve inserir endereço retornado pelo ViaCEP")
	public void deveInserirEndereco() {
		//Arrange
		ViaCepResponseDto viaCepResponseDto = new ViaCepResponseDto(
			    "01001000",
			    "Praça da Sé",
			    "lado ímpar",
			    "Unidade Centro",
			    "Sé",
			    "São Paulo",
			    "SP",
			    "São Paulo",
			    "Sudeste",
			    "3550308",
			    "1004",
			    "11",
			    "7107"
			);
		
		//Act
		EnderecoResponseDto enderecoResponseDto = enderecoServiceImpl.inserirCep(viaCepResponseDto);
		
		//Assert
		assertNotNull(enderecoResponseDto);
		
	    verify(enderecoRepository).save(any(EnderecoEntity.class));
	    verifyNoInteractions(enderecoViaCepGateway);
	}
	
	@Test
	@DisplayName("1.7 - Deve retornar DTO com dados da entidade salva")
	public void deveRetornarDtoComDadosJaSalvo() {
		//Arrange
		ViaCepResponseDto viaCepResponseDto = new ViaCepResponseDto(
			    "01001000",
			    "Praça da Sé",
			    "lado ímpar",
			    "Unidade Centro",
			    "Sé",
			    "São Paulo",
			    "SP",
			    "São Paulo",
			    "Sudeste",
			    "3550308",
			    "1004",
			    "11",
			    "7107"
			);
		
		//Act
		EnderecoResponseDto enderecoResponseDto = enderecoServiceImpl.inserirCep(viaCepResponseDto);
		
		//Assert
		assertNotNull(enderecoResponseDto);
		assertNotNull(enderecoResponseDto.id());
		assertEquals(viaCepResponseDto.cep(), enderecoResponseDto.cep());
		assertEquals(viaCepResponseDto.logradouro(), enderecoResponseDto.logradouro());
		assertEquals(viaCepResponseDto.localidade(), enderecoResponseDto.localidade());
		assertEquals(viaCepResponseDto.uf(), enderecoResponseDto.uf());
	}
}
