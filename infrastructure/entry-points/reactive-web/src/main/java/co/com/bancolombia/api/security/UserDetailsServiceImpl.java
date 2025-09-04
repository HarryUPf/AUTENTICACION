package co.com.bancolombia.api.security;

import co.com.bancolombia.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final UserUseCase userUseCase;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        // Asumimos que UserUseCase tiene un método para buscar por email
        return userUseCase.findByEmail(username)
                .map(user -> new User(
                        user.getEmail(),
                        user.getPassword(),
                        // Usamos el rol del usuario, prefijado con "ROLE_" como es convención en Spring Security.
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
                ));
    }
}