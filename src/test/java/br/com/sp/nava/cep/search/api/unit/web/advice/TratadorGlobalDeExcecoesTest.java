package br.com.sp.nava.cep.search.api.unit.web.advice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import br.com.sp.nava.cep.search.api.dto.in.InputEnderecoDto;
import br.com.sp.nava.cep.search.api.service.exceptions.CepJaCadastradoException;
import br.com.sp.nava.cep.search.api.web.advice.TratadorGlobalDeExcecoes;
import br.com.sp.nava.cep.search.api.web.controller.EnderecoController;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class TratadorGlobalDeExcecoesTest {
	
	private static final URI TIPO_ERRO_VALIDACAO = URI.create("urn:cep-search-api:erro-validacao");

	private static final URI TIPO_CEP_JA_CADASTRADO = URI.create("urn:cep-search-api:cep-ja-cadastrado");

	private static final URI TIPO_RECURSO_NAO_ENCONTRADO = URI.create("urn:cep-search-api:recurso-nao-encontrado");

	private static final URI TIPO_REQUISICAO_MAL_FORMATADA = URI.create("urn:cep-search-api:requisicao-mal-formatada");

	private static final URI TIPO_ERRO_SERVICO_EXTERNO = URI.create("urn:cep-search-api:erro-servico-externo");

	private static final URI TIPO_SERVICO_EXTERNO_INDISPONIVEL = URI
			.create("urn:cep-search-api:servico-externo-indisponivel");

	private static final URI TIPO_REQUISICAO_INVALIDA = URI.create("urn:cep-search-api:requisicao-invalida");

	private static final URI TIPO_ERRO_INTERNO = URI.create("urn:cep-search-api:erro-interno");
	
    private static final URI REQUEST_URI =
            URI.create("/v1/enderecos/01001000");
	
    @Mock
    private HttpServletRequest request;

    private TratadorGlobalDeExcecoes tratador;
    
    @BeforeEach
    public void setUp() {
        tratador = new TratadorGlobalDeExcecoes();

        when(request.getRequestURI())
            .thenReturn(REQUEST_URI.toString());
    }
    
    @Test
    @DisplayName("1.0 - Deve tratar EntityNotFoundException retornando HTTP 404")
    public void deveTratarEntityNotFoundException() {
        // Arrange
        EntityNotFoundException exception =
            new EntityNotFoundException("CEP inválido, tente novamente");

        // Act
        ResponseEntity<ProblemDetail> response =
            tratador.tratarEntidadeNaoEncontrada(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ProblemDetail problem = response.getBody();

        assertNotNull(problem);
        assertEquals("Recurso não encontrado", problem.getTitle());
        assertEquals("CEP inválido, tente novamente", problem.getDetail());
        assertEquals(TIPO_RECURSO_NAO_ENCONTRADO, problem.getType());
        assertEquals(URI.create("/v1/enderecos/01001000"), problem.getInstance());
        assertNotNull(problem.getProperties().get("timestamp"));

        verify(request).getRequestURI();
    }
    
    @Test
    @DisplayName("1.1 - Deve tratar CepJaCadastradoException retornando HTTP 409")
    public void deveTratarCepJaCadastroException() {
    	//Arrange
    	CepJaCadastradoException exception = new CepJaCadastradoException("O CEP informado já está cadastrado.");
    	
    	//Act
    	ResponseEntity<ProblemDetail> response = tratador.tratarCepJaCadastrado(exception, request);
    	
    	//Assert
    	assertNotNull(response);
    	assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    	
    	ProblemDetail problem = response.getBody();
    	
    	assertNotNull(problem);
        assertEquals("CEP já cadastrado", problem.getTitle());
        assertEquals("O CEP informado já está cadastrado.", problem.getDetail());
        assertEquals(TIPO_CEP_JA_CADASTRADO, problem.getType());
        assertEquals(URI.create("/v1/enderecos/01001000"), problem.getInstance());
        assertNotNull(problem.getProperties().get("timestamp"));
    }
    
    @Test
    @DisplayName("1.2 - Deve tratar MethodArgumentNotValidException retornando HTTP 400 com lista de erros")
    public void deveTratarMethodArgumentNotValidException() throws NoSuchMethodException {
    	//Arrange
    	String requestUri = "/v1/enderecos";

    	when(request.getRequestURI()).thenReturn(requestUri);

    	Method method = EnderecoController.class
    	    .getMethod("inserirEndereco", InputEnderecoDto.class);

    	MethodParameter methodParameter = new MethodParameter(method, 0);

    	BeanPropertyBindingResult bindingResult =
    	    new BeanPropertyBindingResult(new InputEnderecoDto(""), "inputEnderecoDto");

    	bindingResult.addError(new FieldError(
    	    "inputEnderecoDto",
    	    "cep",
    	    "O CEP é obrigatório."
    	));

    	MethodArgumentNotValidException exception =
    	    new MethodArgumentNotValidException(methodParameter, bindingResult);
    	
        // Act
        ResponseEntity<ProblemDetail> response =
            tratador.tratarErroDeValidacao(exception, request);
        
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ProblemDetail problem = response.getBody();

        assertNotNull(problem);
        assertEquals("Erro de validação", problem.getTitle());
        assertEquals("A requisição possui campos inválidos.", problem.getDetail());
        assertEquals(TIPO_ERRO_VALIDACAO, problem.getType());
        assertEquals(URI.create(requestUri), problem.getInstance());
        assertNotNull(problem.getProperties().get("timestamp"));
        assertNotNull(problem.getProperties().get("erros"));

        @SuppressWarnings("unchecked")
        List<String> erros = (List<String>) problem.getProperties().get("erros");

        assertEquals(1, erros.size());
        assertEquals("cep: O CEP é obrigatório.", erros.get(0));

        verify(request).getRequestURI();
    }
    
    @Test
    @DisplayName("1.3 - Deve tratar HttpMessageNotReadableException retornando HTTP 400")
    public void deveTratarHttpMessageNotReadableException() {
    	//Arrange
    	MockHttpInputMessage httpInputMessage = new MockHttpInputMessage(new byte[0]);

    	HttpMessageNotReadableException exception =
    	    new HttpMessageNotReadableException(
    	        "Corpo da requisição inválido ou mal formatado.",
    	        httpInputMessage
    	    );
    	
    	//Act
    	ResponseEntity<ProblemDetail> response = tratador.tratarMensagemHttpNaoLegivel(exception, request);
    	
    	//Assert
    	assertNotNull(response);
    	assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    	
    	ProblemDetail problem = response.getBody();
    	
    	assertNotNull(problem);
        assertEquals("Mensagem HTTP inválida", problem.getTitle());
        assertEquals("Corpo da requisição inválido ou mal formatado.", problem.getDetail());
        assertEquals(TIPO_REQUISICAO_MAL_FORMATADA, problem.getType());
        assertEquals(URI.create("/v1/enderecos/01001000"), problem.getInstance());
        assertNotNull(problem.getProperties().get("timestamp"));
    }
    
    @Test
    @DisplayName("1.4 - Deve tratar RestClientResponseException retornando HTTP 502")
    public void deveTratarRestClientResponseException() {
    	// Arrange
    	String requestUri = "/v1/enderecos";

    	when(request.getRequestURI()).thenReturn(requestUri);

    	RestClientResponseException exception = new RestClientResponseException(
    	    "Erro ao consultar ViaCEP",
    	    HttpStatus.NOT_FOUND,
    	    "Not Found",
    	    HttpHeaders.EMPTY,
    	    new byte[0],
    	    StandardCharsets.UTF_8
    	);
    	
    	//Act
    	ResponseEntity<ProblemDetail> response = tratador.tratarErroDeRespostaDoServicoExterno(exception, request);
    	
    	//Assert
    	assertNotNull(response);
    	assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
    	
    	ProblemDetail problem = response.getBody();
    	
    	assertNotNull(problem);
        assertEquals("Erro de integração externa", problem.getTitle());
        assertEquals("Erro ao consultar serviço externo de CEP.", problem.getDetail());
        assertEquals(TIPO_ERRO_SERVICO_EXTERNO, problem.getType());
        assertEquals(URI.create(requestUri), problem.getInstance());
        assertEquals(404, problem.getProperties().get("statusExterno"));
        assertNotNull(problem.getProperties().get("timestamp"));
        
        verify(request).getRequestURI();
    }
    
    @Test
    @DisplayName("1.5 - Deve tratar ResourceAccessException retornando HTTP 503")
    public void deveTratarResourceAccessException() {
        // Arrange
        ResourceAccessException exception =
                new ResourceAccessException("Serviço externo indisponível");

        // Act
        ResponseEntity<ProblemDetail> response =
                tratador.tratarServicoExternoIndisponivel(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());

        ProblemDetail problem = response.getBody();

        assertNotNull(problem);
        assertEquals("Serviço externo indisponível", problem.getTitle());
        assertEquals("Serviço externo de CEP indisponível no momento.", problem.getDetail());
        assertEquals(TIPO_SERVICO_EXTERNO_INDISPONIVEL, problem.getType());
        assertEquals(REQUEST_URI, problem.getInstance());
        assertNotNull(problem.getProperties().get("timestamp"));

        verify(request).getRequestURI();
    }

    @Test
    @DisplayName("1.6 - Deve tratar IllegalArgumentException retornando HTTP 400")
    public void deveTratarIllegalArgumentException() {
        // Arrange
        IllegalArgumentException exception =
                new IllegalArgumentException("CEP inválido.");

        // Act
        ResponseEntity<ProblemDetail> response =
                tratador.tratarArgumentoInvalido(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ProblemDetail problem = response.getBody();

        assertNotNull(problem);
        assertEquals("Requisição inválida", problem.getTitle());
        assertEquals("CEP inválido.", problem.getDetail());
        assertEquals(TIPO_REQUISICAO_INVALIDA, problem.getType());
        assertEquals(REQUEST_URI, problem.getInstance());
        assertNotNull(problem.getProperties().get("timestamp"));

        verify(request).getRequestURI();
    }

    @Test
    @DisplayName("1.7 - Deve tratar Exception genérica retornando HTTP 500")
    public void deveTratarExceptionGenerica() {
        // Arrange
        Exception exception = new Exception("Erro inesperado");

        // Act
        ResponseEntity<ProblemDetail> response =
                tratador.tratarErroGenerico(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        ProblemDetail problem = response.getBody();

        assertNotNull(problem);
        assertEquals("Erro interno", problem.getTitle());
        assertEquals("Erro interno inesperado.", problem.getDetail());
        assertEquals(TIPO_ERRO_INTERNO, problem.getType());
        assertEquals(REQUEST_URI, problem.getInstance());
        assertNotNull(problem.getProperties().get("timestamp"));

        verify(request).getRequestURI();
    }

}
