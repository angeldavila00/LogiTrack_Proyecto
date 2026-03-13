package com.example.proyecto_logitrack.Service.impl;

import com.example.proyecto_logitrack.Service.ProductoService;
import com.example.proyecto_logitrack.dto.request.ProductoRequestDTO;
import com.example.proyecto_logitrack.dto.response.BodegaResponseDTO;
import com.example.proyecto_logitrack.dto.response.ProductoResponseDTO;
import com.example.proyecto_logitrack.dto.response.UsuarioResponseDTO;
import com.example.proyecto_logitrack.mapper.BodegaMapper;
import com.example.proyecto_logitrack.mapper.ProductoMapper;
import com.example.proyecto_logitrack.mapper.UsuarioMapper;
import com.example.proyecto_logitrack.modelo.Bodega;
import com.example.proyecto_logitrack.modelo.Producto;
import com.example.proyecto_logitrack.repository.BodegaRepository;
import com.example.proyecto_logitrack.repository.ProductoRepository;
import com.example.proyecto_logitrack.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;
    private final BodegaRepository bodegaRepository;
    private final BodegaMapper bodegaMapper;
    private final UsuarioMapper usuarioMapper;
    private final UsuarioRepository usuarioRepository;
    @Override
    public ProductoResponseDTO crearProducto(ProductoRequestDTO dto) {
        Bodega b = bodegaRepository.findById(dto.bodegaId())
                .orElseThrow(() -> new RuntimeException("Error: no existe la bodega"));

        UsuarioResponseDTO dtoUsuario = usuarioMapper.entidadADTO(
                usuarioRepository.findById(b.getUsuario().getId())
                        .orElseThrow(() -> new RuntimeException("Error: no existe el usuario")));

        Producto p = productoMapper.DTOAentidad(dto, b);
        Producto p_insertado = productoRepository.save(p);

        BodegaResponseDTO dtoBodega = bodegaMapper.entidadADTO(b, dtoUsuario);

        return productoMapper.entidadADTO(p_insertado, dtoBodega);
    }

    @Override
    public ProductoResponseDTO actualizarProducto(ProductoRequestDTO dto, Long id) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Error: no existe dicho Producto a actualizar"));

        Bodega b = bodegaRepository.findById(dto.bodegaId())
                .orElseThrow(() -> new RuntimeException("Error: no existe la bodega"));

        UsuarioResponseDTO dtoUsuario = usuarioMapper.entidadADTO(
                usuarioRepository.findById(b.getUsuario().getId())
                        .orElseThrow(() -> new RuntimeException("Error: no existe el usuario")));

        productoMapper.actualizarEntidadDesdeDTO(p, dto, b);
        Producto p_actualizado = productoRepository.save(p);

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
                        .orElseThrow(() -> new RuntimeException("Error: no existe la bodega")),
                dtoUsuario);

        return productoMapper.entidadADTO(p, dtoBodega);
    }

    @Override
    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new EntityNotFoundException("Error: no existe el Producto a eliminar");
        }
        productoRepository.deleteById(id);
    }
}
