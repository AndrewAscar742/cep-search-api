package br.com.sp.nava.cep.search.api.service;

import br.com.sp.nava.cep.search.api.dto.out.EnderecoResponseDto;
import br.com.sp.nava.cep.search.api.dto.out.ViaCepResponseDto;

public interface EnderecoService {
	EnderecoResponseDto buscarPorCep(String cep);

	boolean validarCepNaBase(String cep);

	EnderecoResponseDto inserirCep(ViaCepResponseDto enderecoViaCepResponse);

	ViaCepResponseDto buscarPorCepGateway(String cep);
}
