package com.example.proyecto_logitrack.dto.request;

import com.example.proyecto_logitrack.modelo.Rol;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record RegistroRequest(
        @Schema(description = "Nombre de usuario del empleado", example = "angel")
        String nombre,
        @Schema(description = "Documento de usuario del empleado", example = "241452135")
        String documento,
        @NotBlank(message = "Esta campo no puede estar vacio o solo con espacios")
        @Schema(description = "Nombre de usuario del empleado", example = "angel_admin")
        String username,
        @NotBlank(message = "Esta campo no puede estar vacio o solo con espacios")
        @Schema(description = "Contraseña del usuario con al menos una mayúscula, una minúscula y un número", example = "Admin1234")
        String password,
        Rol rol
) {
}
