package com.backend.trego.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Trego API - Restaurantes y Usuarios")
                        .version("1.0.0")
                        .description(
                                "Documentación de los endpoints del backend de Trego, incluyendo el flujo crítico de registro de restaurantes CU-RES-01.")
                        .contact(new Contact()
                                .name("Soporte Trego")
                                .email("soporte@trego.com")));
    }
}