package org.arya.banking.auth.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI authServiceOpenAPI() {
        var securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Auth Service API")
                        .description("REST API for the Arya Banking Auth Service — Keycloak identity bridge, OAuth2 client credentials, JWT authentication, and user registration integration.")
                        .version("1.0.0"))
                .servers(List.of(
                        new Server().url("http://localhost:8085").description("API Gateway"),
                        new Server().url("http://localhost:8087").description("Direct")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Keycloak-issued JWT token (realm_access roles)")));
    }
}
