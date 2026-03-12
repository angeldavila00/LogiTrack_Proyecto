package com.example.proyecto_logitrack.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record MovimientoDetalleRequestDTO(
        @Schema(description = "Cantidad de productos en el movimiento", example = "5")
        Integer cantidad,

        @Schema(description = "ID del movimiento al que pertenece", example = "1")
        Long movimientoId,

        @Schema(description = "ID del producto que se mueve", example = "2")
        Long productoId
) {
}
