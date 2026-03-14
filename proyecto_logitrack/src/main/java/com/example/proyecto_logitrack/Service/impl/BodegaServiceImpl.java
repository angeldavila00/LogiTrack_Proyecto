package com.example.proyecto_logitrack.Service.impl;

import com.example.proyecto_logitrack.Service.AuditoriaService;
import com.example.proyecto_logitrack.Service.BodegaService;
import com.example.proyecto_logitrack.dto.request.BodegaRequestDTO;
import com.example.proyecto_logitrack.dto.response.BodegaResponseDTO;
import com.example.proyecto_logitrack.dto.response.UsuarioResponseDTO;
import com.example.proyecto_logitrack.mapper.BodegaMapper;
import com.example.proyecto_logitrack.mapper.UsuarioMapper;
import com.example.proyecto_logitrack.modelo.Bodega;
import com.example.proyecto_logitrack.modelo.Operacion;
import com.example.proyecto_logitrack.modelo.Usuario;
import com.example.proyecto_logitrack.repository.BodegaRepository;
import com.example.proyecto_logitrack.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class BodegaServiceImpl implements BodegaService {

    private final BodegaRepository bodegaRepository;
    private final BodegaMapper bodegaMapper;
    private final UsuarioMapper usuarioMapper;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;

    @Override
    public BodegaResponseDTO crearBodega(BodegaRequestDTO dto) {
        Usuario u = usuarioRepository.findById(dto.usuarioId()).orElseThrow(() -> new RuntimeException("Error no existe del usuario"));
        Bodega b = bodegaMapper.DTOAentidad(dto,u);
        Bodega b_insertada= bodegaRepository.save(b);
        UsuarioResponseDTO dtoUsuario= usuarioMapper.entidadADTO(u);
        auditoriaService.registrar("bodega", Operacion.INSERT, null,
                "id=" + b_insertada.getId() + ", nombre=" + b_insertada.getNombre(),
                dto.usuarioId());
        return bodegaMapper.entidadADTO(b_insertada,dtoUsuario);
    }

    @Override
    public BodegaResponseDTO actualizarBodega(BodegaRequestDTO dto, Long id) {
        Bodega b = bodegaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Error, no existe dicha Bodega"));
        String valorAnterior = "nombre=" + b.getNombre() + ", ubicacion=" + b.getUbicacion();

        Usuario u = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("Error no existe el usuario"));
        bodegaMapper.actualizarEntidadDesdeDTO(b, dto, u);
        Bodega b_actualizada = bodegaRepository.save(b);

        auditoriaService.registrar("bodega", Operacion.UPDATE,
                valorAnterior,
                "nombre=" + b_actualizada.getNombre() + ", ubicacion=" + b_actualizada.getUbicacion(),
                dto.usuarioId());

        UsuarioResponseDTO dtoUsuario = usuarioMapper.entidadADTO(u);
        return bodegaMapper.entidadADTO(b_actualizada, dtoUsuario);
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
        if(!bodegaRepository.existsById(id)){
            throw new EntityNotFoundException("Error: No existe la Bodega a eliminar");
        }
        bodegaRepository.deleteById(id);
    }
}
