package com.example.proyecto_logitrack.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record BodegaRequestDTO (
        @Schema(description = "Se ingresa el nombre de la bodega",
                example = "INVENTARIOS_NISSAN")
        @NotBlank(message = "El nombre no puede estar vacio.")
        String nombre,
        @NotBlank(message = "La ubicacion no puede estar vacia.")
        String ubicacion,
        @Positive(message = "Error, la capacidad debe ser positiva")
        @NotNull(message = "La capacidad no puede ser nula.")
        @PositiveOrZero(message = "La capacidad no puede ser negativo.")
        Integer capacidad,
        @NotNull(message = "El Encargado es obligatorio. ")
        Long usuarioId
        ){

}
