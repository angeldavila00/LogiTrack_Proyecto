package com.example.proyecto_logitrack.Service.impl;

import com.example.proyecto_logitrack.Service.AuditoriaService;
import com.example.proyecto_logitrack.Service.BodegaService;
import com.example.proyecto_logitrack.config.SecurityUtils;
import com.example.proyecto_logitrack.dto.request.BodegaRequestDTO;
import com.example.proyecto_logitrack.dto.response.BodegaResponseDTO;
import com.example.proyecto_logitrack.dto.response.UsuarioResponseDTO;
import com.example.proyecto_logitrack.mapper.BodegaMapper;
import com.example.proyecto_logitrack.mapper.UsuarioMapper;
import com.example.proyecto_logitrack.modelo.Bodega;
import com.example.proyecto_logitrack.modelo.Operacion;
import com.example.proyecto_logitrack.modelo.Usuario;
import com.example.proyecto_logitrack.repository.BodegaRepository;
import com.example.proyecto_logitrack.repository.ProductoRepository;
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
    private final ProductoRepository  productoRepository;

    @Override
    public BodegaResponseDTO crearBodega(BodegaRequestDTO dto) {
        Usuario u = usuarioRepository.findById(dto.usuarioId()).orElseThrow(() -> new RuntimeException("Error no existe del usuario"));
        Bodega b = bodegaMapper.DTOAentidad(dto,u);
        Bodega b_insertada= bodegaRepository.save(b);
        UsuarioResponseDTO dtoUsuario= usuarioMapper.entidadADTO(u);

        usuarioRepository.findByUsername(SecurityUtils.getUsuarioActual())
                .ifPresent(responsable -> auditoriaService.registrar( "bodega", Operacion.INSERT, null,
                        "id=" + b_insertada.getId() + ", nombre=" +b_insertada.getNombre()+ ", ubicacion=" + b_insertada.getUbicacion() + ", capacidad=" + b_insertada.getCapacidad() + ", encargado=" + u.getNombre(),
                        responsable.getId(), responsable.getNombre()));

        return bodegaMapper.entidadADTO(b_insertada,dtoUsuario);
    }

    @Override
    public BodegaResponseDTO actualizarBodega(BodegaRequestDTO dto, Long id) {
        Bodega b = bodegaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Error, no existe dicha Bodega"));

        Usuario u = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("Error no existe el usuario"));

        String valorAnterior = "nombre=" + b.getNombre() + ", ubicacion=" + b.getUbicacion() + ", capacidad=" + b.getCapacidad() + ", encargado=" + u.getNombre();


        bodegaMapper.actualizarEntidadDesdeDTO(b, dto, u);
        Bodega b_actualizada = bodegaRepository.save(b);

        usuarioRepository.findByUsername(SecurityUtils.getUsuarioActual())
                .ifPresent(responsable -> auditoriaService.registrar( "bodega", Operacion.UPDATE, valorAnterior,
                        "Nombre=" + b_actualizada.getNombre() + ", ubicacion=" + b_actualizada.getUbicacion() + ", capacidad=" + b_actualizada.getCapacidad() + ", encargado=" + u.getNombre(),
                        responsable.getId(), responsable.getNombre()));

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

        Bodega bodega = bodegaRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Error: No existe la Bodega a eliminar"));
        if (productoRepository.existsByBodegaId(id)) {
            throw new RuntimeException("Error: no se puede eliminar la Bodega porque esta asociado a un Producto");
        }

        Usuario u = usuarioRepository.findById(bodega.getUsuario().getId()).orElseThrow(() -> new RuntimeException("Error no existe del usuario"));

        String valorAnterior = "id=" + id + ", nombre=" + bodega.getNombre()
                + ", ubicacion=" + bodega.getUbicacion()
                + ", capacidad=" + bodega.getCapacidad()
                + ", encargado=" + u.getNombre();
        bodegaRepository.deleteById(id);

        usuarioRepository.findByUsername(SecurityUtils.getUsuarioActual())
                .ifPresent(responsable -> auditoriaService.registrar("bodega", Operacion.DELETE,
                        valorAnterior, null, responsable.getId(), responsable.getNombre()));

    }
}
