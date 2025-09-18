package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.*;
import co.com.bancolombia.api.dto.UserLoginDTO;
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
    private static final String ERROR_LABEL = "error";

    @PreAuthorize("hasRole('ADMIN') or hasRole('ASESOR')")
    public Mono<ServerResponse> createUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserDTO.class)
                .map(userMapper::toDomain) // La lógica de rol por defecto ahora está en el UseCase
                .flatMap(userUseCase::createUser)
                .flatMap(user -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("message", "Usuario creado con correo electrónico: " + user.getEmail())));
    }

    public Mono<ServerResponse> login(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(AuthRequestDTO.class)                
                .flatMap(authRequest -> {
                    Authentication authenticationToken = new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword());
                    Mono<String> tokenMono = this.authenticationManager.authenticate(authenticationToken)
                            .map(this.jwtProvider::generateToken)
                            .switchIfEmpty(Mono.error(new BadCredentialsException("Invalid credentials")));

                    Mono<UserLoginDTO> userLoginDTOMono = userUseCase.findByEmail(authRequest.getEmail())
                            .map(userMapper::toLoginDTO);
                    return Mono.zip(tokenMono, userLoginDTOMono, AuthResponseDTO::new);
                }).flatMap(authResponse -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(authResponse));
    }

    @PreAuthorize("hasRole('ASESOR') or hasRole('CLIENTE')")
    public Mono<ServerResponse> findUserByEmail(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(EmailRequestDTO.class)
                .flatMap(emailRequest -> {
                    if (emailRequest.getEmail() == null || emailRequest.getEmail().isBlank()) {
                        return ServerResponse.badRequest().bodyValue(Map.of(ERROR_LABEL, "El campo 'email' es requerido en el cuerpo de la solicitud"));
                    }
                    return userUseCase.findByEmail(emailRequest.getEmail())
                            .map(userMapper::toDTO)
                            .flatMap(userDTO -> ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(userDTO))
                            .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND)
                                    .bodyValue(Map.of(ERROR_LABEL, "Usuario no encontrado con el email: " + emailRequest.getEmail())));
                });
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('ASESOR')")
    public Mono<ServerResponse> getUserById(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(IdRequestDTO.class)
                .flatMap(idRequest -> {
                    if (idRequest.getId() == null) {
                        return ServerResponse.badRequest().bodyValue(Map.of(ERROR_LABEL, "El campo 'id' es requerido en el cuerpo de la solicitud"));
                    }
                    return userUseCase.getUserById(idRequest.getId())
                            .map(userMapper::toDTO)
                            .flatMap(userDTO -> ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(userDTO))
                            .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND)
                                    .bodyValue(Map.of(ERROR_LABEL, "Usuario no encontrado con el ID: " + idRequest.getId())));
                });
    }
}
