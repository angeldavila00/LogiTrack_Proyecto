package com.example.proyecto_logitrack.repository;

import com.example.proyecto_logitrack.modelo.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto,Long> {

    boolean existsByBodegaId(Long bodegaId);

    // Buscar todos los productos de una bodega
    List<Producto> findByBodegaId(Long bodegaId);

    // Buscar productos con stock menor a un valor (útil para alertas de stock bajo)
    List<Producto> findByStockLessThan(Integer stock);

    // Verificar si existe por nombre (útil para evitar duplicados)
    boolean existsByNombre(String nombre);
}
