package com.example.proyecto_logitrack.dto.request;

import com.example.proyecto_logitrack.modelo.Rol;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
        @Schema(description = "Nombre de usuario del empleado", example = "angel")
        @NotBlank(message = "El nombre no puede estar vacío.")
        @Size(min = 2, max = 50, message = "El nombre debe tener mínimo 2 y máximo 50 caracteres.")
        String nombre,

        @Schema(description = "Documento de usuario del empleado", example = "241452135")
        @NotBlank(message = "El documento no puede estar vacío.")
        @Size(min = 8, max = 15, message = "El documento debe tener mínimo 8 y máximo 15 caracteres.")
        String documento,

        @Schema(description = "Nombre de usuario del empleado", example = "angel_admin")
        @NotBlank(message = "El username no puede estar vacío.")
        @Size(min = 3, max = 20, message = "El username debe tener mínimo 3 y máximo 20 caracteres.")
        String username,

        @NotBlank(message = "Esta campo no puede estar vacio o solo con espacios")
        @Schema(description = "Contraseña del usuario con al menos una mayúscula, una minúscula y un número", example = "Admin1234")
        String password,

        @NotNull(message = "El rol es obligatorio.")
        Rol rol
) {
}
