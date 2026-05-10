package br.com.sp.nava.cep.search.api.web.advice;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class TratadorGlobalDeExcecoes {

	private static final URI TIPO_ERRO_VALIDACAO = URI.create("urn:cep-search-api:erro-validacao");

	private static final URI TIPO_RECURSO_NAO_ENCONTRADO = URI.create("urn:cep-search-api:recurso-nao-encontrado");

	private static final URI TIPO_REQUISICAO_MAL_FORMATADA = URI.create("urn:cep-search-api:requisicao-mal-formatada");

	private static final URI TIPO_ERRO_SERVICO_EXTERNO = URI.create("urn:cep-search-api:erro-servico-externo");

	private static final URI TIPO_SERVICO_EXTERNO_INDISPONIVEL = URI
			.create("urn:cep-search-api:servico-externo-indisponivel");

	private static final URI TIPO_REQUISICAO_INVALIDA = URI.create("urn:cep-search-api:requisicao-invalida");

	private static final URI TIPO_ERRO_INTERNO = URI.create("urn:cep-search-api:erro-interno");

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ProblemDetail> tratarEntidadeNaoEncontrada(EntityNotFoundException excecao,
			HttpServletRequest requisicao) {
		ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, excecao.getMessage());

		problema.setTitle("Recurso não encontrado");
		problema.setType(TIPO_RECURSO_NAO_ENCONTRADO);
		problema.setInstance(URI.create(requisicao.getRequestURI()));
		problema.setProperty("timestamp", LocalDateTime.now());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problema);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ProblemDetail> tratarErroDeValidacao(MethodArgumentNotValidException excecao,
			HttpServletRequest requisicao) {
		List<String> erros = excecao.getBindingResult().getFieldErrors().stream().map(this::formatarErroDeCampo)
				.toList();

		ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
				"A requisição possui campos inválidos.");

		problema.setTitle("Erro de validação");
		problema.setType(TIPO_ERRO_VALIDACAO);
		problema.setInstance(URI.create(requisicao.getRequestURI()));
		problema.setProperty("timestamp", LocalDateTime.now());
		problema.setProperty("erros", erros);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problema);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ProblemDetail> tratarMensagemHttpNaoLegivel(HttpMessageNotReadableException excecao,
			HttpServletRequest requisicao) {
		ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
				"Corpo da requisição inválido ou mal formatado.");

		problema.setTitle("Mensagem HTTP inválida");
		problema.setType(TIPO_REQUISICAO_MAL_FORMATADA);
		problema.setInstance(URI.create(requisicao.getRequestURI()));
		problema.setProperty("timestamp", LocalDateTime.now());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problema);
	}

	@ExceptionHandler(RestClientResponseException.class)
	public ResponseEntity<ProblemDetail> tratarErroDeRespostaDoServicoExterno(RestClientResponseException excecao,
			HttpServletRequest requisicao) {
		ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY,
				"Erro ao consultar serviço externo de CEP.");

		problema.setTitle("Erro de integração externa");
		problema.setType(TIPO_ERRO_SERVICO_EXTERNO);
		problema.setInstance(URI.create(requisicao.getRequestURI()));
		problema.setProperty("timestamp", LocalDateTime.now());
		problema.setProperty("statusExterno", excecao.getStatusCode().value());

		return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(problema);
	}

	@ExceptionHandler(ResourceAccessException.class)
	public ResponseEntity<ProblemDetail> tratarServicoExternoIndisponivel(ResourceAccessException excecao,
			HttpServletRequest requisicao) {
		ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
				"Serviço externo de CEP indisponível no momento.");

		problema.setTitle("Serviço externo indisponível");
		problema.setType(TIPO_SERVICO_EXTERNO_INDISPONIVEL);
		problema.setInstance(URI.create(requisicao.getRequestURI()));
		problema.setProperty("timestamp", LocalDateTime.now());

		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(problema);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ProblemDetail> tratarArgumentoInvalido(IllegalArgumentException excecao,
			HttpServletRequest requisicao) {
		ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, excecao.getMessage());

		problema.setTitle("Requisição inválida");
		problema.setType(TIPO_REQUISICAO_INVALIDA);
		problema.setInstance(URI.create(requisicao.getRequestURI()));
		problema.setProperty("timestamp", LocalDateTime.now());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problema);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ProblemDetail> tratarErroGenerico(Exception excecao, HttpServletRequest requisicao) {
		ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
				"Erro interno inesperado.");

		problema.setTitle("Erro interno");
		problema.setType(TIPO_ERRO_INTERNO);
		problema.setInstance(URI.create(requisicao.getRequestURI()));
		problema.setProperty("timestamp", LocalDateTime.now());

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problema);
	}

	private String formatarErroDeCampo(FieldError erroDeCampo) {
		return erroDeCampo.getField() + ": " + erroDeCampo.getDefaultMessage();
	}
}