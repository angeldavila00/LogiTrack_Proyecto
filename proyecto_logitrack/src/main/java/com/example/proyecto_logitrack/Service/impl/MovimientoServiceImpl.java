package com.example.proyecto_logitrack.Service.impl;

import com.example.proyecto_logitrack.Service.AuditoriaService;
import com.example.proyecto_logitrack.Service.MovimientoDetalleService;
import com.example.proyecto_logitrack.Service.MovimientoService;
import com.example.proyecto_logitrack.config.SecurityUtils;
import com.example.proyecto_logitrack.dto.request.MovimientoDetalleRequestDTO;
import com.example.proyecto_logitrack.dto.request.MovimientoRequestDTO;
import com.example.proyecto_logitrack.dto.response.BodegaResponseDTO;
import com.example.proyecto_logitrack.dto.response.MovimientoResponseDTO;
import com.example.proyecto_logitrack.dto.response.UsuarioResponseDTO;
import com.example.proyecto_logitrack.mapper.BodegaMapper;
import com.example.proyecto_logitrack.mapper.MovimientoMapper;
import com.example.proyecto_logitrack.mapper.UsuarioMapper;
import com.example.proyecto_logitrack.modelo.*;
import com.example.proyecto_logitrack.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final MovimientoMapper movimientoMapper;
    private final UsuarioMapper usuarioMapper;
    private final UsuarioRepository usuarioRepository;
    private final BodegaRepository bodegaRepository;
    private final BodegaMapper bodegaMapper;
    private final MovimientoDetalleService movimientoDetalleService;
    private final MovimientoDetalleRepository movimientoDetalleRepository;
    private final ProductoRepository productoRepository;
    private final AuditoriaService auditoriaService;

    @Override
    public MovimientoResponseDTO crearMovimiento(MovimientoRequestDTO dto) {
        Usuario u = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Bodega bodegaOrigen = bodegaRepository.findById(dto.bodegaOrigenId())
                .orElseThrow(() -> new RuntimeException("Bodega origen no encontrada"));
        Bodega bodegaDestino = bodegaRepository.findById(dto.bodegaDestinoId())
                .orElseThrow(() -> new RuntimeException("Bodega destino no encontrada"));

        Movimiento m = movimientoMapper.DTOAentidad(dto, u, bodegaOrigen, bodegaDestino);
        Movimiento m_insertada = movimientoRepository.save(m);

        if (dto.detalles() != null && !dto.detalles().isEmpty()) {
            int stockActualBodegaDestino = productoRepository.findByBodegaId(bodegaDestino.getId())
                    .stream().mapToInt(Producto::getStock).sum();

            int totalEntrante = dto.detalles().stream()
                    .mapToInt(MovimientoDetalleRequestDTO::cantidad).sum();

            if (stockActualBodegaDestino + totalEntrante > bodegaDestino.getCapacidad()) {
                throw new RuntimeException("Error: la bodega destino no tiene capacidad suficiente...");
            }

            for (MovimientoDetalleRequestDTO detalleDTO : dto.detalles()) {
                MovimientoDetalleRequestDTO detalleConMovimiento = new MovimientoDetalleRequestDTO(
                        detalleDTO.cantidad(), m_insertada.getId(), detalleDTO.productoId());
                movimientoDetalleService.crearMovimientoDetalle(detalleConMovimiento);
            }
        }

        UsuarioResponseDTO dtoUsuario = usuarioMapper.entidadADTO(u);
        BodegaResponseDTO dtoOrigen = bodegaMapper.entidadADTO(bodegaOrigen, dtoUsuario);
        BodegaResponseDTO dtoDestino = bodegaMapper.entidadADTO(bodegaDestino, dtoUsuario);

        // ✅ usuario logueado
        usuarioRepository.findByUsername(SecurityUtils.getUsuarioActual())
                .ifPresent(responsable -> auditoriaService.registrar("movimiento", Operacion.INSERT, null,
                        "id=" + m_insertada.getId() + ", tipo=" + m_insertada.getTipomovimiento(),
                        responsable.getId()));

        return movimientoMapper.entidadADTO(m_insertada, dtoUsuario, dtoOrigen, dtoDestino);
    }

    @Override
    public MovimientoResponseDTO actulizarMovimiento(MovimientoRequestDTO dto, Long id) {
        Movimiento m = movimientoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Error: no existe dicho Movimiento a actualizar"));

        String valorAnterior = "tipo=" + m.getTipomovimiento() + ", origen=" + m.getBodegaOrigen().getNombre()
                + ", destino=" + m.getBodegaDestino().getNombre();

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

        // ✅ usuario logueado
        usuarioRepository.findByUsername(SecurityUtils.getUsuarioActual())
                .ifPresent(responsable -> auditoriaService.registrar("movimiento", Operacion.UPDATE,
                        valorAnterior,
                        "tipo=" + m_actualizado.getTipomovimiento() + ", origen=" + bodegaOrigen.getNombre()
                                + ", destino=" + bodegaDestino.getNombre(),
                        responsable.getId()));

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
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Error: no existe el Movimiento a eliminar"));

        String valorAnterior = "id=" + id + ", tipo=" + movimiento.getTipomovimiento()
                + ", origen=" + movimiento.getBodegaOrigen().getNombre()
                + ", destino=" + movimiento.getBodegaDestino().getNombre();

        // Recuperar stock de cada producto antes de eliminar
        List<MovimientoDetalle> detalles = movimientoDetalleRepository.findByMovimientoId(id);
        for (MovimientoDetalle detalle : detalles) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
            productoRepository.save(producto);
        }

        movimientoDetalleRepository.deleteAll(detalles);
        movimientoRepository.delete(movimiento);

        // ✅ usuario logueado — registrar después de eliminar
        usuarioRepository.findByUsername(SecurityUtils.getUsuarioActual())
                .ifPresent(responsable -> auditoriaService.registrar("movimiento", Operacion.DELETE,
                        valorAnterior, null, responsable.getId()));
    }
}
