package br.com.sp.nava.cep.search.api.service.impl;

import java.time.LocalDateTime;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.sp.nava.cep.search.api.domain.EnderecoEntity;
import br.com.sp.nava.cep.search.api.dto.out.EnderecoResponseDto;
import br.com.sp.nava.cep.search.api.dto.out.ViaCepResponseDto;
import br.com.sp.nava.cep.search.api.repository.EnderecoRepository;
import br.com.sp.nava.cep.search.api.service.EnderecoService;
import br.com.sp.nava.cep.search.api.service.exceptions.CepJaCadastradoException;
import br.com.sp.nava.cep.search.api.service.gateway.EnderecoViaCepGateway;
import jakarta.persistence.EntityNotFoundException;

@Service
@Profile("prd")
public class EnderecoViaCepServiceImpl implements EnderecoService {

	private final EnderecoRepository enderecoRepository;
	private final EnderecoViaCepGateway viaCepGateway;

	public EnderecoViaCepServiceImpl(EnderecoRepository enderecoRepository, EnderecoViaCepGateway viaCepGateway) {
		super();
		this.enderecoRepository = enderecoRepository;
		this.viaCepGateway = viaCepGateway;
	}

	@Override
	public EnderecoResponseDto buscarPorCep(String cep) {
		EnderecoEntity enderecoEntity = enderecoRepository.findByCep(cep)
				.orElseThrow(() -> new EntityNotFoundException("CEP inválido, tente novamente"));
		
		enderecoEntity.setUltimaConsulta(LocalDateTime.now());
		enderecoRepository.save(enderecoEntity);
		
		return convertToDto(enderecoEntity);
	}
	
	@Override
	public ViaCepResponseDto buscarPorCepGateway(String cep) {
		return viaCepGateway.buscarPorCep(cep);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	@Override
	public void validarCepNaBase(String cep) {
		enderecoRepository.findByCep(cep).ifPresent(endereco -> {
			throw new CepJaCadastradoException("O CEP informado já está cadastrado.");
		});
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public EnderecoResponseDto inserirCep(ViaCepResponseDto enderecoDto) {
		EnderecoEntity endereco = new EnderecoEntity(enderecoDto);

		enderecoRepository.save(endereco);
		
		return convertToDto(endereco);
	}

	private EnderecoResponseDto convertToDto(EnderecoEntity entity) {
		return new EnderecoResponseDto(entity.getId().toString(), entity.getCep(), entity.getLogradouro(),
				entity.getComplemento(), entity.getBairro(), entity.getLocalidade(), entity.getUf(), entity.getIbge(),
				entity.getGia(), entity.getDdd(), entity.getSiafi(), entity.getDataRegistro(), entity.getUltimaConsulta());
	}
}
