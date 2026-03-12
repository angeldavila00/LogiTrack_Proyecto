package com.example.proyecto_logitrack.Service;

import com.example.proyecto_logitrack.dto.request.ProductoRequestDTO;
import com.example.proyecto_logitrack.dto.response.ProductoResponseDTO;

import java.util.List;

public interface ProductoService {
    ProductoResponseDTO crearProducto(ProductoRequestDTO dto);
    ProductoResponseDTO actualizarProducto(ProductoRequestDTO dto, Long id);
    List<ProductoResponseDTO> listarProductos();
    ProductoResponseDTO buscarPorId(Long id);
    void eliminarProducto(Long id);
}
