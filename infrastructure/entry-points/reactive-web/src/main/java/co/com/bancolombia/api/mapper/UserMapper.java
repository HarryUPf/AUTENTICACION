package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.UserDTO;
import co.com.bancolombia.model.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserDTO dto) {
        return User.builder()
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .salarioBase(dto.getSalarioBase())
                .build();
    }

    public UserDTO toDTO(User user) {
        return UserDTO.builder()
                .nombres(user.getNombres())
                .apellidos(user.getApellidos())
                .password(user.getPassword())
                .email(user.getEmail())
                .salarioBase(user.getSalarioBase())
                .build();
    }
}