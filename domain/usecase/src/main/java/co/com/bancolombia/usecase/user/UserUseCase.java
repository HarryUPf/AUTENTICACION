package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.UserValidator;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

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
        // La validación de unicidad es una regla de negocio, por lo que pertenece aquí.
        // 1. Validación de invariantes del dominio
        List<String> validationErrors = UserValidator.validate(user);
        if (!validationErrors.isEmpty()) {
            return Mono.error(new IllegalArgumentException(String.join(". ", validationErrors)));
        }

        // 2. Validación de proceso (regla de negocio que requiere IO)
        return userRepository.existsByEmail(user.getCorreoElectronico())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new IllegalArgumentException("El correo electrónico '" + user.getCorreoElectronico() + "' ya está en uso."));
                    }
                    return userRepository.save(user);
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
}
