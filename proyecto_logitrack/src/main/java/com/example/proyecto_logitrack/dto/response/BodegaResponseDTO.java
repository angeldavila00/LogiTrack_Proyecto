package com.example.proyecto_logitrack.dto.response;

public record BodegaResponseDTO(
        Long id, String nombre, String ubicacion,Integer capacidad, UsuarioResponseDTO usuario
) {
}
