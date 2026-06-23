package br.com.sp.nava.cep.search.api.unit.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import br.com.sp.nava.cep.search.api.config.RestClientConfig;

@ExtendWith(MockitoExtension.class)
public class RestClientConfigTest {
	
	private RestClientConfig clientConfig;
	
	@BeforeEach
	public void setUp() {
		//Arrange
		clientConfig = new RestClientConfig();
		
		ReflectionTestUtils.setField(clientConfig, "baseUrl", "http://mock-via-cep.com.br");
		ReflectionTestUtils.setField(clientConfig, "connectTimeoutMs", 1000);
		ReflectionTestUtils.setField(clientConfig, "readTimeoutMs", 4000);
	}
	
	@Test
	@DisplayName("1.0 - Deve Retornar Rest Client Configurado")
	public void deveRetornarRestClientConfigurado() {
		// Act
		RestClient restClient = clientConfig.gatewayRestClient();

		// Assert
		assertNotNull(restClient);
		assertInstanceOf(RestClient.class, restClient);
	}
	
	@Test
	@DisplayName("1.1 - Deve manter propriedades configuradas")
	void deveManterPropriedadesConfiguradas() {
	    // Assert
		assertEquals("http://mock-via-cep.com.br", ReflectionTestUtils.getField(clientConfig, "baseUrl"));
		assertEquals(1000, ReflectionTestUtils.getField(clientConfig, "connectTimeoutMs"));
		assertEquals(4000, ReflectionTestUtils.getField(clientConfig, "readTimeoutMs"));
	}
}
