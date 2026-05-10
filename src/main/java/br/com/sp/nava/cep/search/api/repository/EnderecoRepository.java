package br.com.sp.nava.cep.search.api.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.sp.nava.cep.search.api.domain.EnderecoEntity;

public interface EnderecoRepository extends JpaRepository<EnderecoEntity, UUID>{

	Optional<EnderecoEntity> findByCep(String cep);

}
