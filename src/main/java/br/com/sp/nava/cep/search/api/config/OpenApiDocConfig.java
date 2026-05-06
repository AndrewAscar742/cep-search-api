package br.com.sp.nava.cep.search.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiDocConfig {
	
	@Bean
	public OpenAPI customConfiguration() {
		return new OpenAPI().components(new Components())
				.info(new Info().title("CEP Search API").description("API REST para consulta de CEP com integração externa e persistência de logs de auditoria.")
						.version("0.0.1-SNAPSHOT")
						.license(new License().name("Uso exclusivo para avaliação técnica")));
	}
}
