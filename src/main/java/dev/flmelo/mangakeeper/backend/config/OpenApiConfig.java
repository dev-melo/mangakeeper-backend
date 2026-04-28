package dev.flmelo.mangakeeper.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Mangakeeper API",
                version = "0.3.0",
                description = "API REST para gerenciamento de mangás, volumes e interações de usuários, com operações de cadastro, consulta e sistema de curtidas."
        )
)
public class OpenApiConfig {
}
