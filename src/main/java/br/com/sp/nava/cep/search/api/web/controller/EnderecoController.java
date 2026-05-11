package br.com.sp.nava.cep.search.api.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.sp.nava.cep.search.api.dto.in.InputEnderecoDto;
import br.com.sp.nava.cep.search.api.dto.out.EnderecoResponseDto;
import br.com.sp.nava.cep.search.api.dto.out.ViaCepResponseDto;
import br.com.sp.nava.cep.search.api.service.EnderecoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/v1/enderecos")
@Tag(
        name = "Endereços",
        description = "Consulta e cadastro de endereços a partir de um CEP."
)
public class EnderecoController {

	private final EnderecoService enderecoService;

	public EnderecoController(EnderecoService enderecoService) {
		this.enderecoService = enderecoService;
	}
	
    @Operation(
            summary = "Buscar endereço por CEP",
            description = "Consulta um endereço já cadastrado na base de dados."
    )
	@GetMapping(path = "/{cep}")
	public ResponseEntity<EnderecoResponseDto> buscarPorCep(
			@Parameter(description = "CEP com 8 dígitos numéricos.", example = "01001000") @PathVariable String cep) {
		return ResponseEntity.ok(enderecoService.buscarPorCep(cep));
	}
    
    @Operation(
            summary = "Consultar e cadastrar endereço",
            description = """
                    Consulta o CEP em um serviço externo e cadastra o endereço retornado.
                    No profile DSV, a consulta é feita via WireMock.
                    No profile PRD, a consulta é feita via ViaCEP.
                    """
    )
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> buscarPorCep(@RequestBody @Valid InputEnderecoDto enderecoDto) {
		// valida se o cep já existe no banco
		enderecoService.validarCepNaBase(enderecoDto.cep());
		
		// busca o cep na api via cep
		ViaCepResponseDto enderecoViaCepResponse = enderecoService.buscarPorCepGateway(enderecoDto.cep());
		
		//inserção no banco
		EnderecoResponseDto enderecoResponseDto = enderecoService.inserirCep(enderecoViaCepResponse);
		
		//retorno
		return ResponseEntity.status(HttpStatus.CREATED).body(enderecoResponseDto);
	}
}
