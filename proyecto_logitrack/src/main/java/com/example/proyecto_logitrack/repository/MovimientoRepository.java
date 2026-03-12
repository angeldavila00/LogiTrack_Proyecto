package com.example.proyecto_logitrack.repository;

import com.example.proyecto_logitrack.modelo.Movimiento;
import com.example.proyecto_logitrack.modelo.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento,Long> {

    // Buscar movimientos por tipo (ENTRADA, SALIDA, TRASLADO, etc.)
    List<Movimiento> findByTipomovimiento(TipoMovimiento tipomovimiento);

    // Buscar movimientos de un usuario
    List<Movimiento> findByUsuarioId(Long usuarioId);

    // Buscar movimientos por bodega origen
    List<Movimiento> findByBodegaOrigenId(Long bodegaOrigenId);

    // Buscar movimientos por bodega destino
    List<Movimiento> findByBodegaDestinoId(Long bodegaDestinoId);

    // Buscar movimientos en un rango de fechas
    List<Movimiento> findByFechaBetween(Date fechaInicio, Date fechaFin);
}
