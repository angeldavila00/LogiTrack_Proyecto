package com.example.proyecto_logitrack.dto.response;

import com.example.proyecto_logitrack.modelo.TipoMovimiento;

import java.util.Date;

public record MovimientoResponseDTO(
        Long id, Date fecha, TipoMovimiento tipoMovimiento,
        UsuarioResponseDTO usuario, BodegaResponseDTO bodegaOrigen, BodegaResponseDTO bodegaDestino
) {
}
