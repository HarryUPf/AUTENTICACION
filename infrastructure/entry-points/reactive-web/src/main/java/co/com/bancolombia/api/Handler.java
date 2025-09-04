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
                .map(userMapper::toDomain) // La lógica de rol por defecto ahora está en el UseCase
                .flatMap(userUseCase::createUser)
                .flatMap(user -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("message", "Usuario creado con correo electrónico: " + user.getEmail())));
    }

    public Mono<ServerResponse> login(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(AuthRequestDTO.class)                
                .flatMap(dto -> {
                    Authentication authenticationToken = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
                    return this.authenticationManager.authenticate(authenticationToken)
                            .map(this.jwtProvider::generateToken);
                })
                .flatMap(jwt -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new AuthResponseDTO(jwt)));
    }
}
