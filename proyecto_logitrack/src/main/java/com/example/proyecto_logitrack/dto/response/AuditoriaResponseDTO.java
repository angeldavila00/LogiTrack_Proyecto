package com.example.proyecto_logitrack.dto.response;

import com.example.proyecto_logitrack.modelo.Operacion;

import java.time.LocalDateTime;
import java.util.Date;

public record AuditoriaResponseDTO(
        Long id, String entidad, Operacion operacion,
        LocalDateTime fecha, String valorAnterior,
        String valorNuevo, String usuarioNombre, UsuarioResponseDTO usuario
) {
}
