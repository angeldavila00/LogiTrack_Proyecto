package com.example.proyecto_logitrack.controller;

import com.example.proyecto_logitrack.Service.MovimientoDetalleService;
import com.example.proyecto_logitrack.dto.request.MovimientoDetalleRequestDTO;
import com.example.proyecto_logitrack.dto.response.MovimientoDetalleResponseDTO;
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

@Tag(name = "movimiento_detalle", description = "Contiene todo lo relacionado con los Detalles de Movimiento")
@RestController
@RequestMapping("/api/movimiento_detalle")
@RequiredArgsConstructor
@Validated
public class MovimientoDetalleController {
    private final MovimientoDetalleService movimientoDetalleService;

    @Operation(
            summary = "Crear MovimientoDetalle",
            description = "Permite registrar un nuevo detalle de movimiento en el sistema"
    )
    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "MovimientoDetalle creado exitosamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o body mal estructurado",
                    content = @Content
            )
    })
    public ResponseEntity<MovimientoDetalleResponseDTO> crearMovimientoDetalle(@Valid @RequestBody MovimientoDetalleRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoDetalleService.crearMovimientoDetalle(dto));
    }

    @Operation(
            summary = "Actualizar MovimientoDetalle",
            description = "Permite actualizar la información de un detalle de movimiento existente mediante su ID"
    )
    @PutMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "MovimientoDetalle actualizado correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "MovimientoDetalle no encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos",
                    content = @Content
            )
    })
    public ResponseEntity<MovimientoDetalleResponseDTO> actualizarMovimientoDetalle(@Valid @RequestBody MovimientoDetalleRequestDTO dto,
                                                                                    @PathVariable Long id) {
        return ResponseEntity.ok().body(movimientoDetalleService.actualizarMovimientoDetalle(dto, id));
    }

    @Operation(
            summary = "Listar MovimientoDetalles",
            description = "Obtiene la lista de todos los detalles de movimiento registrados en el sistema"
    )
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de MovimientoDetalles obtenida correctamente"
            )
    })
    public ResponseEntity<List<MovimientoDetalleResponseDTO>> obtenerMovimientoDetalles() {
        return ResponseEntity.ok().body(movimientoDetalleService.listarMovimientoDetalles());
    }

    @Operation(
            summary = "Buscar MovimientoDetalle por ID",
            description = "Obtiene la información de un detalle de movimiento específico mediante su ID"
    )
    @GetMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "MovimientoDetalle encontrado"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "MovimientoDetalle no encontrado",
                    content = @Content
            )
    })
    public ResponseEntity<MovimientoDetalleResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok().body(movimientoDetalleService.buscarPorId(id));
    }

    @Operation(
            summary = "Eliminar MovimientoDetalle",
            description = "Permite eliminar un detalle de movimiento del sistema mediante su ID"
    )
    @DeleteMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "MovimientoDetalle eliminado correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "MovimientoDetalle no encontrado",
                    content = @Content
            )
    })
    public ResponseEntity<Void> eliminarMovimientoDetalle(@PathVariable Long id) {
        movimientoDetalleService.eliminarMovimientoDetalle(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
