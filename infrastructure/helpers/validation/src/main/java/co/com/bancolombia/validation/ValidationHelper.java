package co.com.bancolombia.validation;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.regex.Pattern;


@Component
@RequiredArgsConstructor
public class ValidationHelper {

    private final UserUseCase userUseCase;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"
    );

    public Mono<User> validateUser(User user) {
        Map<String, String> validationResults = new LinkedHashMap<>();
        validationResults.put("'Nombres'", (user.getNombres() == null || user.getNombres().isBlank()) ? "nulo o vacío" : null);
        validationResults.put("'Apellidos'", (user.getApellidos() == null || user.getApellidos().isBlank()) ? "nulo o vacío" : null);

        if (user.getCorreoElectronico() == null || user.getCorreoElectronico().isBlank()) {
            validationResults.put("'Correo Electronico'", "nulo o vacío");
        } else if (!EMAIL_PATTERN.matcher(user.getCorreoElectronico()).matches()) {
            validationResults.put("'Correo Electronico'", "tiene un formato de email inválido");
        }

        if (user.getSalarioBase() == null) {
            validationResults.put("'Salario Base'", "nulo");
        } else if (user.getSalarioBase() < 0 || user.getSalarioBase() > 15000000) {
            validationResults.put("'Salario Base'", "debe ser un valor numérico entre 0 y 15,000,000");
        }

        Map<String, List<String>> errorsByType = validationResults.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.groupingBy(
                        Map.Entry::getValue,
                        Collectors.mapping(Map.Entry::getKey, Collectors.toList())));

        if (errorsByType.isEmpty()) {
            return Mono.just(user);
        }

        String errorMessage = errorsByType.entrySet().stream()
                .map(entry -> "El campo " + String.join(", ", entry.getValue()) + " no puede ser " + entry.getKey())
                .collect(Collectors.joining(". "));

        return Mono.error(new IllegalArgumentException(errorMessage));
    }

    public Mono<User> validateEmailUniqueness(User user) {
        User searchCriteria = new User();
        searchCriteria.setCorreoElectronico(user.getCorreoElectronico());

        return userUseCase.searchUsers(searchCriteria)
                .hasElements()
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? Mono.error(new IllegalArgumentException("El correo electrónico '" + user.getCorreoElectronico() + "' ya está en uso."))
                        : Mono.just(user));
    }
}