package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.AuthRequestDTO;
import co.com.bancolombia.api.dto.UserDTO;
import co.com.bancolombia.api.dto.EmailRequestDTO;
import co.com.bancolombia.api.dto.IdRequestDTO;
import co.com.bancolombia.api.dto.AuthResponseDTO;
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
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;

@Configuration
public class RouterRest {
    @Bean
    @RouterOperations({
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
            ),
            @RouterOperation(
                    path = "/api/v1/login",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "login",
                    operation = @Operation(
                            operationId = "login",
                            summary = "Iniciar sesión de usuario",
                            description = "Autentica un usuario con email y password y devuelve un token JWT si las credenciales son correctas.",
                            tags = {"Autenticación"},
                            requestBody = @RequestBody(
                                    description = "Credenciales de inicio de sesión",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = AuthRequestDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Login exitoso", content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
                                    @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content(schema = @Schema(implementation = java.util.Map.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/usuarios/buscar-por-id",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "getUserById",
                    operation = @Operation(
                            operationId = "getUserById",
                            summary = "Buscar usuario por ID",
                            description = "Busca y devuelve los datos de un usuario a partir de su ID, proporcionado en el cuerpo de la solicitud.",
                            tags = {"Usuarios"},
                            requestBody = @RequestBody(required = true, description = "Objeto con el ID del usuario a buscar", content = @Content(schema = @Schema(implementation = IdRequestDTO.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content(schema = @Schema(implementation = UserDTO.class))),
                                    @ApiResponse(responseCode = "400", description = "Cuerpo de la solicitud inválido o ID faltante", content = @Content(schema = @Schema(implementation = java.util.Map.class))),
                                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content(schema = @Schema(implementation = java.util.Map.class)))
                            }
                    )
//            @RouterOperation(
//                    path = "/api/v1/usuarios/buscar",
//                    produces = {MediaType.APPLICATION_JSON_VALUE},
//                    method = RequestMethod.POST,
//                    beanClass = Handler.class,
//                    beanMethod = "findUserByEmail",
//                    operation = @Operation(
//                            operationId = "findUserByEmail",
//                            summary = "Buscar usuario por email",
//                            description = "Busca y devuelve los datos de un usuario a partir de su dirección de correo electrónico, proporcionada en el cuerpo de la solicitud.",
//                            tags = {"Usuarios"},
//                            requestBody = @RequestBody(required = true, description = "Objeto con el email del usuario a buscar", content = @Content(schema = @Schema(implementation = EmailRequestDTO.class))),
//                            responses = {
//                                    @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content(schema = @Schema(implementation = UserDTO.class))),
//                                    @ApiResponse(responseCode = "400", description = "Cuerpo de la solicitud inválido o email faltante", content = @Content(schema = @Schema(implementation = java.util.Map.class))),
//                                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content(schema = @Schema(implementation = java.util.Map.class)))
//                            }
//                    )
//            )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        final var apiV1 = path("/api/v1");
        return route(POST("/api/v1/login"), handler::login)
                // In RouterRest.java, inside your routerFunction...
                .andRoute(POST("/api/v1/usuarios/buscar-por-id"), handler::getUserById)
//                .andRoute(POST("/api/v1/usuarios/buscar"), handler::findUserByEmail)
                .and(nest(apiV1, route(POST("/usuarios"), handler::createUser)));
    }
}
