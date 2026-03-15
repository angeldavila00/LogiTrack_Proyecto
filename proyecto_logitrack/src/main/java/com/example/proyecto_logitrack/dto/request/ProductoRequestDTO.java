package com.example.proyecto_logitrack.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record ProductoRequestDTO(
        @Schema(description = "Nombre del producto", example = "Laptop Lenovo")
        @NotBlank(message = "no puede estar vacio.")
        String nombre,

        @Schema(description = "Categoría del producto", example = "Tecnologia")
        @NotBlank(message = "no puede estar vacio.")
        String categoria,

        @DecimalMin(value = "100", message = "El precio mínimo es 100.")        @Schema(description = "Precio del producto", example = "2500.00")
        @NotNull(message = "no puede estar vacio.")
        @PositiveOrZero(message = "El Precio no puede ser negativo.")
        double precio,

        @Schema(description = "Cantidad en stock", example = "10")
        @NotNull(message = "no puede estar vacio.")
        @PositiveOrZero(message = "El stock no puede ser negativo.")
        Integer stock,

        @Schema(description = "ID de la bodega donde se almacena", example = "1")
        @NotNull(message = "La bodega es obligatoria. (Si no existen bodegas crearla)")
        Long bodegaId
) {
}
