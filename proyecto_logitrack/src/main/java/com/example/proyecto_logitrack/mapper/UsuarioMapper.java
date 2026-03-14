package com.example.proyecto_logitrack.mapper;

import com.example.proyecto_logitrack.dto.request.UsuarioRequestDTO;
import com.example.proyecto_logitrack.dto.response.UsuarioResponseDTO;
import com.example.proyecto_logitrack.modelo.Usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    @Autowired
    private PasswordEncoder passwordEncoder;

    /*entidadADTO*/
    public UsuarioResponseDTO entidadADTO(Usuario usuario){
        if (usuario == null) return null;
        return new UsuarioResponseDTO(
                usuario.getId(),usuario.getNombre(),usuario.getDocumento(),
                usuario.getUsername(), usuario.getRol()
        );
    }
    /*DTOAentidad*/

    public Usuario DTOAentidad(UsuarioRequestDTO dto){
        if (dto == null) return null;
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.nombre());
        usuario.setDocumento(dto.documento());
        usuario.setUsername(dto.username());
        usuario.setPassword(passwordEncoder.encode(dto.password()));
        usuario.setRol(dto.rol());
        return usuario;
    }

    /*actualizarEntidadDesdeDTO*/

    public void actualizarEntidadDesdeDTO (Usuario usuario, UsuarioRequestDTO dto){
        if (usuario == null || dto == null) return;
        usuario.setNombre(dto.nombre());
        usuario.setDocumento(dto.documento());
        usuario.setUsername(dto.username());
        usuario.setPassword(passwordEncoder.encode(dto.password()));
        usuario.setRol(dto.rol());
    }


}
