package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.AuthRequestDTO;
import co.com.bancolombia.api.dto.AuthResponseDTO;
import co.com.bancolombia.api.dto.UserDTO;
import co.com.bancolombia.api.mapper.UserMapper;
import co.com.bancolombia.api.security.JwtProvider;
import co.com.bancolombia.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    private final ReactiveAuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @PreAuthorize("hasRole('ADMIN') or hasRole('ASESOR')")
    public Mono<ServerResponse> createUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserDTO.class)
                .doOnSubscribe(subscription -> log.info(">>> Starting createUser flow"))
                .map(userDTO -> {
                    // Si el rol no viene en el DTO, se asigna 'CLIENTE' por defecto.
                    // Esto evita que el campo llegue nulo al mapper y la DB use su default.
                    if (userDTO.getRole() == null || userDTO.getRole().isEmpty()) {
                        log.warn("Role not provided in request, defaulting to 'CLIENTE'");
                        userDTO.setRole("CLIENTE");
                    }
                    // Ahora, el mapper sí recibirá el rol para convertirlo al objeto de dominio.
                    return userMapper.toDomain(userDTO);
                })
                .flatMap(userUseCase::createUser)
                .doOnSuccess(user -> log.info("<<< User created successfully: {}", user.toString()))
                .flatMap(user -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("message", "Usuario creado con correo electrónico: " + user.getEmail())))
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

    public Mono<ServerResponse> login(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(AuthRequestDTO.class)
                .doOnSubscribe(subscription -> log.info(">>> Starting login flow"))
                .flatMap(dto -> {
                    Authentication authenticationToken = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
                    return this.authenticationManager.authenticate(authenticationToken)
                            .map(this.jwtProvider::generateToken);
                })
                .doOnSuccess(token -> log.info("<<< Login successful, token generated"))
                .flatMap(jwt -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new AuthResponseDTO(jwt)))
                .doOnError(BadCredentialsException.class, e -> log.warn("!!! Invalid credentials attempt"))
                .doOnError(e -> !(e instanceof BadCredentialsException), e -> log.error("!!! Internal error in login", e))
                .onErrorResume(BadCredentialsException.class, e ->
                        ServerResponse.status(HttpStatus.UNAUTHORIZED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", "Credenciales inválidas")))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", "Error interno en el servidor.")));
    }
}
