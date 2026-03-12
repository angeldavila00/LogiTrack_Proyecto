package com.example.proyecto_logitrack.Service.impl;

import com.example.proyecto_logitrack.Service.MovimientoService;
import com.example.proyecto_logitrack.dto.request.MovimientoDetalleRequestDTO;
import com.example.proyecto_logitrack.dto.request.MovimientoRequestDTO;
import com.example.proyecto_logitrack.dto.response.BodegaResponseDTO;
import com.example.proyecto_logitrack.dto.response.MovimientoResponseDTO;
import com.example.proyecto_logitrack.dto.response.UsuarioResponseDTO;
import com.example.proyecto_logitrack.mapper.BodegaMapper;
import com.example.proyecto_logitrack.mapper.MovimientoMapper;
import com.example.proyecto_logitrack.mapper.UsuarioMapper;
import com.example.proyecto_logitrack.modelo.Bodega;
import com.example.proyecto_logitrack.modelo.Movimiento;
import com.example.proyecto_logitrack.modelo.Usuario;
import com.example.proyecto_logitrack.repository.BodegaRepository;
import com.example.proyecto_logitrack.repository.MovimientoRepository;
import com.example.proyecto_logitrack.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final MovimientoMapper movimientoMapper;
    private final UsuarioMapper usuarioMapper;
    private final UsuarioRepository usuarioRepository;
    private final BodegaRepository bodegaRepository;
    private final BodegaMapper bodegaMapper;


    @Override
    public MovimientoResponseDTO crearMovimiento(MovimientoRequestDTO dto) {
        Usuario u= usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(()-> new RuntimeException("Usuario no encontrado"));
        Bodega bodegaOrigen = bodegaRepository.findById(dto.bodegaOrigenId())
                .orElseThrow(()-> new RuntimeException("Bodega origen no encontrada"));
        Bodega bodegaDestino = bodegaRepository.findById(dto.bodegaDestinoId())
                .orElseThrow(()-> new RuntimeException("Bodega destino no encontrada"));
        Movimiento m = movimientoMapper.DTOAentidad(dto,u,bodegaOrigen,bodegaDestino);
        Movimiento m_insertada = movimientoRepository.save(m);

        UsuarioResponseDTO dtoUsuario = usuarioMapper.entidadADTO(u);
        BodegaResponseDTO dtoOrigen = bodegaMapper.entidadADTO(bodegaOrigen,dtoUsuario);
        BodegaResponseDTO dtoDestino = bodegaMapper.entidadADTO(bodegaDestino,dtoUsuario);

        return movimientoMapper.entidadADTO(m_insertada, dtoUsuario, dtoOrigen, dtoDestino);

    }

    @Override
    public MovimientoResponseDTO actulizarMovimiento(MovimientoRequestDTO dto, Long id) {
        Movimiento m = movimientoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Error: no existe dicho Movimiento a actualizar"));

        Usuario u = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("Error: no existe el usuario"));
        Bodega bodegaOrigen = bodegaRepository.findById(dto.bodegaOrigenId())
                .orElseThrow(() -> new RuntimeException("Error: no existe la bodega origen"));
        Bodega bodegaDestino = bodegaRepository.findById(dto.bodegaDestinoId())
                .orElseThrow(() -> new RuntimeException("Error: no existe la bodega destino"));

        movimientoMapper.actualizarEntidadDesdeDTO(m, dto, u, bodegaOrigen, bodegaDestino);
        Movimiento m_actualizado = movimientoRepository.save(m);

        UsuarioResponseDTO dtoUsuario = usuarioMapper.entidadADTO(u);
        BodegaResponseDTO dtoOrigen = bodegaMapper.entidadADTO(bodegaOrigen, dtoUsuario);
        BodegaResponseDTO dtoDestino = bodegaMapper.entidadADTO(bodegaDestino, dtoUsuario);

        return movimientoMapper.entidadADTO(m_actualizado, dtoUsuario, dtoOrigen, dtoDestino);
    }

    @Override
    public List<MovimientoResponseDTO> listarMovimientos() {
        return movimientoRepository.findAll().stream().map(dato -> {
            UsuarioResponseDTO dtoUsuario = usuarioMapper.entidadADTO(
                    usuarioRepository.findById(dato.getUsuario().getId())
                            .orElseThrow(() -> new RuntimeException("Error: no existe el usuario")));

            BodegaResponseDTO dtoOrigen = bodegaMapper.entidadADTO(
                    bodegaRepository.findById(dato.getBodegaOrigen().getId())
                            .orElseThrow(() -> new RuntimeException("Error: no existe la bodega origen")), dtoUsuario);

            BodegaResponseDTO dtoDestino = bodegaMapper.entidadADTO(
                    bodegaRepository.findById(dato.getBodegaDestino().getId())
                            .orElseThrow(() -> new RuntimeException("Error: no existe la bodega destino")), dtoUsuario);

            return movimientoMapper.entidadADTO(dato, dtoUsuario, dtoOrigen, dtoDestino);
        }).toList();

    }

    @Override
    public MovimientoResponseDTO buscarPorId(Long id) {
        Movimiento m = movimientoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Error: no existe dicho Movimiento"));

        UsuarioResponseDTO dtoUsuario = usuarioMapper.entidadADTO(
                usuarioRepository.findById(m.getUsuario().getId())
                        .orElseThrow(() -> new RuntimeException("Error: no existe el usuario")));

        BodegaResponseDTO dtoOrigen = bodegaMapper.entidadADTO(
                bodegaRepository.findById(m.getBodegaOrigen().getId())
                        .orElseThrow(() -> new RuntimeException("Error: no existe la bodega origen")), dtoUsuario);

        BodegaResponseDTO dtoDestino = bodegaMapper.entidadADTO(
                bodegaRepository.findById(m.getBodegaDestino().getId())
                        .orElseThrow(() -> new RuntimeException("Error: no existe la bodega destino")), dtoUsuario);

        return movimientoMapper.entidadADTO(m, dtoUsuario, dtoOrigen, dtoDestino);
    }

    @Override
    public void eliminarMovimiento(Long id) {
        if (!movimientoRepository.existsById(id)) {
            throw new EntityNotFoundException("Error: no existe el Movimiento a eliminar");
        }
        movimientoRepository.deleteById(id);

    }
}
