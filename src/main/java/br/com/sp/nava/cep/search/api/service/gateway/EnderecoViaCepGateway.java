package br.com.sp.nava.cep.search.api.service.gateway;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import br.com.sp.nava.cep.search.api.dto.out.EnderecoResponseDto;

@Service
@Profile("prd")
public class EnderecoViaCepGateway {
	
	private final RestClient restClient;

	public EnderecoViaCepGateway(RestClient restClient) {
		super();
		this.restClient = restClient;
	}
	
    public EnderecoResponseDto buscarPorCep(String cep) {
        return restClient.get()
                .uri("/ws/{cep}/json", cep)
                .retrieve()
                .body(EnderecoResponseDto.class);
    }
}
