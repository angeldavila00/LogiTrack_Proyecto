package com.example.proyecto_logitrack.Service.impl;

import com.example.proyecto_logitrack.Service.AuditoriaService;
import com.example.proyecto_logitrack.dto.request.AuditoriaRequestDTO;
import com.example.proyecto_logitrack.dto.response.AuditoriaResponseDTO;
import com.example.proyecto_logitrack.dto.response.UsuarioResponseDTO;
import com.example.proyecto_logitrack.mapper.AuditoriaMapper;
import com.example.proyecto_logitrack.mapper.UsuarioMapper;
import com.example.proyecto_logitrack.modelo.Auditoria;
import com.example.proyecto_logitrack.modelo.Operacion;
import com.example.proyecto_logitrack.modelo.Usuario;
import com.example.proyecto_logitrack.repository.AuditoriaRepository;
import com.example.proyecto_logitrack.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditoriaServiceImpl implements AuditoriaService {
    private final AuditoriaRepository auditoriaRepository;
    private final AuditoriaMapper auditoriaMapper;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;


    @Override
    public List<AuditoriaResponseDTO> listarAuditorias() {
        return auditoriaRepository.findAll().stream().map(dato -> {

            UsuarioResponseDTO dtoUsuario = null;
            if (dato.getUsuario() != null) {
                dtoUsuario = usuarioMapper.entidadADTO(
                        usuarioRepository.findById(dato.getUsuario().getId())
                                .orElse(null));
            }
            return auditoriaMapper.entidadADTO(dato, dtoUsuario);
        }).toList();
    }

    @Override
    public AuditoriaResponseDTO buscarPorId(Long id) {
        Auditoria a = auditoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Error: no existe dicha Auditoria"));


        UsuarioResponseDTO dtoUsuario = null;
        if (a.getUsuario() != null) {
            dtoUsuario = usuarioMapper.entidadADTO(
                    usuarioRepository.findById(a.getUsuario().getId())
                            .orElse(null));
        }

        return auditoriaMapper.entidadADTO(a, dtoUsuario);
    }



    @Override
    public void registrar(String entidad, Operacion operacion, String valorAnterior, String valorNuevo, Long usuarioId, String usuarioNombre) {
        Usuario u = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Error: no existe el usuario"));

        AuditoriaRequestDTO dto = new AuditoriaRequestDTO(
                entidad,
                operacion,
                LocalDateTime.now(),
                valorAnterior,
                valorNuevo,
                usuarioId
        );

        Auditoria a = auditoriaMapper.DTOAentidad(dto, u);
        a.setUsuarioNombre(usuarioNombre);
        auditoriaRepository.save(a);
    }
}
