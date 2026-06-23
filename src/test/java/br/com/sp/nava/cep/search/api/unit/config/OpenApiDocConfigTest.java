package br.com.sp.nava.cep.search.api.unit.config;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.sp.nava.cep.search.api.config.OpenApiDocConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

public class OpenApiDocConfigTest {
	private OpenApiDocConfig docConfig;
	
	@BeforeEach
	private void initialize() {
		docConfig = new OpenApiDocConfig();
	}
	
	@Test
	@DisplayName("Deve configurar e retornar o objeto OpenAPI através do método customConfig")
	public void deveRetornarOpenApiDocConfigConfigurado() {
		//Arrange
		OpenAPI customConfiguration = docConfig.customConfiguration();
		
		//Act & Assert
		assertNotNull(customConfiguration);
		assertNotNull(customConfiguration.getInfo());
		assertNotNull(customConfiguration.getInfo().getTitle());
		assertNotNull(customConfiguration.getInfo().getDescription());
		assertNotNull(customConfiguration.getInfo().getVersion());
		
		Info info = customConfiguration.getInfo();
		assertAll(
					() -> assertFalse(info.getTitle().isBlank()),
					() -> assertFalse(info.getDescription().isBlank()),
					() -> assertFalse(info.getVersion().isBlank())
				);
	}
}
