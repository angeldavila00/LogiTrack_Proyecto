package com.example.proyecto_logitrack.repository;

import com.example.proyecto_logitrack.modelo.Rol;
import com.example.proyecto_logitrack.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,Long> {

    List<Usuario>findByNombreIgnoreCase(String nombre);
    // Útil para validar documento duplicado
    boolean existsByDocumento(String documento);

    // Buscar usuarios por rol
    List<Usuario> findByRol(Rol rol);

}
