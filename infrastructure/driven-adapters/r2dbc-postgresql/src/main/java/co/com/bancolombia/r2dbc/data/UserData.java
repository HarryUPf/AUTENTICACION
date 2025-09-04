package co.com.bancolombia.r2dbc.data;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Table("users")
public class UserData {

    @Id
    private Long id;
    @Column("nombres")
    private String nombres;
    @Column("apellidos")
    private String apellidos;
    @Column("fecha_nacimiento")
    private LocalDate fechaNacimiento;
    @Column("direccion")
    private String direccion;
    @Column("telefono")
    private String telefono;
    @Column("email")
    private String email;
    @Column("salario_base")
    private BigDecimal salarioBase;
    @Column("password")
    private String password;
    @Column("role")
    private String role;
}