package com.example.proyecto_logitrack.repository;

import com.example.proyecto_logitrack.modelo.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto,Long> {
}
