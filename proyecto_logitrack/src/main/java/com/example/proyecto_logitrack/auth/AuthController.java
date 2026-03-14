package com.example.proyecto_logitrack.auth;


import com.example.proyecto_logitrack.Service.UsuarioService;
import com.example.proyecto_logitrack.config.JwtService;
import com.example.proyecto_logitrack.dto.request.RegistroRequest;
import com.example.proyecto_logitrack.dto.request.UsuarioRequestDTO;
import com.example.proyecto_logitrack.exception.BusinessRuleException;
import com.example.proyecto_logitrack.modelo.Usuario;
import com.example.proyecto_logitrack.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {
        System.out.println("LOGIN REQUEST: " + request.username());

        Usuario usuario = usuarioRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessRuleException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new BusinessRuleException("Credenciales inválidas");
        }

        String token = jwtService.generateToken(usuario.getUsername());
        return Map.of("token", token);
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody RegistroRequest request) {
        usuarioService.crear(new UsuarioRequestDTO(
                request.nombre(),
                request.documento(),
                request.username(),
                request.password(),
                request.rol()
        ));
        return ResponseEntity.ok(Map.of("mensaje", "Usuario creado correctamente"));
    }


}
