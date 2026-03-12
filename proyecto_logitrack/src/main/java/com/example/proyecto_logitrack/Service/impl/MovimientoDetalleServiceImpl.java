package com.example.proyecto_logitrack.Service.impl;

import com.example.proyecto_logitrack.Service.MovimientoDetalleService;
import com.example.proyecto_logitrack.dto.request.MovimientoDetalleRequestDTO;
import com.example.proyecto_logitrack.dto.response.*;
import com.example.proyecto_logitrack.mapper.*;
import com.example.proyecto_logitrack.modelo.Movimiento;
import com.example.proyecto_logitrack.modelo.MovimientoDetalle;
import com.example.proyecto_logitrack.modelo.Producto;
import com.example.proyecto_logitrack.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoDetalleServiceImpl implements MovimientoDetalleService {
    private final MovimientoDetalleRepository movimientoDetalleRepository;
    private final MovimientoDetalleMapper movimientoDetalleMapper;
    private final MovimientoRepository movimientoRepository;
    private final MovimientoMapper movimientoMapper;
    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;
    private final BodegaRepository bodegaRepository;
    private final BodegaMapper bodegaMapper;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    @Override
    public MovimientoDetalleResponseDTO crearMovimientoDetalle(MovimientoDetalleRequestDTO dto) {
        Movimiento movimiento = movimientoRepository.findById(dto.movimientoId())
                .orElseThrow(() -> new RuntimeException("Error: no existe el movimiento"));

        Producto producto = productoRepository.findById(dto.productoId())
                .orElseThrow(() -> new RuntimeException("Error: no existe el producto"));

        MovimientoDetalle md = movimientoDetalleMapper.DTOAentidad(dto, movimiento, producto);
        MovimientoDetalle md_insertado = movimientoDetalleRepository.save(md);

        UsuarioResponseDTO dtoUsuario = usuarioMapper.entidadADTO(
                usuarioRepository.findById(movimiento.getUsuario().getId())
                        .orElseThrow(() -> new RuntimeException("Error: no existe el usuario")));

        BodegaResponseDTO dtoOrigen = bodegaMapper.entidadADTO(
                bodegaRepository.findById(movimiento.getBodegaOrigen().getId())
                        .orElseThrow(() -> new RuntimeException("Error: no existe la bodega origen")), dtoUsuario);

        BodegaResponseDTO dtoDestino = bodegaMapper.entidadADTO(
                bodegaRepository.findById(movimiento.getBodegaDestino().getId())
                        .orElseThrow(() -> new RuntimeException("Error: no existe la bodega destino")), dtoUsuario);

        MovimientoResponseDTO dtoMovimiento = movimientoMapper.entidadADTO(movimiento, dtoUsuario, dtoOrigen, dtoDestino);

        UsuarioResponseDTO dtoUsuarioBodega = usuarioMapper.entidadADTO(
                usuarioRepository.findById(producto.getBodega().getUsuario().getId())
                        .orElseThrow(() -> new RuntimeException("Error: no existe el usuario de la bodega")));

        BodegaResponseDTO dtoBodegaProducto = bodegaMapper.entidadADTO(
                bodegaRepository.findById(producto.getBodega().getId())
                        .orElseThrow(() -> new RuntimeException("Error: no existe la bodega del producto")), dtoUsuarioBodega);

        ProductoResponseDTO dtoProducto = productoMapper.entidadADTO(producto, dtoBodegaProducto);

        return movimientoDetalleMapper.entidadADTO(md_insertado, dtoMovimiento, dtoProducto);
    }

    @Override
    public MovimientoDetalleResponseDTO actualizarMovimientoDetalle(MovimientoDetalleRequestDTO dto, Long id) {
        MovimientoDetalle md = movimientoDetalleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Error: no existe dicho MovimientoDetalle a actualizar"));

        Movimiento movimiento = movimientoRepository.findById(dto.movimientoId())
                .orElseThrow(() -> new RuntimeException("Error: no existe el movimiento"));

        Producto producto = productoRepository.findById(dto.productoId())
                .orElseThrow(() -> new RuntimeException("Error: no existe el producto"));

        movimientoDetalleMapper.actualizarEntidadDesdeDTO(md, dto, movimiento, producto);

        MovimientoDetalle md_actualizado = movimientoDetalleRepository.save(md);

        UsuarioResponseDTO dtoUsuario = usuarioMapper.entidadADTO(
                usuarioRepository.findById(movimiento.getUsuario().getId())
                        .orElseThrow(() -> new RuntimeException("Error: no existe el usuario")));

        BodegaResponseDTO dtoOrigen = bodegaMapper.entidadADTO(
                bodegaRepository.findById(movimiento.getBodegaOrigen().getId())
                        .orElseThrow(() -> new RuntimeException("Error: no existe la bodega origen")), dtoUsuario);

        BodegaResponseDTO dtoDestino = bodegaMapper.entidadADTO(
                bodegaRepository.findById(movimiento.getBodegaDestino().getId())
                        .orElseThrow(() -> new RuntimeException("Error: no existe la bodega destino")), dtoUsuario);

        MovimientoResponseDTO dtoMovimiento = movimientoMapper.entidadADTO(md_actualizado.getMovimiento(), dtoUsuario, dtoOrigen, dtoDestino);

        UsuarioResponseDTO dtoUsuarioBodega = usuarioMapper.entidadADTO(
                usuarioRepository.findById(producto.getBodega().getUsuario().getId())
                        .orElseThrow(() -> new RuntimeException("Error: no existe el usuario de la bodega")));

        BodegaResponseDTO dtoBodegaProducto = bodegaMapper.entidadADTO(
                bodegaRepository.findById(producto.getBodega().getId())
                        .orElseThrow(() -> new RuntimeException("Error: no existe la bodega del producto")), dtoUsuarioBodega);

        ProductoResponseDTO dtoProducto = productoMapper.entidadADTO(producto, dtoBodegaProducto);

        return movimientoDetalleMapper.entidadADTO(md_actualizado, dtoMovimiento, dtoProducto);
    }

    @Override
    public List<MovimientoDetalleResponseDTO> listarMovimientoDetalles() {
        return movimientoDetalleRepository.findAll().stream().map(dato -> {
            Movimiento movimiento = movimientoRepository.findById(dato.getMovimiento().getId())
                    .orElseThrow(() -> new RuntimeException("Error: no existe el movimiento"));

            Producto producto = productoRepository.findById(dato.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Error: no existe el producto"));

            UsuarioResponseDTO dtoUsuario = usuarioMapper.entidadADTO(
                    usuarioRepository.findById(movimiento.getUsuario().getId())
                            .orElseThrow(() -> new RuntimeException("Error: no existe el usuario")));

            BodegaResponseDTO dtoOrigen = bodegaMapper.entidadADTO(
                    bodegaRepository.findById(movimiento.getBodegaOrigen().getId())
                            .orElseThrow(() -> new RuntimeException("Error: no existe la bodega origen")), dtoUsuario);

            BodegaResponseDTO dtoDestino = bodegaMapper.entidadADTO(
                    bodegaRepository.findById(movimiento.getBodegaDestino().getId())
                            .orElseThrow(() -> new RuntimeException("Error: no existe la bodega destino")), dtoUsuario);

            MovimientoResponseDTO dtoMovimiento = movimientoMapper.entidadADTO(movimiento, dtoUsuario, dtoOrigen, dtoDestino);

            UsuarioResponseDTO dtoUsuarioBodega = usuarioMapper.entidadADTO(
                    usuarioRepository.findById(producto.getBodega().getUsuario().getId())
                            .orElseThrow(() -> new RuntimeException("Error: no existe el usuario de la bodega")));

            BodegaResponseDTO dtoBodegaProducto = bodegaMapper.entidadADTO(
                    bodegaRepository.findById(producto.getBodega().getId())
                            .orElseThrow(() -> new RuntimeException("Error: no existe la bodega del producto")), dtoUsuarioBodega);

            ProductoResponseDTO dtoProducto = productoMapper.entidadADTO(producto, dtoBodegaProducto);

            return movimientoDetalleMapper.entidadADTO(dato, dtoMovimiento, dtoProducto);
        }).toList();
    }

    @Override
    public MovimientoDetalleResponseDTO buscarPorId(Long id) {
        MovimientoDetalle md = movimientoDetalleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Error: no existe dicho MovimientoDetalle"));

        Movimiento movimiento = movimientoRepository.findById(md.getMovimiento().getId())
                .orElseThrow(() -> new RuntimeException("Error: no existe el movimiento"));

        Producto producto = productoRepository.findById(md.getProducto().getId())
                .orElseThrow(() -> new RuntimeException("Error: no existe el producto"));

        UsuarioResponseDTO dtoUsuario = usuarioMapper.entidadADTO(
                usuarioRepository.findById(movimiento.getUsuario().getId())
                        .orElseThrow(() -> new RuntimeException("Error: no existe el usuario")));

        BodegaResponseDTO dtoOrigen = bodegaMapper.entidadADTO(
                bodegaRepository.findById(movimiento.getBodegaOrigen().getId())
                        .orElseThrow(() -> new RuntimeException("Error: no existe la bodega origen")), dtoUsuario);

        BodegaResponseDTO dtoDestino = bodegaMapper.entidadADTO(
                bodegaRepository.findById(movimiento.getBodegaDestino().getId())
                        .orElseThrow(() -> new RuntimeException("Error: no existe la bodega destino")), dtoUsuario);

        MovimientoResponseDTO dtoMovimiento = movimientoMapper.entidadADTO(movimiento, dtoUsuario, dtoOrigen, dtoDestino);

        UsuarioResponseDTO dtoUsuarioBodega = usuarioMapper.entidadADTO(
                usuarioRepository.findById(producto.getBodega().getUsuario().getId())
                        .orElseThrow(() -> new RuntimeException("Error: no existe el usuario de la bodega")));

        BodegaResponseDTO dtoBodegaProducto = bodegaMapper.entidadADTO(
                bodegaRepository.findById(producto.getBodega().getId())
                        .orElseThrow(() -> new RuntimeException("Error: no existe la bodega del producto")), dtoUsuarioBodega);

        ProductoResponseDTO dtoProducto = productoMapper.entidadADTO(producto, dtoBodegaProducto);

        return movimientoDetalleMapper.entidadADTO(md, dtoMovimiento, dtoProducto);
    }

    @Override
    public void eliminarMovimientoDetalle(Long id) {
        if (!movimientoDetalleRepository.existsById(id)) {
            throw new EntityNotFoundException("Error: no existe el MovimientoDetalle a eliminar");
        }
        movimientoDetalleRepository.deleteById(id);
    }
}
