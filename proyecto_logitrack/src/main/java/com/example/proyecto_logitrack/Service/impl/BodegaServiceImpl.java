package com.example.proyecto_logitrack.Service.impl;

import com.example.proyecto_logitrack.Service.BodegaService;
import com.example.proyecto_logitrack.dto.request.BodegaRequestDTO;
import com.example.proyecto_logitrack.dto.response.BodegaResponseDTO;
import com.example.proyecto_logitrack.dto.response.UsuarioResponseDTO;
import com.example.proyecto_logitrack.mapper.BodegaMapper;
import com.example.proyecto_logitrack.mapper.UsuarioMapper;
import com.example.proyecto_logitrack.modelo.Bodega;
import com.example.proyecto_logitrack.modelo.Usuario;
import com.example.proyecto_logitrack.repository.BodegaRepository;
import com.example.proyecto_logitrack.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class BodegaServiceImpl implements BodegaService {

    private final BodegaRepository bodegaRepository;
    private final BodegaMapper bodegaMapper;
    private final UsuarioMapper usuarioMapper;
    private final UsuarioRepository usuarioRepository;

    @Override
    public BodegaResponseDTO crearBodega(BodegaRequestDTO dto) {
        Usuario u = usuarioRepository.findById(dto.usuarioId()).orElseThrow(() -> new RuntimeException("Error no existe del usuario"));
        Bodega b = bodegaMapper.DTOAentidad(dto,u);
        Bodega b_insertada= bodegaRepository.save(b);
        UsuarioResponseDTO dtoUsuario= usuarioMapper.entidadADTO(u);
        return bodegaMapper.entidadADTO(b_insertada,dtoUsuario);
    }

    @Override
    public BodegaResponseDTO actualizarBodega(BodegaRequestDTO dto, Long id) {
        Bodega b=bodegaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Error, no existe dicha Bodega a actualizar"));
        Usuario u = usuarioRepository.findById(dto.usuarioId()).orElseThrow(() -> new RuntimeException("Error no existe del usuario"));
        bodegaMapper.actualizarEntidadDesdeDTO(b,dto,u);
        Bodega b_actualizada= bodegaRepository.save(b);
        UsuarioResponseDTO dtoUsuario= usuarioMapper.entidadADTO(u);
        return bodegaMapper.entidadADTO(b_actualizada,dtoUsuario);
    }

    @Override
    public List<BodegaResponseDTO> listarBodegas() {
        return bodegaRepository.findAll().stream().map(dato -> bodegaMapper.entidadADTO(dato,usuarioMapper
                .entidadADTO(usuarioRepository.findById(dato.getUsuario().getId()).orElseThrow(
                ()->new RuntimeException("No existe la bodega")
        )))).toList();
    }

    @Override
    public BodegaResponseDTO buscarPorId(Long id) {
        Bodega b = bodegaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Error, no existe dicha Bodega"));
        Usuario u = usuarioRepository.findById(b.getUsuario().getId()).orElseThrow(() -> new RuntimeException("Error no existe del usuario"));
        UsuarioResponseDTO dtoUsuario= usuarioMapper.entidadADTO(u);
        return bodegaMapper.entidadADTO(b,dtoUsuario);
    }

    @Override
    public void eliminarBodega(Long id) {

        if(bodegaRepository.existsById(id)){
            throw new EntityNotFoundException("Error: No se puede eliminar");
        }
        bodegaRepository.deleteById(id);

    }
}
