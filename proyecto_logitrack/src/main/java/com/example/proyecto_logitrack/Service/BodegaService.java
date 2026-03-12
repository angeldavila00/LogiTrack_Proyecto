package com.example.proyecto_logitrack.Service;

import com.example.proyecto_logitrack.dto.request.BodegaRequestDTO;
import com.example.proyecto_logitrack.dto.response.BodegaResponseDTO;

import java.util.List;

public interface BodegaService {

    BodegaResponseDTO crearBodega(BodegaRequestDTO dto);
    BodegaResponseDTO actualizarBodega(BodegaRequestDTO dto, Long id);
    List<BodegaResponseDTO> listarBodegas();
    BodegaResponseDTO buscarPorId(Long id);
    void eliminarBodega(Long id);
}
