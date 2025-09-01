package co.com.bancolombia.model.user;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class UserValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"
    );

    private UserValidator() {
        // Private constructor to prevent instantiation
    }

    public static List<String> validate(User user) {
        List<String> errors = new ArrayList<>();

        if (user.getNombres() == null || user.getNombres().isBlank()) {
            errors.add("El campo 'Nombres' no puede ser nulo o vacío");
        }
        if (user.getApellidos() == null || user.getApellidos().isBlank()) {
            errors.add("El campo 'Apellidos' no puede ser nulo o vacío");
        }

        // Email validation
        if (user.getCorreoElectronico() == null || user.getCorreoElectronico().isBlank()) {
            errors.add("El campo 'Correo Electronico' no puede ser nulo o vacío");
        } else if (!EMAIL_PATTERN.matcher(user.getCorreoElectronico()).matches()) {
            errors.add("El campo 'Correo Electronico' tiene un formato de email inválido");
        }

        // Salary validation
        if (user.getSalarioBase() == null) {
            errors.add("El campo 'Salario Base' no puede ser nulo");
        } else if (user.getSalarioBase().compareTo(BigDecimal.ZERO) < 0 ||
                user.getSalarioBase().compareTo(new BigDecimal("15000000")) > 0) {
            errors.add("El campo 'Salario Base' debe ser un valor numérico entre 0 y 15,000,000");
        }

        return errors;
    }
}