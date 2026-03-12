package com.example.proyecto_logitrack.repository;

import com.example.proyecto_logitrack.modelo.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoRepository extends JpaRepository<Movimiento,Long> {
}
