package co.com.bancolombia.model.user.gateways;

import co.com.bancolombia.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {
//    Flux<User> findAll();
//    Mono<User> findById(Long id);
    Mono<User> save(User user);
//    Mono<User> update(Long id, User user);
//    Mono<Void> deleteById(Long id);
    Flux<User> findByExample(User user);
}