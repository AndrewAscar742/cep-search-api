package br.com.sp.nava.cep.search.api.dto.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record InputEnderecoDto(
        @NotBlank(message = "O CEP é obrigatório.")
        @Size(min = 8, max = 8, message = "O CEP deve conter exatamente 8 caracteres.")
        @Pattern(regexp = "\\d{8}", message = "O CEP deve conter apenas números.")
		String cep) {

}
