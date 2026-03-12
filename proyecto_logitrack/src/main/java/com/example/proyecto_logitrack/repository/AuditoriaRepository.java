package com.example.proyecto_logitrack.repository;

import com.example.proyecto_logitrack.modelo.Auditoria;
import com.example.proyecto_logitrack.modelo.Operacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface AuditoriaRepository extends JpaRepository<Auditoria,Long> {
    // Buscar auditorías por entidad (ej: "Producto", "Bodega", "Movimiento")
    List<Auditoria> findByEntidad(String entidad);

    // Buscar auditorías por tipo de operación (INSERT, UPDATE, DELETE)
    List<Auditoria> findByOperacion(Operacion operacion);

    // Buscar auditorías realizadas por un usuario
    List<Auditoria> findByUsuarioId(Long usuarioId);

    // Buscar auditorías en un rango de fechas
    List<Auditoria> findByFechaBetween(Date fechaInicio, Date fechaFin);

    // Buscar auditorías por entidad y operación (ej: todos los DELETE de Producto)
    List<Auditoria> findByEntidadAndOperacion(String entidad, Operacion operacion);

    // Buscar auditorías de un usuario en un rango de fechas
    List<Auditoria> findByUsuarioIdAndFechaBetween(Long usuarioId, Date fechaInicio, Date fechaFin);
}
