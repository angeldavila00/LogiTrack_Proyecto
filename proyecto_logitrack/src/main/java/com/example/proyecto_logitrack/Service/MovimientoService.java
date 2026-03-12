package com.example.proyecto_logitrack.Service;

import com.example.proyecto_logitrack.dto.request.MovimientoDetalleRequestDTO;
import com.example.proyecto_logitrack.dto.request.MovimientoRequestDTO;
import com.example.proyecto_logitrack.dto.response.MovimientoResponseDTO;

import java.util.List;

public interface MovimientoService {

    MovimientoResponseDTO crearMovimiento(MovimientoRequestDTO dto);
    MovimientoResponseDTO actulizarMovimiento(MovimientoRequestDTO dto,Long id);
    List<MovimientoResponseDTO> listarMovimientos();
    MovimientoResponseDTO buscarPorId(Long id);
    void eliminarMovimiento(Long id);

}
