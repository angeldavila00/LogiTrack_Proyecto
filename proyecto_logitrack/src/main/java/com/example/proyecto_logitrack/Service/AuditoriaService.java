package com.example.proyecto_logitrack.Service;

import com.example.proyecto_logitrack.dto.request.AuditoriaRequestDTO;
import com.example.proyecto_logitrack.dto.response.AuditoriaResponseDTO;
import com.example.proyecto_logitrack.modelo.Operacion;

import java.util.List;

public interface AuditoriaService {
    List<AuditoriaResponseDTO> listarAuditorias();
    AuditoriaResponseDTO buscarPorId(Long id);
 void registrar(String entidad, Operacion operacion, String valorAnterior, String valorNuevo, Long usuarioId);
}
