package co.com.bancolombia.model.user.gateways;

import co.com.bancolombia.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> save(User user);
    Flux<User> findByExample(User user);

    Mono<Boolean> existsByEmail(String email);

    Mono<User> findByEmail(String email);

    Mono<User> findById(Long id);

}