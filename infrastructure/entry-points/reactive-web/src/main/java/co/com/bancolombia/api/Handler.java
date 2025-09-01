package co.com.bancolombia.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.api.dto.UserDTO;
import co.com.bancolombia.api.mapper.UserMapper;
import co.com.bancolombia.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class Handler {

    private static final Logger log = LoggerFactory.getLogger(Handler.class);

    private final UserUseCase userUseCase;
    private final UserMapper userMapper;

    public Mono<ServerResponse> createUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserDTO.class)
                .doOnSubscribe(subscription -> log.info(">>> Starting createUser flow"))
                .map(userMapper::toDomain)
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

}
