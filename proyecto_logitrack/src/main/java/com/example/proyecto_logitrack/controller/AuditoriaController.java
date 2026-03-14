package com.example.proyecto_logitrack.controller;

import com.example.proyecto_logitrack.Service.AuditoriaService;
import com.example.proyecto_logitrack.dto.request.AuditoriaRequestDTO;
import com.example.proyecto_logitrack.dto.response.AuditoriaResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
@RequiredArgsConstructor
@Validated
public class AuditoriaController {
    private final AuditoriaService auditoriaService;

//    @Operation(summary = "Crear Auditoria", description = "Permite registrar un nuevo registro de auditoría en el sistema")
//    @PostMapping
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "201", description = "Auditoria creada exitosamente"),
//            @ApiResponse(responseCode = "400", description = "Datos inválidos o body mal estructurado", content = @Content)
//    })
//    public ResponseEntity<AuditoriaResponseDTO> crearAuditoria(@Valid @RequestBody AuditoriaRequestDTO dto) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(auditoriaService.crearAuditoria(dto));
//    }

//    @Operation(summary = "Actualizar Auditoria", description = "Permite actualizar un registro de auditoría existente mediante su ID")
//    @PutMapping("/{id}")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Auditoria actualizada correctamente"),
//            @ApiResponse(responseCode = "404", description = "Auditoria no encontrada", content = @Content),
//            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
//    })
//    public ResponseEntity<AuditoriaResponseDTO> actualizarAuditoria(@Valid @RequestBody AuditoriaRequestDTO dto,
//                                                                    @PathVariable Long id) {
//        return ResponseEntity.ok().body(auditoriaService.actualizarAuditoria(dto, id));
//    }

    @Operation(summary = "Listar Auditorias", description = "Obtiene la lista de todos los registros de auditoría del sistema")
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de Auditorias obtenida correctamente")
    })
    public ResponseEntity<List<AuditoriaResponseDTO>> obtenerAuditorias() {
        return ResponseEntity.ok().body(auditoriaService.listarAuditorias());
    }

    @Operation(summary = "Buscar Auditoria por ID", description = "Obtiene la información de un registro de auditoría específico mediante su ID")
    @GetMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Auditoria encontrada"),
            @ApiResponse(responseCode = "404", description = "Auditoria no encontrada", content = @Content)
    })
    public ResponseEntity<AuditoriaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok().body(auditoriaService.buscarPorId(id));
    }

//    @Operation(summary = "Eliminar Auditoria", description = "Permite eliminar un registro de auditoría del sistema mediante su ID")
//    @DeleteMapping("/{id}")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "204", description = "Auditoria eliminada correctamente"),
//            @ApiResponse(responseCode = "404", description = "Auditoria no encontrada", content = @Content)
//    })
//    public ResponseEntity<Void> eliminarAuditoria(@PathVariable Long id) {
//        auditoriaService.eliminarAuditoria(id);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
}