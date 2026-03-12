package com.example.proyecto_logitrack.Service;

import com.example.proyecto_logitrack.dto.request.AuditoriaRequestDTO;
import com.example.proyecto_logitrack.dto.response.AuditoriaResponseDTO;

import java.util.List;

public interface AuditoriaService {
    AuditoriaResponseDTO crearAuditoria(AuditoriaRequestDTO dto);
    AuditoriaResponseDTO actualizarAuditoria(AuditoriaRequestDTO dto, Long id);
    List<AuditoriaResponseDTO> listarAuditorias();
    AuditoriaResponseDTO buscarPorId(Long id);
    void eliminarAuditoria(Long id);
}
