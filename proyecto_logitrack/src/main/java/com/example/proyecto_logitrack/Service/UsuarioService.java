package com.example.proyecto_logitrack.Service;

import com.example.proyecto_logitrack.dto.request.UsuarioRequestDTO;
import com.example.proyecto_logitrack.dto.response.UsuarioResponseDTO;

import java.util.List;

public interface UsuarioService {
    UsuarioResponseDTO crear (UsuarioRequestDTO dto);
    List<UsuarioResponseDTO>listar();
    UsuarioResponseDTO buscarPorId(Long id);

    UsuarioResponseDTO actualizar(UsuarioRequestDTO dto,Long id);

    void eliminar(Long id);
}
