package com.pfe.elearning.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenConfig() {

        return new OpenAPI()
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList("bearerAuth")
                ).components(
                        new Components()
                                .addSecuritySchemes(
                                        "bearerAuth",
                                        new SecurityScheme()
                                                .name("bearerAuth")
                                                .type(Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                )
                ;
    }
}

/*
-This class is part of the Swagger (OpenAPI) configuration.
- It sets up security requirements and schemes specifically for Swagger documentation. The "bearerAuth" security scheme is configured to handle authentication with a bearer token (JWT), and it allows you to test your API in the Swagger UI with authentication.
-Swagger UI provides a user-friendly interface to explore, test, and document your RESTful APIs.

 */