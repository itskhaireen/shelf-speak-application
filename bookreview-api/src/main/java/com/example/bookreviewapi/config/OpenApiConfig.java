package com.example.bookreviewapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ShelfSpeak Book Review API")
                        .version("1.2.0")
                        .description("A comprehensive REST API for managing books and reviews with full CRUD operations, JWT authentication, and role-based access control. **Dev Environment:** Auto-creates admin user (admin/admin123) for testing.")
                        .contact(new Contact()
                                .name("ShelfSpeak API Team")
                                .email("contact@bookreviewapi.com")
                                .url("https://github.com/itskhaireen/bookreviews-api")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Development server"),
                        new Server().url("https://api.bookreview.com").description("Production server")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token obtained from /auth/login endpoint")));
    }
} 