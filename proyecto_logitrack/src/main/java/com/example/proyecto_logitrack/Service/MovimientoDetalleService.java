package com.example.proyecto_logitrack.Service;

import com.example.proyecto_logitrack.dto.request.MovimientoDetalleRequestDTO;
import com.example.proyecto_logitrack.dto.response.MovimientoDetalleResponseDTO;

import java.util.List;

public interface MovimientoDetalleService {
    MovimientoDetalleResponseDTO crearMovimientoDetalle(MovimientoDetalleRequestDTO dto);
    MovimientoDetalleResponseDTO actualizarMovimientoDetalle(MovimientoDetalleRequestDTO dto, Long id);
    List<MovimientoDetalleResponseDTO> listarMovimientoDetalles();
    MovimientoDetalleResponseDTO buscarPorId(Long id);
    void eliminarMovimientoDetalle(Long id);
}
