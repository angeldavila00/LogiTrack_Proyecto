package com.example.proyecto_logitrack.dto.request;

import com.example.proyecto_logitrack.modelo.Rol;

public record RegistroRequest(
        String nombre,
        String documento,
        String username,
        String password,
        Rol rol
) {
}
