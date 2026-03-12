package com.example.proyecto_logitrack.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Esta campo no puede estar vacio o solo con espacios")
        @Schema(description = "Nombre de usuario del empleado", example = "angel_admin")
        String username,
        @NotBlank(message = "Esta campo no puede estar vacio o solo con espacios")
        @Schema(description = "Contraseña del usuario con al menos una mayúscula, una minúscula y un número", example = "Admin1234")
        String contrasena
) {
}
