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
                .email("valid.email@example.com")
                .salarioBase(new BigDecimal("1000000"));
    }

    @Test
    void validateWithValidUserShouldReturnNoErrors() {
        User validUser = userBuilder.build();
        List<String> errors = UserValidator.validate(validUser);
        assertTrue(errors.isEmpty());
    }

    @Test
    void validateWithNullNombresShouldReturnError() {
        User user = userBuilder.nombres(null).build();
        List<String> errors = UserValidator.validate(user);
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("'Nombres'"));
    }

    @Test
    void validateWithInvalidEmailShouldReturnError() {
        User user = userBuilder.email("invalid-email").build();
        List<String> errors = UserValidator.validate(user);
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("formato de email inválido"));
    }

    @Test
    void validateWithSalaryOutOfRangeShouldReturnError() {
        User user = userBuilder.salarioBase(new BigDecimal("20000000")).build();
        List<String> errors = UserValidator.validate(user);
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("entre 0 y 15,000,000"));
    }

    @Test
    void validateWithMultipleErrorsShouldReturnAllErrors() {
        User user = User.builder()
                .nombres("") // Invalid
                .apellidos("Valid")
                .email("invalid-email") // Invalid
                .salarioBase(null) // Invalid
                .build();
        List<String> errors = UserValidator.validate(user);
        assertEquals(3, errors.size());
    }
}