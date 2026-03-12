package com.example.proyecto_logitrack.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record BodegaRequestDTO (
        @Schema(description = "Se ingresa el nombre de la bodega",
                example = "INVENTARIOS_NISSAN")
        @NotBlank(message = "El nombre no puede estar vacio.")
        String nombre,
        @NotBlank(message = "La ubicacion no puede estar vacia.")
        String ubicacion,
        @Positive(message = "Error, la capacidad debe ser positiva")
        Integer capacidad,
        Long usuarioId
        ){

}
