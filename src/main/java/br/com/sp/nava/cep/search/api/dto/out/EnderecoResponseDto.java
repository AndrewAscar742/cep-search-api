package br.com.sp.nava.cep.search.api.dto.out;

import java.time.LocalDateTime;

public record EnderecoResponseDto(
		String id,
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        String localidade,
        String uf,
        String ibge,
        String gia,
        String ddd,
        String siafi,
        LocalDateTime dataRegistro,
        LocalDateTime ultimaAtualizacao
) {
	
	
}
