package com.example.proyecto_logitrack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.proyecto_logitrack.modelo.Movimiento;
import com.example.proyecto_logitrack.modelo.TipoMovimiento;

public interface MovimientoRepository extends JpaRepository<Movimiento,Long> {

    // Buscar movimientos por tipo (ENTRADA, SALIDA, TRASLADO, etc.)
    List<Movimiento> findByTipomovimiento(TipoMovimiento tipomovimiento);

    // Top 10 fecha descendente
    List<Movimiento> findTop10ByOrderByFechaDesc();

    // Contar por tipo
    long countByTipomovimiento(TipoMovimiento tipo);


}
