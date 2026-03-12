package com.example.proyecto_logitrack.controller;

import com.example.proyecto_logitrack.Service.MovimientoService;
import com.example.proyecto_logitrack.dto.request.MovimientoRequestDTO;
import com.example.proyecto_logitrack.dto.response.MovimientoResponseDTO;
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

@Tag(name = "Movimiento", description = "Contiene todo lo relacionado con los Movimientos")
@RestController
@RequestMapping("/api/movimiento")
@RequiredArgsConstructor
@Validated
public class MovimientoController {
    private final MovimientoService movimientoService;

    @Operation(
            summary = "Crea Movimiento",
            description = "Permite registrar un nuevo movimiento en el sistema"
    )
    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Movimiento creado exitosamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o body mal estructurado",
                    content = @Content
            )
    })
    public ResponseEntity<MovimientoResponseDTO> crearMovimiento(@Valid @RequestBody MovimientoRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoService.crearMovimiento(dto));
    }

    @Operation(
            summary = "Actualizar Movimiento",
            description = "Permite actualizar la información de una bodega existente mediante su ID"
    )
    @PutMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Movimiento actualizada correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Movimiento no encontrada",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos",
                    content = @Content
            )
    })
    public ResponseEntity<MovimientoResponseDTO>actualizarMovimiento(@Valid @RequestBody MovimientoRequestDTO dto,
                                                                     @PathVariable Long id){
        return ResponseEntity.ok().body(movimientoService.actulizarMovimiento(dto, id));
    }

    @Operation(
            summary = "Listar Movimiento",
            description = "Obtiene la lista de todos los Movimiento registrados en el sistema"
    )
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de Movimientos obtenida correctamente"
            )
    })
    public ResponseEntity<List<MovimientoResponseDTO>> obtenerMovimientos(){
        return ResponseEntity.ok().body(movimientoService.listarMovimientos());
    }

    @Operation(
            summary = "Buscar Movimientos por ID",
            description = "Obtiene la información del movimiento específica mediante su ID"
    )
    @GetMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Movimiento encontrada"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Movimiento no encontrada",
                    content = @Content

            )
    })
    public ResponseEntity<MovimientoResponseDTO>buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok().body(movimientoService.buscarPorId(id));
    }

    @Operation(
            summary = "Eliminar Movimiento",
            description = "Permite eliminar un Movimiento del sistema mediante su ID"
    )
    @DeleteMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Movimiento eliminada correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Movimiento no encontrada",
                    content = @Content
            )
    })
    public ResponseEntity<Void> eliminarMovimiento(@PathVariable Long id){
        movimientoService.eliminarMovimiento(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
