package com.example.proyecto_logitrack.controller;


import com.example.proyecto_logitrack.Service.ReporteService;
import com.example.proyecto_logitrack.dto.response.ReporteResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
    @RequestMapping("/reportes")
    public class ReporteController {

        private final ReporteService reporteService;

        public ReporteController(ReporteService reporteService) {
            this.reporteService = reporteService;
        }
        @GetMapping("/movimientos")
        public ResponseEntity<ReporteResponseDTO> reporteMovimientos() {
            ReporteResponseDTO reporte = reporteService.reporteDTO();
            return ResponseEntity.ok(reporte);
        }
    }

