package com.example.proyecto_logitrack.Service.impl;

import com.example.proyecto_logitrack.Service.UsuarioService;
import com.example.proyecto_logitrack.dto.request.UsuarioRequestDTO;
import com.example.proyecto_logitrack.dto.response.UsuarioResponseDTO;
import com.example.proyecto_logitrack.mapper.UsuarioMapper;
import com.example.proyecto_logitrack.modelo.Usuario;
import com.example.proyecto_logitrack.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    public UsuarioResponseDTO crear(UsuarioRequestDTO dto) {
        Usuario u= usuarioMapper.DTOAentidad(dto);
        Usuario u_insertada = usuarioRepository.save(u);
        return usuarioMapper.entidadADTO(u_insertada);

    }


    @Override
    public List<UsuarioResponseDTO> listar() {
        List<Usuario> usuario = usuarioRepository.findAll();
        return usuario.stream().map(usuarioMapper::entidadADTO).toList();
    }


    @Override
    public UsuarioResponseDTO buscarPorId(Long id) {
            Usuario usuario = usuarioRepository.findById(id).orElseThrow(()-> new RuntimeException("No existe el usuario"));
            return usuarioMapper.entidadADTO(usuario);
    }

    @Override
    public UsuarioResponseDTO actualizar(UsuarioRequestDTO dto,Long id) {
        Usuario u = usuarioRepository.findById(id).orElseThrow(()-> new RuntimeException("No existe el usuario"));
        usuarioMapper.actualizarEntidadDesdeDTO(u, dto);
        Usuario u_actualizada=usuarioRepository.save(u);
        return usuarioMapper.entidadADTO(u_actualizada);
    }

    @Override
    public void eliminar(Long id) {
        Usuario u= usuarioRepository.findById(id).orElseThrow(()-> new RuntimeException("No existe el usuario"));
        usuarioRepository.delete(u);

    }
}
