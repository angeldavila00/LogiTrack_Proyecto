package com.example.proyecto_logitrack.Service.impl;

import com.example.proyecto_logitrack.Service.AuditoriaService;
import com.example.proyecto_logitrack.Service.ProductoService;
import com.example.proyecto_logitrack.config.SecurityUtils;
import com.example.proyecto_logitrack.dto.request.ProductoRequestDTO;
import com.example.proyecto_logitrack.dto.response.BodegaResponseDTO;
import com.example.proyecto_logitrack.dto.response.ProductoResponseDTO;
import com.example.proyecto_logitrack.dto.response.UsuarioResponseDTO;
import com.example.proyecto_logitrack.mapper.BodegaMapper;
import com.example.proyecto_logitrack.mapper.ProductoMapper;
import com.example.proyecto_logitrack.mapper.UsuarioMapper;
import com.example.proyecto_logitrack.modelo.Bodega;
import com.example.proyecto_logitrack.modelo.Operacion;
import com.example.proyecto_logitrack.modelo.Producto;
import com.example.proyecto_logitrack.repository.BodegaRepository;
import com.example.proyecto_logitrack.repository.MovimientoDetalleRepository;
import com.example.proyecto_logitrack.repository.ProductoRepository;
import com.example.proyecto_logitrack.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;
    private final BodegaRepository bodegaRepository;
    private final BodegaMapper bodegaMapper;
    private final UsuarioMapper usuarioMapper;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;
    private final MovimientoDetalleRepository movimientoDetalleRepository;

    @Override
    public ProductoResponseDTO crearProducto(ProductoRequestDTO dto) {
        Bodega b = bodegaRepository.findById(dto.bodegaId())
                .orElseThrow(() -> new RuntimeException("Error: no existe la bodega"));

        int stockActual = productoRepository.findByBodegaId(b.getId())
                .stream().mapToInt(Producto::getStock).sum();

        if (stockActual + dto.stock() > b.getCapacidad()) {
            throw new RuntimeException("Error: stock excede maximo de stock." +
                    "Capacidad: " + b.getCapacidad());
        }

        UsuarioResponseDTO dtoUsuario = usuarioMapper.entidadADTO(
                usuarioRepository.findById(b.getUsuario().getId())
                        .orElseThrow(() -> new RuntimeException("Error: no existe el usuario")));

        Producto p = productoMapper.DTOAentidad(dto, b);
        Producto p_insertado = productoRepository.save(p);
        BodegaResponseDTO dtoBodega = bodegaMapper.entidadADTO(b, dtoUsuario);

        usuarioRepository.findByUsername(SecurityUtils.getUsuarioActual())
                .ifPresent(responsable -> auditoriaService.registrar("producto", Operacion.INSERT, null,
                        "id=" + p_insertado.getId() + ", nombre=" + p_insertado.getNombre() +", categoria=" + p_insertado.getCategoria() +", precio="+p_insertado.getPrecio() +", stock=" + p_insertado.getStock() +b.getNombre(),
                        responsable.getId(), responsable.getNombre()));

        return productoMapper.entidadADTO(p_insertado, dtoBodega);
    }

    @Override
    public ProductoResponseDTO actualizarProducto(ProductoRequestDTO dto, Long id) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Error: no existe dicho Producto"));
        String valorAnterior = "nombre=" + p.getNombre() + ", stock=" + p.getStock();

        Bodega b = bodegaRepository.findById(dto.bodegaId())
                .orElseThrow(() -> new RuntimeException("Error: no existe la bodega"));
        UsuarioResponseDTO dtoUsuario = usuarioMapper.entidadADTO(
                usuarioRepository.findById(b.getUsuario().getId())
                        .orElseThrow(() -> new RuntimeException("Error: no existe el usuario")));

        int stockActual = productoRepository.findByBodegaId(b.getId())
                .stream().mapToInt(Producto::getStock).sum();

        if (stockActual + dto.stock() > b.getCapacidad()) {
            throw new RuntimeException("Error: stock excede maximo de stock." +
                    "Capacidad: " + b.getCapacidad());
        }

        productoMapper.actualizarEntidadDesdeDTO(p, dto, b);
        Producto p_actualizado = productoRepository.save(p);

        usuarioRepository.findByUsername(SecurityUtils.getUsuarioActual())
                .ifPresent(responsable -> auditoriaService.registrar("producto", Operacion.UPDATE,
                        valorAnterior,
                        "nombre=" + p_actualizado.getNombre() + ", stock=" + p_actualizado.getStock(),
                        responsable.getId(), responsable.getNombre()));

        BodegaResponseDTO dtoBodega = bodegaMapper.entidadADTO(b, dtoUsuario);
        return productoMapper.entidadADTO(p_actualizado, dtoBodega);
    }

    @Override
    public List<ProductoResponseDTO> listarProductos() {
        return productoRepository.findAll().stream().map(dato -> {
            UsuarioResponseDTO dtoUsuario = usuarioMapper.entidadADTO(
                    usuarioRepository.findById(dato.getBodega().getUsuario().getId())
                            .orElseThrow(() -> new RuntimeException("Error: no existe el usuario")));

            BodegaResponseDTO dtoBodega = bodegaMapper.entidadADTO(
                    bodegaRepository.findById(dato.getBodega().getId())
                            .orElseThrow(() -> new RuntimeException("Error: no existe la bodega")), dtoUsuario);

            return productoMapper.entidadADTO(dato, dtoBodega);
        }).toList();
    }

    @Override
    public ProductoResponseDTO buscarPorId(Long id) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Error: no existe dicho Producto"));

        UsuarioResponseDTO dtoUsuario = usuarioMapper.entidadADTO(
                usuarioRepository.findById(p.getBodega().getUsuario().getId())
                        .orElseThrow(() -> new RuntimeException("Error: no existe el usuario")));

        BodegaResponseDTO dtoBodega = bodegaMapper.entidadADTO(
                bodegaRepository.findById(p.getBodega().getId())
                        .orElseThrow(() -> new RuntimeException("Error: no existe la bodega")), dtoUsuario);

        return productoMapper.entidadADTO(p, dtoBodega);
    }

    @Override
    public void eliminarProducto(Long id) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Error: no existe el Producto a eliminar"));
        if (movimientoDetalleRepository.existsByProductoId(id)) {
            throw new RuntimeException("Error: no se puede eliminar el Producto porque tiene Movimientos asociados");
        }

        String valorAnterior = "id=" + id + ", nombre=" + p.getNombre() + ", stock=" + p.getStock();
        productoRepository.deleteById(id);

        usuarioRepository.findByUsername(SecurityUtils.getUsuarioActual())
                .ifPresent(responsable -> auditoriaService.registrar("producto", Operacion.DELETE,
                        valorAnterior, null, responsable.getId(), responsable.getNombre()));
    }

    @Override
    public List<ProductoResponseDTO> listarStockBajo() {
        return productoRepository.findByStockLessThan(10).stream().map(p -> {
            UsuarioResponseDTO dtoUsuario = usuarioMapper.entidadADTO(usuarioRepository.findById(p.getBodega().getUsuario().getId())
                            .orElseThrow(() -> new RuntimeException("Error: no existe el usuario")));
            BodegaResponseDTO dtoBodega = bodegaMapper.entidadADTO(p.getBodega(), dtoUsuario);
            return productoMapper.entidadADTO(p, dtoBodega);
        }).toList();
    }


}
