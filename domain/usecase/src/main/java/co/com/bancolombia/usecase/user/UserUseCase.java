package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.UserValidator;
import co.com.bancolombia.model.user.gateways.PasswordEncryptor;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncryptor passwordEncryptor;
//    public Flux<User> getAllUsers() {
//        return userRepository.findAll();
//    }

//    public Mono<User> getUserById(Long id) {
//        return userRepository.findById(id);
//    }

//    public Mono<User> createUser(User user) {
//        return userRepository.save(user);
//    }

    public Mono<User> createUser(User user) {
        // Asignar rol por defecto si no se proporciona y normalizar a mayúsculas
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("CLIENTE");
        } else {
            user.setRole(user.getRole().toUpperCase());
        }

        // La validación de unicidad es una regla de negocio, por lo que pertenece aquí.
        // 1. Validación de invariantes del dominio
        List<String> validationErrors = UserValidator.validate(user);
        if (!validationErrors.isEmpty()) {
            return Mono.error(new IllegalArgumentException(String.join(". ", validationErrors)));
        }

        // Validar que el rol sea uno de los permitidos
        List<String> allowedRoles = Arrays.asList("ADMIN", "ASESOR", "CLIENTE");
        if (!allowedRoles.contains(user.getRole())) {
            return Mono.error(new IllegalArgumentException("El rol '" + user.getRole() + "' no es válido."));
        }

        // 2. Validación de proceso (regla de negocio que requiere IO)
        return userRepository.existsByEmail(user.getEmail())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new IllegalArgumentException("El correo electrónico '" + user.getEmail() + "' ya está en uso."));
                    }
//                    return userRepository.save(user);
                    return Mono.just(user)
                            .map(u -> {
                                u.setPassword(passwordEncryptor.encode(u.getPassword()));
                                return u;
                            })
                            .flatMap(userRepository::save);
                });
    }

//    public Mono<User> updateUser(Long id, User user) {
//        return userRepository.update(id, user);
//    }
//
//    public Mono<Void> deleteUser(Long id) {
//        return userRepository.deleteById(id);
//    }

    public Flux<User> searchUsers(User user) {
        return userRepository.findByExample(user);
    }

    public Mono<User> findByEmail(String email) {
        // Este método es crucial para que Spring Security (a través de UserDetailsServiceImpl)
        // pueda buscar al usuario durante el proceso de login.
        return userRepository.findByEmail(email);
    }
}
