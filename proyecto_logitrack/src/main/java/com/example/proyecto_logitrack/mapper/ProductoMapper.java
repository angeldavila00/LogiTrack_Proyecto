package com.example.proyecto_logitrack.mapper;

import com.example.proyecto_logitrack.dto.request.BodegaRequestDTO;
import com.example.proyecto_logitrack.dto.request.ProductoRequestDTO;
import com.example.proyecto_logitrack.dto.response.BodegaResponseDTO;
import com.example.proyecto_logitrack.dto.response.ProductoResponseDTO;
import com.example.proyecto_logitrack.dto.response.UsuarioResponseDTO;
import com.example.proyecto_logitrack.modelo.Bodega;
import com.example.proyecto_logitrack.modelo.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    /*entidadADTO*/
    public ProductoResponseDTO entidadADTO(Producto producto, BodegaResponseDTO dto){
        if (producto == null) return null;
        return new ProductoResponseDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getCategoria(),
                producto.getPrecio(),
                producto.getStock(),
                dto
        );
    }

    /*DTOAentidad*/

    public Producto DTOAentidad (ProductoRequestDTO dto, Bodega bodega){
        if (dto == null || bodega==null) return null;
        Producto producto = new Producto();
        producto.setNombre(dto.nombre());
        producto.setCategoria(dto.categoria());
        producto.setPrecio(dto.precio());
        producto.setStock(dto.stock());
        producto.setBodega(bodega);
        return producto;
    }
    /*actualizarEntidadDesdeDTO*/

    public void actualizarEntidadDesdeDTO(Producto producto, ProductoRequestDTO dto, Bodega bodega){
        if (dto== null || producto == null) return;
        producto.setNombre(dto.nombre());
        producto.setCategoria(dto.categoria());
        producto.setPrecio(dto.precio());
        producto.setStock(dto.stock());
        producto.setBodega(bodega);

    }
}
