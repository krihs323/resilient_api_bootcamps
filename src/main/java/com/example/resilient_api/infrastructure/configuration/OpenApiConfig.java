package com.example.resilient_api.infrastructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gestión de Capacidades - Reto Reactivo")
                        .version("1.0")
                        .description("API RESTful reactiva construida con Spring WebFlux y R2DBC."));
    }


}
