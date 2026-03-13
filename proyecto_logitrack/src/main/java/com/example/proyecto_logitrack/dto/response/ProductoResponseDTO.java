package com.example.proyecto_logitrack.dto.response;

public record ProductoResponseDTO(
        long id,
        String nombre,String categoria,
        double precio,Integer stock,
        BodegaResponseDTO bodega
) {
}
