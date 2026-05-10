package br.com.sp.nava.cep.search.api.service.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import br.com.sp.nava.cep.search.api.dto.out.EnderecoResponseDto;
import br.com.sp.nava.cep.search.api.repository.EnderecoRepository;
import br.com.sp.nava.cep.search.api.service.EnderecoService;
import br.com.sp.nava.cep.search.api.service.gateway.EnderecoViaCepGateway;

@Service
@Profile("prd")
public class EnderecoViaCepServiceImpl implements EnderecoService{
	
	private final EnderecoRepository enderecoRepository;
	private final EnderecoViaCepGateway viaCepGateway;

	public EnderecoViaCepServiceImpl(EnderecoRepository enderecoRepository, EnderecoViaCepGateway viaCepGateway) {
		super();
		this.enderecoRepository = enderecoRepository;
		this.viaCepGateway = viaCepGateway;
	}


	@Override
	public EnderecoResponseDto buscarPorCep(String cep) {
		return viaCepGateway.buscarPorCep(cep);
	}
	
	
	
}
