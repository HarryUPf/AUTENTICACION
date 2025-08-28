package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

//    public Flux<User> getAllUsers() {
//        return userRepository.findAll();
//    }

//    public Mono<User> getUserById(Long id) {
//        return userRepository.findById(id);
//    }

    public Mono<User> createUser(User user) {
        return userRepository.save(user);
    }
//
//    public Mono<User> updateUser(Long id, User user) {
//        return userRepository.update(id, user);
//    }
//
//    public Mono<Void> deleteUser(Long id) {
//        return userRepository.deleteById(id);
//    }

    public Flux<User> searchUsers(User user) {
        return userRepository.findByExample(user);
    }
}
