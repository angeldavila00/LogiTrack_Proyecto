package com.example.proyecto_logitrack.dto.request;

import com.example.proyecto_logitrack.modelo.TipoMovimiento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public record MovimientoRequestDTO(
        @Schema(description = "Fecha del movimiento", example = "2026-03-10T10:30:00")
        @NotNull(message = "no puede estar vacio.")
        LocalDateTime fecha,

        @Schema(description = "Tipo de movimiento", example = "ENTRADA")
        @NotNull(message = "no puede estar vacio.")
        TipoMovimiento tipoMovimiento,

        @Schema(description = "ID del usuario que realiza el movimiento", example = "1")
        @NotNull(message = "no puede estar vacio.")
        Long usuarioId,

        @Schema(description = "ID de la bodega origen", example = "1")
        @NotNull(message = "no puede estar vacio.")
        Long bodegaOrigenId,

        @Schema(description = "ID de la bodega destino", example = "2")
        @NotNull(message = "no puede estar vacio.")
        Long bodegaDestinoId,

        //lista movimiento
        @NotNull(message = "La lista de detalles no puede ser nula.")
        @Size(min = 1, message = "Debe agregar al menos un producto al movimiento.")
        List<MovimientoDetalleRequestDTO> detalles

) {
}
