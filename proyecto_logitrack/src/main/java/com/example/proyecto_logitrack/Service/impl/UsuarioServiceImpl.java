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

        // Al crear, el responsable es el logueado (admin) o el mismo usuario
        // recién creado (registro propio desde /auth/registro)
        Long responsableId = usuarioRepository.findByUsername(SecurityUtils.getUsuarioActual())
                .map(Usuario::getId)
                .orElse(u_insertada.getId());

        auditoriaService.registrar("usuario", Operacion.INSERT, null,
                "id=" + u_insertada.getId() + ", username=" + u_insertada.getUsername(),
                responsableId);

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

        // Capturar ANTES de modificar
        String valorAnterior = "nombre=" + u.getNombre() + ", username=" + u.getUsername() + ", rol=" + u.getRol();

        usuarioMapper.actualizarEntidadDesdeDTO(u, dto);
        Usuario u_actualizada = usuarioRepository.save(u);

        // ✅ auditoría con el usuario logueado
        usuarioRepository.findByUsername(SecurityUtils.getUsuarioActual())
                .ifPresent(responsable -> auditoriaService.registrar("usuario", Operacion.UPDATE,
                        valorAnterior,
                        "nombre=" + u_actualizada.getNombre() + ", username=" + u_actualizada.getUsername() + ", rol=" + u_actualizada.getRol(),
                        responsable.getId()));

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

        Usuario u = usuarioRepository.findById(id).get();
        String datosUsuario = "id=" + id + ", username=" + u.getUsername();

        // buscar otro admin como responsable
        String usernameActual = SecurityUtils.getUsuarioActual();
        Long responsableId = usuarioRepository.findByUsername(usernameActual)
                .map(responsable -> {
                    //Si el logueado es el mismo busca a otro admin
                    if (responsable.getId().equals(id)) {
                        return usuarioRepository.findAll().stream()
                                .filter(a -> a.getRol().name().equals("ADMIN") && a.getId().equals(id))
                                .findFirst()
                                .map(Usuario::getId).orElse(null);
                    }
                    return responsable.getId();

                })
                .orElse(null);

        // Registrar ANTES del delete — ON DELETE SET NULL se encarga del resto
        if (responsableId != null) {
            Long finalResponsableId = responsableId;
            auditoriaService.registrar("usuario", Operacion.DELETE, datosUsuario, null, finalResponsableId);
        }

        usuarioRepository.deleteById(id);
    }
}
