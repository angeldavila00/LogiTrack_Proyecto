package com.example.proyecto_logitrack.dto.request;

import com.example.proyecto_logitrack.modelo.Rol;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequestDTO(
        @Schema(description = "Se ingresa el nombre de la persona",
                example = "Angel")
        @NotBlank(message = "El nombre no puede estar vacio.")
        @Size(min = 2, max = 25, message = "Error, el nombre debe tener minimo 2 caracteres maximo 25")
        String nombre,

        @Schema(description = "Se ingresa el documento de la persona",
                example = "1007999211")
        String documento,

        @Schema(description = "Se ingresa el username",
                example = "angel_admin")
        @NotBlank(message = "El username no puede estar vacio.")
        String username,

        @Schema(description = "Se ingresa la contraseña",
        example = "221414")
        @NotBlank(message = "El password no puede estar vacio.")
        String password,

        @Schema(description = "Rol del usuario",
                example = "ADMIN")
        Rol rol

) {}
