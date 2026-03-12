package com.example.proyecto_logitrack.mapper;

import com.example.proyecto_logitrack.dto.request.MovimientoRequestDTO;
import com.example.proyecto_logitrack.dto.response.BodegaResponseDTO;
import com.example.proyecto_logitrack.dto.response.MovimientoResponseDTO;
import com.example.proyecto_logitrack.dto.response.UsuarioResponseDTO;
import com.example.proyecto_logitrack.modelo.Bodega;
import com.example.proyecto_logitrack.modelo.Movimiento;
import com.example.proyecto_logitrack.modelo.Usuario;

public class MovimientoMapper {

    /*entidadADTO*/

    public MovimientoResponseDTO entidadADTO(Movimiento movimiento,
                                             UsuarioResponseDTO dto,
                                             BodegaResponseDTO dtoBodegaOrigen,
                                             BodegaResponseDTO dtoBodegaDestino) {
        if (movimiento == null) return null;
        return new MovimientoResponseDTO(
                movimiento.getId(),
                movimiento.getFecha(),
                movimiento.getTipomovimiento(),
                dto,
                dtoBodegaOrigen,
                dtoBodegaDestino
        );
    }

    /*DTOAentidad*/

    public Movimiento DTOAentidad (MovimientoRequestDTO dto, Usuario usuario, Bodega bodegaOrigen, Bodega bodegaDestino) {
        if(dto == null || usuario == null || bodegaDestino==null || bodegaOrigen== null) return null;
        Movimiento movimiento = new Movimiento();
        movimiento.setFecha(dto.fecha());
        movimiento.setTipomovimiento(dto.tipoMovimiento());
        movimiento.setUsuario(usuario);
        movimiento.setBodegaOrigen(bodegaOrigen);
        movimiento.setBodegaDestino(bodegaDestino);
        return movimiento;
    }

    /*actualizarEntidadDesdeDTO*/

    public void actualizarEntidadDesdeDTO (Movimiento movimiento, MovimientoRequestDTO dto, Usuario usuario, Bodega bodegaOrigen,
                                           Bodega bodegaDestino, BodegaResponseDTO dtoBodegaOrigen) {
        if(dto == null || usuario == null || bodegaDestino==null || bodegaOrigen== null) return;
        movimiento.setFecha(dto.fecha());
        movimiento.setTipomovimiento(dto.tipoMovimiento());
        movimiento.setUsuario(usuario);
        movimiento.setBodegaOrigen(bodegaOrigen);
        movimiento.setBodegaDestino(bodegaDestino);
    }
}
