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
                .orElseThrow(() -> new RuntimeException("Error: no existe el Usuario"));
        Bodega bodegaOrigen = bodegaRepository.findById(dto.bodegaOrigenId())
                .orElseThrow(() -> new RuntimeException("Error: no existe la Bodega origen"));
        Bodega bodegaDestino = bodegaRepository.findById(dto.bodegaDestinoId())
                .orElseThrow(() -> new RuntimeException("Error: no existe la Bodega destino"));

        Movimiento m = movimientoMapper.DTOAentidad(dto, u, bodegaOrigen, bodegaDestino);
        Movimiento m_insertada = movimientoRepository.save(m);

        if (dto.detalles() != null && !dto.detalles().isEmpty()) {

            if (dto.tipoMovimiento() == TipoMovimiento.ENTRADA ||
                    dto.tipoMovimiento() == TipoMovimiento.TRANSFERENCIA) {

                // cuenta productos distintos, NO suma stock
                int productosActuales = productoRepository.findByBodegaId(bodegaDestino.getId()).size();
                int productosEntrantes = dto.detalles().size();

                if (productosActuales + productosEntrantes > bodegaDestino.getCapacidad()) {
                    throw new RuntimeException("Error: la Bodega destino no tiene capacidad suficiente. " +
                            "Capacidad máxima: " + bodegaDestino.getCapacidad() +
                            ", ocupada: " + productosActuales +
                            ", disponible: " + (bodegaDestino.getCapacidad() - productosActuales));
                }
            }

            for (MovimientoDetalleRequestDTO detalleDTO : dto.detalles()) {
                MovimientoDetalleRequestDTO detalleConMovimiento = new MovimientoDetalleRequestDTO(
                        detalleDTO.cantidad(), m_insertada.getId(), detalleDTO.productoId());
                movimientoDetalleService.crearMovimientoDetalle(detalleConMovimiento);
            }
        }

        UsuarioResponseDTO dtoUsuario = usuarioMapper.entidadADTO(u);
        BodegaResponseDTO dtoOrigen = bodegaMapper.entidadADTO(
                bodegaOrigen, usuarioMapper.entidadADTO(bodegaOrigen.getUsuario()));
        BodegaResponseDTO dtoDestino = bodegaMapper.entidadADTO(
                bodegaDestino, usuarioMapper.entidadADTO(bodegaDestino.getUsuario()));

        usuarioRepository.findByUsername(SecurityUtils.getUsuarioActual())
                .ifPresent(responsable -> auditoriaService.registrar("movimiento", Operacion.INSERT, null,
                        "id=" + m_insertada.getId() + ", tipo=" + m_insertada.getTipomovimiento()
                                + ", usuario=" + u.getNombre(),
                        responsable.getId(), responsable.getNombre()));

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
                        responsable.getId(), responsable.getNombre()));

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

        List<MovimientoDetalle> detalles = movimientoDetalleRepository.findByMovimientoId(id);

        for (MovimientoDetalle detalle : detalles) {
            Producto producto = detalle.getProducto();

            if (movimiento.getTipomovimiento() == TipoMovimiento.ENTRADA) {
                // Si fue ENTRADA, al eliminar se resta el stock
                producto.setStock(producto.getStock() - detalle.getCantidad());

            } else if (movimiento.getTipomovimiento() == TipoMovimiento.SALIDA) {
                // Si fue SALIDA, al eliminar se devuelve el stock
                producto.setStock(producto.getStock() + detalle.getCantidad());

            } else if (movimiento.getTipomovimiento() == TipoMovimiento.TRANSFERENCIA) {
                // Si fue TRANSFERENCIA, al eliminar se invierte el movimiento
                producto.setStock(producto.getStock() + detalle.getCantidad());
            }

            productoRepository.save(producto);
        }
        if (!detalles.isEmpty()) {
            throw new RuntimeException("Error: no se puede eliminar el Movimiento porque tiene Detalles asociados.");
        }

        movimientoRepository.delete(movimiento);

        usuarioRepository.findByUsername(SecurityUtils.getUsuarioActual())
                .ifPresent(responsable -> auditoriaService.registrar("movimiento", Operacion.DELETE,
                        valorAnterior, null, responsable.getId(), responsable.getNombre()));
    }
}