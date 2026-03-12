package com.example.proyecto_logitrack.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProductoRequestDTO(
        @Schema(description = "Nombre del producto", example = "Laptop Lenovo")
        String nombre,

        @Schema(description = "Categoría del producto", example = "Tecnologia")
        String categoria,

        @Schema(description = "Precio del producto", example = "2500.00")
        double precio,

        @Schema(description = "Cantidad en stock", example = "10")
        Integer stock,

        @Schema(description = "ID de la bodega donde se almacena", example = "1")
        Long bodegaId
) {
}
