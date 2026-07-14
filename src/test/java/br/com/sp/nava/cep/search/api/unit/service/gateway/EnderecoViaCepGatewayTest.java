package br.com.sp.nava.cep.search.api.unit.service.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import br.com.sp.nava.cep.search.api.dto.out.ViaCepResponseDto;
import br.com.sp.nava.cep.search.api.service.gateway.EnderecoViaCepGateway;

@ExtendWith(MockitoExtension.class)
public class EnderecoViaCepGatewayTest {

	@Mock
	private RestClient restClient;

	@Mock
	private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

	@Mock
	private RestClient.RequestHeadersSpec requestHeadersSpec;

	@Mock
	private RestClient.ResponseSpec responseSpec;

	@InjectMocks
	private EnderecoViaCepGateway enderecoViaCepGateway;

	@Test
	@DisplayName("1.1 - Deve buscar CEP no ViaCEP e retornar ViaCepResponseDto.")
	public void deveBuscarCep() {
		// Arrange
		String cep = "01001000";

		ViaCepResponseDto viaCepResponseDto = new ViaCepResponseDto("01001000", "Praça da Sé", "lado ímpar",
				"Unidade Centro", "Sé", "São Paulo", "SP", "São Paulo", "Sudeste", "3550308", "1004", "11", "7107");
		
		when(restClient.get()).thenReturn(requestHeadersUriSpec);

		when(requestHeadersUriSpec.uri("/ws/{cep}/json", cep)).thenReturn(requestHeadersSpec);

		when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

		when(responseSpec.body(ViaCepResponseDto.class)).thenReturn(viaCepResponseDto);

		// Act
		ViaCepResponseDto resultado = enderecoViaCepGateway.buscarPorCep(cep);

		// Assert
        assertNotNull(resultado);
        assertEquals(viaCepResponseDto, resultado);

        verify(restClient).get();
        verify(requestHeadersUriSpec).uri("/ws/{cep}/json", cep);
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).body(ViaCepResponseDto.class);
	}

	@Test
	@DisplayName("1.2 - Deve propagar exceção quando ocorrer erro na chamada do RestClient.")
	public void devePropagarExcecaoErroChamada() {
		// Arrange
		String cep = "01001000";
		RuntimeException exception = new RuntimeException("Erro ao consultar ViaCEP");

		when(restClient.get()).thenReturn(requestHeadersUriSpec);

		when(requestHeadersUriSpec.uri("/ws/{cep}/json", cep)).thenReturn(requestHeadersSpec);

		when(requestHeadersSpec.retrieve()).thenThrow(exception);

		// Act & Assert
		RuntimeException thrown = assertThrows(RuntimeException.class, () -> enderecoViaCepGateway.buscarPorCep(cep));

		assertSame(exception, thrown);

		verify(restClient).get();
		verify(requestHeadersUriSpec).uri("/ws/{cep}/json", cep);
		verify(requestHeadersSpec).retrieve();
		verify(responseSpec, never()).body(ViaCepResponseDto.class);

	}
}
