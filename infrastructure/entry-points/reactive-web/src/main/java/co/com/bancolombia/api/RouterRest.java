package co.com.bancolombia.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(GET("/api/v1/users"), handler::getAllUsers)

                .andRoute(POST("/api/v1/users/search"), handler::searchUsers)
                .andRoute(POST("/api/v1/users"), handler::createUser)

                .andRoute(GET("/api/v1/users/{id}"), handler::getUserById)
                .andRoute(PUT("/api/v1/users/{id}"), handler::updateUser)
                .andRoute(DELETE("/api/v1/users/{id}"), handler::deleteUser);
    }
}
