package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserUseCase userUseCase;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .nombres("Test")
                .apellidos("User")
                .email("test.user@example.com")
                .salarioBase(new BigDecimal("5000000.00"))
                .role("CLIENTE") // Represents the state after being saved
                .build();
    }



        @Test
        @DisplayName("Should throw exception when user data is invalid")
        void shouldThrowExceptionWhenCreatingUserWithInvalidData() {
            // Arrange
            User invalidUser = sampleUser.toBuilder().nombres("").build();

            // Act & Assert
            StepVerifier.create(userUseCase.createUser(invalidUser))
                    .expectError(IllegalArgumentException.class)
                    .verify();

            // Verify
            verify(userRepository, never()).existsByEmail(any());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void shouldThrowExceptionWhenCreatingUserWithExistingEmail() {
            // Arrange
            when(userRepository.existsByEmail(sampleUser.getEmail())).thenReturn(Mono.just(true));

            // Act & Assert
            StepVerifier.create(userUseCase.createUser(sampleUser))
                    .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                            throwable.getMessage().contains("ya está en uso"))
                    .verify();

            // Verify
            verify(userRepository).existsByEmail(sampleUser.getEmail());
            verify(userRepository, never()).save(any());
        }
}