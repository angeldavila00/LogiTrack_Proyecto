package com.example.proyecto_logitrack.dto.response;

public record ReporteResponseDTO(
        long total,
        long Entradas,
        long Salidas,
        long Transferencias
) {
}
