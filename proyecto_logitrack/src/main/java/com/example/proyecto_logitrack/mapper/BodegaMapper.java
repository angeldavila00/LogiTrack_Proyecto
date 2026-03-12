package com.example.proyecto_logitrack.mapper;

import com.example.proyecto_logitrack.dto.request.BodegaRequestDTO;
import com.example.proyecto_logitrack.dto.response.BodegaResponseDTO;
import com.example.proyecto_logitrack.dto.response.UsuarioResponseDTO;
import com.example.proyecto_logitrack.modelo.Bodega;
import com.example.proyecto_logitrack.modelo.Usuario;

public class BodegaMapper {
    /*entidadADTO*/
    public BodegaResponseDTO entidadADTO (Bodega bodega, UsuarioResponseDTO dto){
        if(bodega == null) return null;
        return new BodegaResponseDTO(
                bodega.getId(),bodega.getNombre(), bodega.getUbicacion(), bodega.getCapacidad(),dto
        );
    }

    /*DTOAentidad*/

    public Bodega DTOAentidad (BodegaRequestDTO dto, Usuario usuario){
        if(dto == null || usuario==null) return null;
        Bodega bodega = new Bodega();
        bodega.setNombre(dto.nombre());
        bodega.setUbicacion(dto.ubicacion());
        bodega.setCapacidad(dto.capacidad());
        bodega.setUsuario(usuario);
        return bodega;
    }

    /*actualizarEntidadDesdeDTO*/

    public void actualizarEntidadDesdeDTO (Bodega bodega, BodegaRequestDTO dto, Usuario usuario){
        if (dto == null || usuario == null) return;
        bodega.setNombre(dto.nombre());
        bodega.setUbicacion(dto.ubicacion());
        bodega.setCapacidad(dto.capacidad());
        bodega.setUsuario(usuario);
    }

}
