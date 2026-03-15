package com.example.proyecto_logitrack.dto.response;

import com.example.proyecto_logitrack.modelo.TipoMovimiento;

import java.time.LocalDateTime;
import java.util.Date;

public record MovimientoResponseDTO(
        Long id, LocalDateTime fecha, TipoMovimiento tipoMovimiento,
        UsuarioResponseDTO usuario, BodegaResponseDTO bodegaOrigen, BodegaResponseDTO bodegaDestino
) {
}
