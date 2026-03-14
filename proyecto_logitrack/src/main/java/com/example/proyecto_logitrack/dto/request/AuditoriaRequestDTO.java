package com.example.proyecto_logitrack.dto.request;

import com.example.proyecto_logitrack.modelo.Operacion;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

public record AuditoriaRequestDTO(
        @Schema(description = "Entidad afectada", example = "producto")
        String entidad,

        @Schema(description = "Tipo de operación realizada", example = "UPDATE")
        Operacion operacion,

        @Schema(description = "Fecha de la operación", example = "2026-03-10T10:30:00")
        Date fecha,

        @Schema(description = "Valor antes del cambio", example = "stock=10")
        String valorAnterior,

        @Schema(description = "Valor después del cambio", example = "stock=8")
        String valorNuevo,

        @Schema(description = "ID del usuario que realizó la acción", example = "1")
        Long usuarioId
) {
}
