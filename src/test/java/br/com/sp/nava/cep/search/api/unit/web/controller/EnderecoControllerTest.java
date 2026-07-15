package br.com.sp.nava.cep.search.api.unit.web.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.com.sp.nava.cep.search.api.dto.in.InputEnderecoDto;
import br.com.sp.nava.cep.search.api.dto.out.EnderecoResponseDto;
import br.com.sp.nava.cep.search.api.dto.out.ViaCepResponseDto;
import br.com.sp.nava.cep.search.api.service.EnderecoService;
import br.com.sp.nava.cep.search.api.service.exceptions.CepJaCadastradoException;
import br.com.sp.nava.cep.search.api.web.controller.EnderecoController;

@ExtendWith(MockitoExtension.class)
public class EnderecoControllerTest {

    @Mock
    private EnderecoService enderecoService;

    @InjectMocks
    private EnderecoController enderecoController;
    
    private EnderecoResponseDto enderecoResponseDto;
    
	@BeforeEach
    private void setUp() {
		String enderecoId = "5f2bd087-f06a-40b0-ac5c-198da13375d4";
		String cep = "01001000";
		String logradouro = "Praça da Sé";
		String complemento = "lado ímpar";
		String bairro = "Sé";
		String localidade = "São Paulo";
		String uf = "SP";
		String ibge = "3550308";
		String gia = "1004";
		String ddd = "11";
		String siafi = "7107";
		LocalDateTime dataRegistro = LocalDateTime.of(2026, 6, 23, 10, 30);
		LocalDateTime ultimaConsulta = LocalDateTime.of(2026, 6, 23, 11, 45);
		
		this.enderecoResponseDto = new EnderecoResponseDto(enderecoId, cep, logradouro, complemento, bairro, localidade, uf, ibge, gia, ddd, siafi, dataRegistro, ultimaConsulta);
    }
    
    @Test
    @DisplayName("1.1 - Deve buscar endereço por CEP e retornar HTTP 200")
    public void deveBuscarEnderecoPorCepRetornarHTTP200() {
    	//Arrange
    	String cep = enderecoResponseDto.cep();
    	when(enderecoService.buscarPorCep(cep)).thenReturn(enderecoResponseDto);
    	
    	//Act
    	ResponseEntity<EnderecoResponseDto> response = enderecoController.buscarPorCep(cep);
    	
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(enderecoResponseDto, response.getBody());

        verify(enderecoService).buscarPorCep(cep);
        verifyNoMoreInteractions(enderecoService);
    }
    
    @Test
    @DisplayName("1.2 - Deve consultar, cadastrar endereço e retornar HTTP 201 Created")
    public void deveCadastrarEndereco() {
    	//Arrange
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
		
    	doNothing().when(enderecoService).validarCepNaBase(cep);

    	when(enderecoService.buscarPorCepGateway(cep))
    	    .thenReturn(viaCepResponseDto);

    	when(enderecoService.inserirCep(viaCepResponseDto))
    	    .thenReturn(enderecoResponseDto);
    	
    	//Act
    	ResponseEntity<Object> response = enderecoController.inserirEndereco(new InputEnderecoDto(cep));
    	
    	//Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(enderecoResponseDto, response.getBody());
        
        verify(enderecoService).validarCepNaBase(cep);
        verify(enderecoService).buscarPorCepGateway(cep);
        verify(enderecoService).inserirCep(viaCepResponseDto);
    }
    
    @Test
    @DisplayName("1.3 - Deve interromper fluxo quando CEP já estiver cadastrado")
    public void deveInterromperFluxoCadastroEndereco() {
        // Arrange
        String cep = "01001000";
        InputEnderecoDto inputEnderecoDto = new InputEnderecoDto(cep);

        doThrow(CepJaCadastradoException.class)
            .when(enderecoService).validarCepNaBase(cep);

        // Act & Assert
        assertThrows(
            CepJaCadastradoException.class,
            () -> enderecoController.inserirEndereco(inputEnderecoDto)
        );

        verify(enderecoService).validarCepNaBase(cep);
        verify(enderecoService, never()).buscarPorCepGateway(cep);
        verify(enderecoService, never()).inserirCep(any(ViaCepResponseDto.class));
        verifyNoMoreInteractions(enderecoService);
    }
}
