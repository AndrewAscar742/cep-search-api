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
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/v1/enderecos")
public class EnderecoController {
	
    private final EnderecoService enderecoService;

    public EnderecoController(EnderecoService enderecoService) {
        this.enderecoService = enderecoService;
    }

    @GetMapping(path = "/{cep}")
    public ResponseEntity<EnderecoResponseDto> buscarPorCep(@PathVariable String cep) {
        return ResponseEntity.ok(enderecoService.buscarPorCep(cep));
    }
    
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> buscarPorCep(@RequestBody @Valid InputEnderecoDto enderecoDto) {
       boolean isCepRegistered = enderecoService.validarCepNaBase(enderecoDto.cep());
       
       System.out.println(isCepRegistered);
       
       if (isCepRegistered) {
    	   return ResponseEntity.badRequest().body("O CEP passado já está cadastrado");
       }
       
       ViaCepResponseDto enderecoViaCepResponse = enderecoService.buscarPorCepGateway(enderecoDto.cep());
       
       EnderecoResponseDto enderecoResponseDto = enderecoService.inserirCep(enderecoViaCepResponse);
       
       return ResponseEntity.status(HttpStatus.CREATED).body(enderecoResponseDto);
    }
}
