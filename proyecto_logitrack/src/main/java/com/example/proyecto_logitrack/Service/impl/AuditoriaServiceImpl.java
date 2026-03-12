package com.example.proyecto_logitrack.Service.impl;

import com.example.proyecto_logitrack.Service.AuditoriaService;
import com.example.proyecto_logitrack.dto.request.AuditoriaRequestDTO;
import com.example.proyecto_logitrack.dto.response.AuditoriaResponseDTO;
import com.example.proyecto_logitrack.dto.response.UsuarioResponseDTO;
import com.example.proyecto_logitrack.mapper.AuditoriaMapper;
import com.example.proyecto_logitrack.mapper.UsuarioMapper;
import com.example.proyecto_logitrack.modelo.Auditoria;
import com.example.proyecto_logitrack.modelo.Usuario;
import com.example.proyecto_logitrack.repository.AuditoriaRepository;
import com.example.proyecto_logitrack.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditoriaServiceImpl implements AuditoriaService {
    private final AuditoriaRepository auditoriaRepository;
    private final AuditoriaMapper auditoriaMapper;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    public AuditoriaResponseDTO crearAuditoria(AuditoriaRequestDTO dto) {
        Usuario u = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("Error: no existe el usuario"));

        Auditoria a = auditoriaMapper.DTOAentidad(dto, u);
        Auditoria a_insertada = auditoriaRepository.save(a);

        UsuarioResponseDTO dtoUsuario = usuarioMapper.entidadADTO(u);

        return auditoriaMapper.entidadADTO(a_insertada, dtoUsuario);
    }

    @Override
    public AuditoriaResponseDTO actualizarAuditoria(AuditoriaRequestDTO dto, Long id) {
        Auditoria a = auditoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Error: no existe dicha Auditoria a actualizar"));

        Usuario u = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("Error: no existe el usuario"));

        auditoriaMapper.actualizarEntidadDesdeDTO(a, dto, u);
        Auditoria a_actualizada = auditoriaRepository.save(a);

        UsuarioResponseDTO dtoUsuario = usuarioMapper.entidadADTO(u);

        return auditoriaMapper.entidadADTO(a_actualizada, dtoUsuario);
    }

    @Override
    public List<AuditoriaResponseDTO> listarAuditorias() {
        return auditoriaRepository.findAll().stream().map(dato -> {
            UsuarioResponseDTO dtoUsuario = usuarioMapper.entidadADTO(
                    usuarioRepository.findById(dato.getUsuario().getId())
                            .orElseThrow(() -> new RuntimeException("Error: no existe el usuario")));

            return auditoriaMapper.entidadADTO(dato, dtoUsuario);
        }).toList();
    }

    @Override
    public AuditoriaResponseDTO buscarPorId(Long id) {
        Auditoria a = auditoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Error: no existe dicha Auditoria"));

        UsuarioResponseDTO dtoUsuario = usuarioMapper.entidadADTO(
                usuarioRepository.findById(a.getUsuario().getId())
                        .orElseThrow(() -> new RuntimeException("Error: no existe el usuario")));

        return auditoriaMapper.entidadADTO(a, dtoUsuario);
    }

    @Override
    public void eliminarAuditoria(Long id) {
        if (!auditoriaRepository.existsById(id)) {
            throw new EntityNotFoundException("Error: no existe la Auditoria a eliminar");
        }
        auditoriaRepository.deleteById(id);
    }
}
