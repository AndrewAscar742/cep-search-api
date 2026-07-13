package br.com.sp.nava.cep.search.api.unit.dto.in;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.sp.nava.cep.search.api.dto.in.InputEnderecoDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class InputEnderecoDtoTest {
	
	private static Validator validator;
	private InputEnderecoDto enderecoDto;
	
    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
	
	@Test
	@DisplayName("1.0 - Deve aceitar CEP válido com exatamente 8 números")
	public void deveAceitarCepValido() {
		//Arrange
		this.enderecoDto = new InputEnderecoDto("01001000");
		
		//Act
        Set<ConstraintViolation<InputEnderecoDto>> violations = validator.validate(enderecoDto);
		
		//Assert
		assertTrue(violations.isEmpty());
	}
	
	@Test
	@DisplayName("1.1 - Deve rejeitar CEP nulo")
	public void deveRejeitarCepNulo() {
		//Arrange
		this.enderecoDto = new InputEnderecoDto(null);
		
		//Act
        Set<ConstraintViolation<InputEnderecoDto>> violations = validator.validate(enderecoDto);
		
		//Assert
		assertFalse(violations.isEmpty());
	}
	
	@Test
	@DisplayName("1.2 - Deve Rejeitar CEP Vazio")
	public void deveRejeitarCepVazio() {
		//Arrange
		this.enderecoDto = new InputEnderecoDto("");
		
		//Act
        Set<ConstraintViolation<InputEnderecoDto>> violations = validator.validate(enderecoDto);
		
		//Assert
		assertFalse(violations.isEmpty());
	}
	
	@Test
	@DisplayName("1.3 - Deve testar Cep em Branco")
	public void deveRejeitarCepBranco(){
		//Arrange
		this.enderecoDto = new InputEnderecoDto(" ");
		
		//Act
        Set<ConstraintViolation<InputEnderecoDto>> violations = validator.validate(enderecoDto);
		
		//Assert
		assertFalse(violations.isEmpty());
	}
	
	@Test
	@DisplayName("1.4 - Deve testar Cep com tamanho menor que 8 caracteres")
	public void deveRejeitarCepComMenosDeOitoCaracteres(){
		//Arrange
		this.enderecoDto = new InputEnderecoDto("0100100");
		
		//Act
        Set<ConstraintViolation<InputEnderecoDto>> violations = validator.validate(enderecoDto);
		
		//Assert
		assertFalse(violations.isEmpty());
	}
	
	@Test
	@DisplayName("1.5 - Deve testar Cep com tamanho maior que 8 caracteres")
	public void deveRejeitarCepComMaisDeOitoCaracteres(){
		//Arrange
		this.enderecoDto = new InputEnderecoDto("010010000");
		
		//Act
        Set<ConstraintViolation<InputEnderecoDto>> violations = validator.validate(enderecoDto);
		
		//Assert
		assertFalse(violations.isEmpty());
	}
	
	@Test
	@DisplayName("1.6 - Deve rejeitar CEP com Letras")
	public void deveRejeitarCepComLetras() {
		//Arrange
		this.enderecoDto = new InputEnderecoDto("01001ABC");
		
		//Act
        Set<ConstraintViolation<InputEnderecoDto>> violations = validator.validate(enderecoDto);
		
		//Assert
		assertFalse(violations.isEmpty());
	}
	
	@Test
	@DisplayName("1.7 - Deve rejeitar CEP com Hifen")
	public void deveRejeitarCepComHifen() {
		//Arrange
		this.enderecoDto = new InputEnderecoDto("01001-000");
		
		//Act
        Set<ConstraintViolation<InputEnderecoDto>> violations = validator.validate(enderecoDto);
		
		//Assert
		assertFalse(violations.isEmpty());
	}
	
	@Test
	@DisplayName("1.8 - Deve rejeitar CEP com espaço entre os números")
	public void deveRejeitarCepComEspaco() {
		//Arrange
		this.enderecoDto = new InputEnderecoDto("0100 100");
		
		//Act
        Set<ConstraintViolation<InputEnderecoDto>> violations = validator.validate(enderecoDto);
		
		//Assert
		assertFalse(violations.isEmpty());
	}
	
	@Test
	@DisplayName("1.9 - Deve rejeitar CEP com caractere especial")
	public void deveRejeitarCepComCaractereEspecial() {
		//Arrange
		this.enderecoDto = new InputEnderecoDto("01001@00");
		
		//Act
        Set<ConstraintViolation<InputEnderecoDto>> violations = validator.validate(enderecoDto);
		
		//Assert
		assertFalse(violations.isEmpty());
	}
}
