package com.example.proyecto_logitrack.repository;

import com.example.proyecto_logitrack.modelo.MovimientoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoDetalleRepository extends JpaRepository<MovimientoDetalle,Long> {
    // Buscar todos los detalles de un movimiento
    List<MovimientoDetalle> findByMovimientoId(Long movimientoId);

    // Buscar todos los detalles que involucren un producto
    List<MovimientoDetalle> findByProductoId(Long productoId);


    boolean existsByProductoId(Long productoId);
}
