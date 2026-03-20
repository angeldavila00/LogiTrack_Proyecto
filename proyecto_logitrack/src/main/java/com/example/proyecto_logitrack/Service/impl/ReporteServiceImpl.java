package com.example.proyecto_logitrack.Service.impl;

import com.example.proyecto_logitrack.Service.ReporteService;
import com.example.proyecto_logitrack.dto.response.ReporteResponseDTO;
import com.example.proyecto_logitrack.modelo.TipoMovimiento;
import com.example.proyecto_logitrack.repository.MovimientoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ReporteServiceImpl implements ReporteService {
    private final MovimientoRepository movimientoRepository;

    @Override
    public ReporteResponseDTO reporteDTO() {
        long total = movimientoRepository.count();
        long entradas = movimientoRepository.countByTipomovimiento(TipoMovimiento.ENTRADA);
        long salidas = movimientoRepository.countByTipomovimiento(TipoMovimiento.SALIDA);
        long transferencias = movimientoRepository.countByTipomovimiento(TipoMovimiento.TRANSFERENCIA);

        return new ReporteResponseDTO(total, entradas, salidas, transferencias);
    }
}
