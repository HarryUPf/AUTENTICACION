package co.com.bancolombia.model.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {

    private User.UserBuilder userBuilder;

    @BeforeEach
    void setUp() {
        userBuilder = User.builder()
                .nombres("Valid Name")
                .apellidos("Valid Lastname")
                .correoElectronico("valid.email@example.com")
                .salarioBase(new BigDecimal("1000000"));
    }

    @Test
    void validateWithValidUserShouldReturnNoErrors() {
        // Arrange
        User validUser = userBuilder.build();

        // Act
        List<String> errors = UserValidator.validate(validUser);

        // Assert
        assertTrue(errors.isEmpty());
    }

    @Test
    void validateWithNullNombresShouldReturnError() {
        // Arrange
        User user = userBuilder.nombres(null).build();

        // Act
        List<String> errors = UserValidator.validate(user);

        // Assert
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("'Nombres'"));
    }

    @Test
    void validateWithInvalidEmailShouldReturnError() {
        // Arrange
        User user = userBuilder.correoElectronico("invalid-email").build();

        // Act
        List<String> errors = UserValidator.validate(user);

        // Assert
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("formato de email inválido"));
    }

    @Test
    void validateWithSalaryOutOfRangeShouldReturnError() {
        // Arrange
        User user = userBuilder.salarioBase(new BigDecimal("20000000")).build();

        // Act
        List<String> errors = UserValidator.validate(user);

        // Assert
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("entre 0 y 15,000,000"));
    }

    @Test
    void validateWithMultipleErrorsShouldReturnAllErrors() {
        // Arrange
        User user = User.builder()
                .nombres("") // Invalid
                .apellidos("Valid")
                .correoElectronico("invalid-email") // Invalid
                .salarioBase(null) // Invalid
                .build();

        // Act
        List<String> errors = UserValidator.validate(user);

        // Assert
        assertEquals(3, errors.size());
    }
}