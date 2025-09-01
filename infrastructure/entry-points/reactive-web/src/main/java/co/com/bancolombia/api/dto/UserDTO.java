package co.com.bancolombia.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDTO {
    private String nombres;
    private String apellidos;
    private String correoElectronico;
    private BigDecimal salarioBase;
}