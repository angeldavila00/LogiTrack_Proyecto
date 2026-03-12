package com.example.proyecto_logitrack.mapper;

import com.example.proyecto_logitrack.dto.request.MovimientoDetalleRequestDTO;
import com.example.proyecto_logitrack.dto.response.MovimientoDetalleResponseDTO;
import com.example.proyecto_logitrack.dto.response.MovimientoResponseDTO;
import com.example.proyecto_logitrack.dto.response.ProductoResponseDTO;
import com.example.proyecto_logitrack.modelo.Movimiento;
import com.example.proyecto_logitrack.modelo.MovimientoDetalle;
import com.example.proyecto_logitrack.modelo.Producto;
import org.springframework.stereotype.Component;

@Component
public class MovimientoDetalleMapper {

    /*entidadADTO*/

    public MovimientoDetalleResponseDTO entidadADTO(MovimientoDetalle movimientoDetalle,
                                                    MovimientoResponseDTO dto,
                                                    ProductoResponseDTO dtoP
                                                    ){
        if (movimientoDetalle == null) return null;
        return  new MovimientoDetalleResponseDTO(
                movimientoDetalle.getId(),
                movimientoDetalle.getCantidad(),
                dto,dtoP
        );
    }

    /*DTOAentidad*/

    public MovimientoDetalle DTOAentidad (MovimientoDetalleRequestDTO dto,
                                          Movimiento movimiento,
                                          Producto producto){
        if(dto == null || movimiento==null || producto==null) return null;
        MovimientoDetalle movimientodetalle= new MovimientoDetalle();
        movimientodetalle.setCantidad(dto.cantidad());
        movimientodetalle.setMovimiento(movimiento);
        movimientodetalle.setProducto(producto);
        return movimientodetalle;
    }

    /*actualizarEntidadDesdeDTO*/

    public void actualizarEntidadDesdeDTO (MovimientoDetalle movimientoDetalle,
                                           MovimientoDetalleRequestDTO dto,
                                           Producto producto,
                                           Movimiento  movimiento){
        if(dto == null || movimiento==null|| movimientoDetalle==null || producto==null) return;
        movimientoDetalle.setCantidad(dto.cantidad());
        movimientoDetalle.setMovimiento(movimiento);
        movimientoDetalle.setProducto(producto);
    }

}
