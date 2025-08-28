package co.com.bancolombia.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import co.com.bancolombia.validation.ValidationHelper;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class Handler {

    private static final Logger log = LoggerFactory.getLogger(Handler.class);

    private final UserUseCase userUseCase;
    private final ValidationHelper validationHelper;

//    public Mono<ServerResponse> getAllUsers(ServerRequest serverRequest) {
//        Flux<User> usersFlux = userUseCase.getAllUsers()
//                .doOnSubscribe(subscription -> log.info(">>> Starting getAllUsers flow"))
//                .doOnComplete(() -> log.info("<<< getAllUsers flow completed successfully"));
//
//        return ServerResponse.ok()
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(usersFlux, User.class)
//                .doOnError(e -> log.error("!!! Error during getAllUsers flow", e))
//                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .bodyValue(Map.of("error", "Ocurrió un error interno inesperado.")));
//    }
//
//    public Mono<ServerResponse> getUserById(ServerRequest serverRequest) {
//        Long id = Long.valueOf(serverRequest.pathVariable("id"));
//        return userUseCase.getUserById(id)
//                .doOnSubscribe(subscription -> log.info(">>> Starting getUserById flow for id: {}", id))
//                .flatMap(user -> {
//                    log.info("<<< User found for id: {}", id);
//                    return ServerResponse.ok()
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .bodyValue(user);
//                })
//                .switchIfEmpty(Mono.defer(() -> {
//                    log.warn("!!! User not found for id: {}", id);
//                    return ServerResponse.status(HttpStatus.NOT_FOUND)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .bodyValue(Map.of("error", "Usuario con id " + id + " no encontrado."));
//                }))
//                .doOnError(e -> log.error("!!! Error during getUserById flow for id: {}", id, e))
//                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .bodyValue(Map.of("error", "Ocurrió un error interno inesperado.")));
//    }

    public Mono<ServerResponse> createUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(User.class)
                .doOnSubscribe(subscription -> log.info(">>> Starting createUser flow"))
                .flatMap(validationHelper::validateUser)
                .flatMap(validationHelper::validateEmailUniqueness)
                .flatMap(userUseCase::createUser)
                .doOnSuccess(user -> log.info("<<< User created successfully: {}", user.toString()))
                .flatMap(user -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("message", "Usuario creado con correo electrónico: " + user.getCorreoElectronico())))
                .doOnError(IllegalArgumentException.class, e -> log.warn("!!! Validation error in createUser: {}", e.getMessage()))
                .doOnError(e -> !(e instanceof IllegalArgumentException), e -> log.error("!!! Internal error in createUser", e))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage())))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", "Ocurrió un error interno inesperado.")));
    }

//    public Mono<ServerResponse> updateUser(ServerRequest serverRequest) {
//        Long id = Long.valueOf(serverRequest.pathVariable("id"));
//        return serverRequest.bodyToMono(User.class)
//                .doOnSubscribe(subscription -> log.info(">>> Starting updateUser flow for id: {}", id))
//                .flatMap(validationHelper::validateUser)
//                .flatMap(user -> userUseCase.updateUser(id, user))
//                .flatMap(updatedUser -> {
//                    log.info("<<< User with id: {} updated successfully", id);
//                    return ServerResponse.ok()
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .bodyValue(updatedUser);
//                })
//                .switchIfEmpty(Mono.defer(() -> {
//                    log.warn("!!! User not found for update with id: {}", id);
//                    return ServerResponse.status(HttpStatus.NOT_FOUND)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .bodyValue(Map.of("error", "Usuario con id " + id + " no encontrado para actualizar."));
//                }))
//                .doOnError(IllegalArgumentException.class, e -> log.warn("!!! Validation error in updateUser for id: {}: {}", id, e.getMessage()))
//                .doOnError(e -> !(e instanceof IllegalArgumentException), e -> log.error("!!! Internal error in updateUser for id: {}", id, e))
//                .onErrorResume(IllegalArgumentException.class, e ->
//                        ServerResponse.badRequest()
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .bodyValue(Map.of("error", e.getMessage())))
//                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .bodyValue(Map.of("error", "Ocurrió un error interno inesperado.")));
//    }
//
//    public Mono<ServerResponse> deleteUser(ServerRequest serverRequest) {
//        Long id = Long.valueOf(serverRequest.pathVariable("id"));
//        return userUseCase.deleteUser(id) // Asumo que el use-case retorna Mono<Void> y es vacío si no existe
//                .doOnSubscribe(subscription -> log.info(">>> Starting deleteUser flow for id: {}", id))
//                .then(Mono.defer(() -> {
//                    log.info("<<< User with id: {} deleted successfully", id);
//                    return ServerResponse.noContent().build();
//                }))
//                .switchIfEmpty(Mono.defer(() -> {
//                    log.warn("!!! User not found for deletion with id: {}", id);
//                    return ServerResponse.status(HttpStatus.NOT_FOUND)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .bodyValue(Map.of("error", "Usuario con id " + id + " no encontrado para eliminar."));
//                }))
//                .doOnError(e -> log.error("!!! Error during deleteUser flow for id: {}", id, e))
//                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .bodyValue(Map.of("error", "Ocurrió un error interno inesperado.")));
//    }
//
//    public Mono<ServerResponse> searchUsers(ServerRequest serverRequest) {
//        Flux<User> foundUsers = serverRequest.bodyToMono(User.class)
//                .doOnSubscribe(subscription -> log.info(">>> Starting searchUsers flow"))
//                .doOnNext(criteria -> log.info("--- Search criteria received: {}", criteria))
//                .flatMapMany(userUseCase::searchUsers)
//                .doOnComplete(() -> log.info("<<< searchUsers stream completed"));
//
//        return ServerResponse.ok()
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(foundUsers, User.class)
//                .doOnError(e -> log.error("!!! Error during searchUsers flow", e))
//                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .bodyValue(Map.of("error", "Ocurrió un error interno inesperado.")));
//    }
}
