package com.example.proyecto_logitrack.controller;


import com.example.proyecto_logitrack.Service.UsuarioService;
import com.example.proyecto_logitrack.dto.request.UsuarioRequestDTO;
import com.example.proyecto_logitrack.dto.response.UsuarioResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Usuario",description = "Contiene todo lo  relacionado con Usuarios")

@RestController
@RequestMapping("/api/usuario")
@RequiredArgsConstructor
@Validated
public class UsuarioController {
    private final UsuarioService usuarioService;
    @Operation(
            summary = "Crear empleado",
            description = "Permite registrar un nuevo empleado junto con la información de la persona asociada"
    )
    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Empleado creado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o estructura incorrecta",
                    content = @Content
            )
    })
    public ResponseEntity<UsuarioResponseDTO> guardarUsuario(@RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(dto));
    }

    @Operation(
            summary = "Actualizar Usuario",
            description = "Permite actualizar la información de un Usuario"
    )
    @PutMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario actualizado correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos",
                    content = @Content
            )
    })
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(@RequestBody UsuarioRequestDTO dto,
                                                                @Parameter(description = "ID del usuario a actualizar",
                                                                        example = "1") @PathVariable Long id) {
        return ResponseEntity.ok().body(usuarioService.actualizar(dto, id));
    }

    @Operation(
            summary = "Listar Usuarios",
            description = "Obtiene la lista de todos los Usuarios registrados en el sistema"
    )
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de Usuarios obtenida correctamente"
            )
    })
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerUsuarios() {
        return ResponseEntity.ok().body(usuarioService.listar());

    }

    @Operation(
            summary = "Buscar Usuarios por ID",
            description = "Obtiene la información de un Usuario específico mediante su ID"
    )
    @GetMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario encontrado"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content
            )
    })
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok().body(usuarioService.buscarPorId(id));
    }


    @Operation(
            summary = "Eliminar empleado",
            description = "Permite eliminar un Usuario del sistema mediante su ID"
    )
    @DeleteMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Usuario eliminado correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content
            )
    })
    public ResponseEntity<Void> eliminarUsuario(@Parameter(description = "ID del usuario a Eliminar",
            example = "1")@PathVariable Long id) {
        usuarioService.eliminar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
