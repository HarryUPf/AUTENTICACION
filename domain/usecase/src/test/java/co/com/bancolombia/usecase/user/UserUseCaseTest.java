package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserUseCase userUseCase;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .nombres("Test")
                .apellidos("User")
                .correoElectronico("test.user@example.com")
                .salarioBase(new BigDecimal("1000.00"))
                .build();
    }

    @Test
    void createUserWithInvalidDataShouldFail() {
        User invalidUser = user.toBuilder().nombres("").build();

        StepVerifier.create(userUseCase.createUser(invalidUser))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUserWithExistingEmailShouldFail() {
        when(userRepository.existsByEmail(user.getCorreoElectronico())).thenReturn(Mono.just(true));

        StepVerifier.create(userUseCase.createUser(user))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("ya está en uso"))
                .verify();

        verify(userRepository, never()).save(any());
    }

    @Test
    void createUserWithValidDataShouldSucceed() {
        when(userRepository.existsByEmail(user.getCorreoElectronico())).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));

        StepVerifier.create(userUseCase.createUser(user))
                .expectNext(user)
                .verifyComplete();

        verify(userRepository).existsByEmail(user.getCorreoElectronico());
        verify(userRepository).save(user);
    }

}