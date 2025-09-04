package co.com.bancolombia.r2dbc;

import co.com.bancolombia.r2dbc.data.UserData;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import reactor.core.publisher.Mono;

public interface MyReactiveRepository extends ReactiveCrudRepository<UserData, Long>, ReactiveQueryByExampleExecutor<UserData> {
    Mono<Boolean> existsByEmail(String email);
    Mono<UserData> findByEmail(String email);
}