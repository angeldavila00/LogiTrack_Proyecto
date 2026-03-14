package com.example.proyecto_logitrack.dto.response;

import com.example.proyecto_logitrack.modelo.Rol;

public record UsuarioResponseDTO(
        long id,
        String nombre,
        String documento,
        String username,
        Rol rol
) {
}
