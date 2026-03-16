package com.example.proyecto_logitrack.Service.impl;

import com.example.proyecto_logitrack.Service.AuditoriaService;
import com.example.proyecto_logitrack.Service.UsuarioService;
import com.example.proyecto_logitrack.config.SecurityUtils;
import com.example.proyecto_logitrack.dto.request.BodegaRequestDTO;
import com.example.proyecto_logitrack.dto.request.UsuarioRequestDTO;
import com.example.proyecto_logitrack.dto.response.UsuarioResponseDTO;
import com.example.proyecto_logitrack.mapper.UsuarioMapper;
import com.example.proyecto_logitrack.modelo.Operacion;
import com.example.proyecto_logitrack.modelo.Usuario;
import com.example.proyecto_logitrack.repository.BodegaRepository;
import com.example.proyecto_logitrack.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final BodegaRepository bodegaRepository;
    private final AuditoriaService auditoriaService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UsuarioResponseDTO crear(UsuarioRequestDTO dto) {
        Usuario u = usuarioMapper.DTOAentidad(dto);
        Usuario u_insertada = usuarioRepository.save(u);

        usuarioRepository.findByUsername(SecurityUtils.getUsuarioActual())
                .ifPresentOrElse(
                        responsable -> auditoriaService.registrar("usuario", Operacion.INSERT, null,
                                "id=" + u_insertada.getId() + ", username=" + u_insertada.getUsername(),
                                responsable.getId(), responsable.getNombre()),
                        // registro propio desde /auth/registro (sin token)
                        () -> auditoriaService.registrar("usuario", Operacion.INSERT, null,
                                "id=" + u_insertada.getId() + ", username=" + u_insertada.getUsername(),
                                u_insertada.getId(), u_insertada.getNombre())
                );

        return usuarioMapper.entidadADTO(u_insertada);
    }

    @Override
    public List<UsuarioResponseDTO> listar() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::entidadADTO)
                .toList();
    }

    @Override
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el usuario"));
        return usuarioMapper.entidadADTO(usuario);
    }

    @Override
    public UsuarioResponseDTO actualizar(UsuarioRequestDTO dto, Long id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el usuario"));

        String valorAnterior = "nombre=" + u.getNombre() + ", username=" + u.getUsername() + ", rol=" + u.getRol();

        usuarioMapper.actualizarEntidadDesdeDTO(u, dto);
        Usuario u_actualizada = usuarioRepository.save(u);

        usuarioRepository.findByUsername(SecurityUtils.getUsuarioActual())
                .ifPresent(responsable -> auditoriaService.registrar("usuario", Operacion.UPDATE,
                        valorAnterior,
                        "nombre=" + u_actualizada.getNombre() + ", username=" + u_actualizada.getUsername() + ", rol=" + u_actualizada.getRol(),
                        responsable.getId(), responsable.getNombre()));

        return usuarioMapper.entidadADTO(u_actualizada);
    }

    @Override
    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new EntityNotFoundException("Error: no existe el Usuario a eliminar");
        }
        if (bodegaRepository.existsByUsuarioId(id)) {
            throw new RuntimeException("Error: no se puede eliminar el Usuario porque tiene Bodegas asociadas");
        }
        if (usuarioRepository.count() <= 1) {
            throw new RuntimeException("Error: no se puede eliminar el único usuario del sistema");
        }

        Usuario u = usuarioRepository.findById(id).get();
        String usernameActual = SecurityUtils.getUsuarioActual();

        if (u.getUsername().equals(usernameActual)) {
            throw new RuntimeException("Error: no puedes eliminar tu propio usuario mientras estás logueado");
        }

        String datosUsuario = "id=" + id + ", username=" + u.getUsername();

        usuarioRepository.findByUsername(usernameActual)
                .ifPresent(responsable -> {
                    if (responsable.getId().equals(id)) {
                        usuarioRepository.findAll().stream()
                                .filter(a -> a.getRol().name().equals("ADMIN") && !a.getId().equals(id))
                                .findFirst()
                                .ifPresent(otroAdmin -> auditoriaService.registrar("usuario", Operacion.DELETE,
                                        datosUsuario, null, otroAdmin.getId(), otroAdmin.getNombre()));
                    } else {
                        auditoriaService.registrar("usuario", Operacion.DELETE,
                                datosUsuario, null, responsable.getId(), responsable.getNombre());
                    }
                });

        usuarioRepository.deleteById(id);
    }
}
