package com.example.proyecto_logitrack.controller;

import com.example.proyecto_logitrack.Service.BodegaService;
import com.example.proyecto_logitrack.dto.request.BodegaRequestDTO;
import com.example.proyecto_logitrack.dto.response.BodegaResponseDTO;
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

    @PostMapping
    public ResponseEntity<BodegaResponseDTO> createBodega(@Valid @RequestBody BodegaRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(bodegaService.crearBodega(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BodegaResponseDTO>actualizarBodega(@Valid @RequestBody BodegaRequestDTO dto,
                                                             @PathVariable Long id){
        return ResponseEntity.ok().body(bodegaService.actualizarBodega(dto,id));
    }

    @GetMapping
    public ResponseEntity<List<BodegaResponseDTO>> obtenerBodegas(){
        return ResponseEntity.ok().body(bodegaService.listarBodegas());
    }
    @GetMapping("/{id}")
    public ResponseEntity<BodegaResponseDTO>buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok().body(bodegaService.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarBodega(@PathVariable Long id){
        bodegaService.eliminarBodega(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
