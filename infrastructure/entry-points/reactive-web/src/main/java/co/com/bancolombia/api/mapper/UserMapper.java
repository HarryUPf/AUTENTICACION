package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.UserDTO;
import co.com.bancolombia.model.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserDTO dto) {
        return User.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(dto.getRole())
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .fechaNacimiento(dto.getFechaNacimiento())
                .direccion(dto.getDireccion())
                .telefono(dto.getTelefono())
                .salarioBase(dto.getSalarioBase())
                .build();
    }

    public UserDTO toDTO(User user) {
        return UserDTO.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .nombres(user.getNombres())
                .apellidos(user.getApellidos())
                .fechaNacimiento(user.getFechaNacimiento())
                .direccion(user.getDireccion())
                .telefono(user.getTelefono())
                .salarioBase(user.getSalarioBase())
                .build();
    }
}