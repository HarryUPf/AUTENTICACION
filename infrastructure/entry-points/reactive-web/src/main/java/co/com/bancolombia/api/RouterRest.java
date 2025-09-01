package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    @RouterOperations(
            @RouterOperation(
                    path = "/api/v1/usuarios",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "createUser",
                    operation = @Operation(
                            operationId = "createUser",
                            summary = "Crear un nuevo usuario",
                            description = "Crea un nuevo usuario en el sistema. Valida que los campos no sean nulos/vacíos, que el formato del email sea correcto, que el salario esté en el rango permitido y que el email no exista previamente.",
                            tags = {"Usuarios"},
                            requestBody = @RequestBody(
                                    description = "Datos del usuario a crear",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = UserDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente", content = @Content(schema = @Schema(implementation = java.util.Map.class))),
                                    @ApiResponse(responseCode = "400", description = "Error de validación en los datos de entrada", content = @Content(schema = @Schema(implementation = java.util.Map.class))),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(schema = @Schema(implementation = java.util.Map.class)))
                            }
                    )
            )
    )
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/usuarios"), handler::createUser)
        ;
    }
}
