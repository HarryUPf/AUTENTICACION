package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.r2dbc.data.UserData;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class MyReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        User, // Domain Model
        UserData, // Data Model
        Long,
        MyReactiveRepository
> implements UserRepository {

    public MyReactiveRepositoryAdapter(MyReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.mapBuilder(d, User.UserBuilder.class).build());
    }

    @Override
    @Transactional
    public Mono<User> save(User user) {
        // The ID is null for a new user, and the database will generate it.
        return super.save(user);
    }

    @Override
    @Transactional
    public Mono<User> update(Long id, User user) {
        return this.repository.findById(id)
                .flatMap(existing -> {
                    // Set the ID on the entity to ensure we perform an update
                    UserData userToUpdate = toData(user);
                    userToUpdate.setId(id);
                    return repository.save(userToUpdate);
                }).map(this::toEntity);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id);
    }

    @Override
    public Flux<User> findByExample(User user) {
        // Leverages the implementation from the base class
        return super.findByExample(user);
    }
}
