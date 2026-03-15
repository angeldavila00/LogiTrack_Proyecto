package com.example.proyecto_logitrack.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record MovimientoDetalleRequestDTO(
        @Schema(description = "Cantidad de productos en el movimiento", example = "5")
        @NotNull(message = "No puede estar vacia.")
        @PositiveOrZero(message = "La cantidad no puede ser negativo.")
        Integer cantidad,

        @Schema(description = "ID del movimiento al que pertenece", example = "1")
        @NotNull(message = "no puede estar vacio.")
        Long movimientoId,

        @Schema(description = "ID del producto que se mueve", example = "2")
        @NotNull(message = "no puede estar vacio.")
        Long productoId
) {
}
