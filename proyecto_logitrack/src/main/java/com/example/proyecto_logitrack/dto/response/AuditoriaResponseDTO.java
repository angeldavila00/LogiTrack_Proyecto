package com.example.proyecto_logitrack.dto.response;

import com.example.proyecto_logitrack.modelo.Operacion;

import java.util.Date;

public record AuditoriaResponseDTO(
        Long id, String entidad, Operacion operacion,
        Date fecha,String valorAnterior,
        String valorNuevo, UsuarioResponseDTO usuario
) {
}
