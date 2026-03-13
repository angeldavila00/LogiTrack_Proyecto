package com.example.proyecto_logitrack.dto.response;

public record MovimientoDetalleResponseDTO(
        Long id, Integer cantidad,
        MovimientoResponseDTO movimiento,ProductoResponseDTO producto
) {
}
