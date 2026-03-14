package com.example.proyecto_logitrack.controller;

import com.example.proyecto_logitrack.Service.BodegaService;
import com.example.proyecto_logitrack.dto.request.BodegaRequestDTO;
import com.example.proyecto_logitrack.dto.response.BodegaResponseDTO;
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

@Tag(name = "Bodega",description = "Contiene todo lo relacionado con las bodegas")

@RestController
@RequestMapping("/api/bodega")
@RequiredArgsConstructor
@Validated
public class BodegaController {
    private final BodegaService bodegaService;
    @Operation(
            summary = "Crear bodega",
            description = "Permite registrar una nueva bodega en el sistema"
    )
    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Bodega creada exitosamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o body mal estructurado",
                    content = @Content
            )
    })
    public ResponseEntity<BodegaResponseDTO> createBodega(@Valid @RequestBody BodegaRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(bodegaService.crearBodega(dto));
    }

    @Operation(
            summary = "Actualizar bodega",
            description = "Permite actualizar la información de una bodega existente mediante su ID"
    )
    @PutMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Bodega actualizada correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Bodega no encontrada",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos",
                    content = @Content
            )
    })
    public ResponseEntity<BodegaResponseDTO>actualizarBodega(@Valid @RequestBody BodegaRequestDTO dto,
                                                             @PathVariable Long id){
        return ResponseEntity.ok().body(bodegaService.actualizarBodega(dto,id));
    }
    @Operation(
            summary = "Listar bodegas",
            description = "Obtiene la lista de todas las bodegas registradas en el sistema"
    )
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de bodegas obtenida correctamente"
            )
    })
    public ResponseEntity<List<BodegaResponseDTO>> obtenerBodegas(){
        return ResponseEntity.ok().body(bodegaService.listarBodegas());
    }

    @Operation(
            summary = "Buscar bodega por ID",
            description = "Obtiene la información de una bodega específica mediante su ID"
    )
    @GetMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Bodega encontrada"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Bodega no encontrada",
                    content = @Content

            )
    })
    public ResponseEntity<BodegaResponseDTO>buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok().body(bodegaService.buscarPorId(id));
    }

    @Operation(
            summary = "Eliminar bodega",
            description = "Permite eliminar una bodega del sistema mediante su ID"
    )
    @DeleteMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Bodega eliminada correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Bodega no encontrada",
                    content = @Content
            )
    })

    public ResponseEntity<Void> eliminarBodega(@PathVariable Long id){
        bodegaService.eliminarBodega(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
