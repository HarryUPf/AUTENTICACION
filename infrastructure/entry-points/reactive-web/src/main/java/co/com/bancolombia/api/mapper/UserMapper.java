package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.UserDTO;
import co.com.bancolombia.api.dto.UserLoginDTO;
import co.com.bancolombia.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toDomain(UserDTO dto);

    /**
     * Maps a User domain entity to a UserDTO for general purposes (e.g., admin views).
     * The password field is explicitly ignored to prevent it from being exposed.
     */
    @Mapping(target = "password", ignore = true)
    UserDTO toDTO(User user);

    /**
     * Maps a User domain entity to a UserLoginDTO, containing only the data
     * needed for a post-login response.
     */
    UserLoginDTO toLoginDTO(User user);
}