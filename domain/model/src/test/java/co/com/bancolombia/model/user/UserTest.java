package co.com.bancolombia.model.user;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void userBuilderShouldCreateCorrectObject() {
        // Arrange
        Long id = 1L;
        String nombres = "John";
        String apellidos = "Doe";
        String correo = "john.doe@example.com";
        BigDecimal salario = new BigDecimal("50000.00");

        // Act
        User user = User.builder()
                .id(id)
                .nombres(nombres)
                .apellidos(apellidos)
                .correoElectronico(correo)
                .salarioBase(salario)
                .build();

        // Assert
        assertNotNull(user);
        assertEquals(id, user.getId());
        assertEquals(nombres, user.getNombres());
        assertEquals(apellidos, user.getApellidos());
        assertEquals(correo, user.getCorreoElectronico());
        assertEquals(0, salario.compareTo(user.getSalarioBase())); // Use compareTo for BigDecimal
    }

    @Test
    void userSettersAndGettersShouldWork() {
        // Arrange
        User user = new User();
        Long id = 2L;
        String nombres = "Jane";
        String apellidos = "Doe";
        String correo = "jane.doe@example.com";
        BigDecimal salario = new BigDecimal("60000.50");

        // Act
        user.setId(id);
        user.setNombres(nombres);
        user.setApellidos(apellidos);
        user.setCorreoElectronico(correo);
        user.setSalarioBase(salario);

        // Assert
        assertEquals(id, user.getId());
        assertEquals(nombres, user.getNombres());
        assertEquals(apellidos, user.getApellidos());
        assertEquals(correo, user.getCorreoElectronico());
        assertEquals(0, salario.compareTo(user.getSalarioBase()));
    }

    @Test
    void equalsAndHashCodeShouldBeConsistent() {
        // Arrange
        BigDecimal salario = new BigDecimal("75000.00");
        User user1 = User.builder().id(3L).nombres("Peter").apellidos("Jones").correoElectronico("peter.jones@example.com").salarioBase(salario).build();
        User user2 = User.builder().id(3L).nombres("Peter").apellidos("Jones").correoElectronico("peter.jones@example.com").salarioBase(salario).build();
        User user3 = User.builder().id(4L).nombres("Mary").apellidos("Jane").correoElectronico("mary.jane@example.com").salarioBase(new BigDecimal("80000.00")).build();

        // Assert
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1, user3);
        assertNotEquals(user1.hashCode(), user3.hashCode());
        assertNotEquals(user1, null);
        assertNotEquals(user1, new Object());
    }

    @Test
    void toStringShouldNotBeNull() {
        // Arrange
        User user = User.builder().nombres("Test").build();

        // Act & Assert
        assertNotNull(user.toString());
        assertTrue(user.toString().contains("Test"));
    }
}