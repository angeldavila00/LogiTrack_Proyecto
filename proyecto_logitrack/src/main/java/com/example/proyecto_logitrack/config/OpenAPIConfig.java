package com.example.proyecto_logitrack.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gestion De LogiTrack")
                        .version("1.0")
                        .description("Gestión de datos de bodega: Se dispone de tablas generales para la manipulación de información. Queda estrictamente prohibido el uso  \n" +
                                "de herramientas de prueba externas por vulnerabilidades de seguridad. Todo acceso, consulta o prueba de integridad sobre los datos debe validarse mediante el login oficial del sistema.")
                );
    }
}
