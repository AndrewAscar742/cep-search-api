package br.com.sp.nava.cep.search.api.service;

import br.com.sp.nava.cep.search.api.dto.out.EnderecoResponseDto;

public interface EnderecoService {
	EnderecoResponseDto buscarPorCep(String cep);
}
