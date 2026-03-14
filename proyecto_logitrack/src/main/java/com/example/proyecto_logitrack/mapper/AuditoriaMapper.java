package com.example.proyecto_logitrack.mapper;

import com.example.proyecto_logitrack.dto.request.AuditoriaRequestDTO;
import com.example.proyecto_logitrack.dto.response.AuditoriaResponseDTO;
import com.example.proyecto_logitrack.dto.response.UsuarioResponseDTO;
import com.example.proyecto_logitrack.modelo.Auditoria;
import com.example.proyecto_logitrack.modelo.Usuario;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaMapper {

    /*entidadADTO*/

    public AuditoriaResponseDTO entidadADTO (Auditoria auditoria, UsuarioResponseDTO dto){
        if (auditoria == null) return null;
        return new AuditoriaResponseDTO(
                auditoria.getId(),auditoria.getEntidad(),
                auditoria.getOperacion(),
                auditoria.getFecha(), auditoria.getValorAnterior(),
                auditoria.getValorNuevo(),dto
        );
    }

    /*DTOAentidad*/

    public Auditoria DTOAentidad (AuditoriaRequestDTO dto, Usuario  usuario){
        if(dto == null || usuario == null) return null;
        Auditoria auditoria = new Auditoria();
        auditoria.setEntidad(dto.entidad());
        auditoria.setOperacion(dto.operacion());
        auditoria.setFecha(dto.fecha());
        auditoria.setValorAnterior(dto.valorAnterior());
        auditoria.setValorNuevo(dto.valorNuevo());
        auditoria.setUsuario(usuario);
        return auditoria;
    }

    /*actualizarEntidadDesdeDTO*/

    public void actualizarEntidadDesdeDTO (Auditoria auditoria, AuditoriaRequestDTO dto, Usuario usuario){
        if(dto == null || usuario == null) return;
        auditoria.setEntidad(dto.entidad());
        auditoria.setOperacion(dto.operacion());
        auditoria.setFecha(dto.fecha());
        auditoria.setValorAnterior(dto.valorAnterior());
        auditoria.setValorNuevo(dto.valorNuevo());
        auditoria.setUsuario(usuario);
    }
}
