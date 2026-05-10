package br.com.sp.nava.cep.search.api.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.sp.nava.cep.search.api.dto.out.EnderecoResponseDto;
import br.com.sp.nava.cep.search.api.service.EnderecoService;

@RestController
@RequestMapping("/v1/enderecos")
public class EnderecoController {
	
    private final EnderecoService enderecoService;

    public EnderecoController(EnderecoService enderecoService) {
        this.enderecoService = enderecoService;
    }

    @GetMapping("/{cep}")
    public ResponseEntity<EnderecoResponseDto> buscarPorCep(@PathVariable String cep) {
        return ResponseEntity.ok(enderecoService.buscarPorCep(cep));
    }
}
