package com.example.proyecto_logitrack.repository;

import com.example.proyecto_logitrack.modelo.Bodega;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BodegaRepository extends JpaRepository<Bodega,Long> {
    // Buscar por nombre exacto
    List<Bodega> findByNombre(String nombre);

    // Buscar por nombre parcial
    List<Bodega> findByNombreContainingIgnoreCase(String nombre);

    // Buscar bodegas por ubicación
    List<Bodega> findByUbicacion(String ubicacion);

    // Buscar bodegas con capacidad mayor a un valor (útil para saber dónde hay espacio)
    List<Bodega> findByCapacidadGreaterThan(Integer capacidad);

    // Buscar todas las bodegas a cargo de un usuario
    List<Bodega> findByUsuarioId(Long usuarioId);

    // Verificar si existe una bodega con ese nombre (útil para evitar duplicados)
    boolean existsByNombre(String nombre);

}
