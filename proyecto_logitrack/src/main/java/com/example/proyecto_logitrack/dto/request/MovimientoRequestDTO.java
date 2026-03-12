package com.example.proyecto_logitrack.dto.request;

import com.example.proyecto_logitrack.modelo.TipoMovimiento;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

public record MovimientoRequestDTO(
        @Schema(description = "Fecha del movimiento", example = "2026-03-10T10:30:00")
        Date fecha,

        @Schema(description = "Tipo de movimiento", example = "ENTRADA")
        TipoMovimiento tipoMovimiento,

        @Schema(description = "ID del usuario que realiza el movimiento", example = "1")
        Long usuarioId,

        @Schema(description = "ID de la bodega origen", example = "1")
        Long bodegaOrigenId,

        @Schema(description = "ID de la bodega destino", example = "2")
        Long bodegaDestinoId

) {
}
