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
//        return ServerResponse.ok()
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(userUseCase.getAllUsers(), User.class);
//    }
//
//    public Mono<ServerResponse> getUserById(ServerRequest serverRequest) {
//        Long id = Long.valueOf(serverRequest.pathVariable("id"));
//        return userUseCase.getUserById(id)
//                .flatMap(user -> ServerResponse.ok()
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .bodyValue(user))
//                .switchIfEmpty(ServerResponse.notFound().build());
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
                        .bodyValue(Map.of("message","Usuario creado con correo electrónico: "+user.getCorreoElectronico())))

                .doOnError(IllegalArgumentException.class, e -> log.warn("!!! Validation error in createUser: {}", e.getMessage()))
                .doOnError(e -> !(e instanceof IllegalArgumentException), e -> log.error("!!! Internal error in createUser", e))

                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage())))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", e.getMessage())))
        ;
    }

//    public Mono<ServerResponse> updateUser(ServerRequest serverRequest) {
//        Long id = Long.valueOf(serverRequest.pathVariable("id"));
//        return serverRequest.bodyToMono(User.class)
//                .flatMap(validationHelper::validateUser)
//                .flatMap(user -> userUseCase.updateUser(id, user))
//                .flatMap(user -> ServerResponse.ok()
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .bodyValue(user))
//                .switchIfEmpty(ServerResponse.notFound().build())
//                .onErrorResume(IllegalArgumentException.class, e ->
//                        ServerResponse.badRequest()
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .bodyValue(Map.of("error", e.getMessage())));
//    }
//
//    public Mono<ServerResponse> deleteUser(ServerRequest serverRequest) {
//        Long id = Long.valueOf(serverRequest.pathVariable("id"));
//        return userUseCase.deleteUser(id)
//                .then(ServerResponse.noContent().build());
//    }
//
//    public Mono<ServerResponse> searchUsers(ServerRequest serverRequest) {
//        Mono<User> userExample = serverRequest.bodyToMono(User.class);
//        Flux<User> foundUsers = userExample.flatMapMany(userUseCase::searchUsers);
//        return ServerResponse.ok()
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(foundUsers, User.class);
//    }
}
