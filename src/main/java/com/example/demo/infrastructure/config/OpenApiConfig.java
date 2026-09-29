package com.example.demo.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
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
    public OpenAPI openAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("LocalSpot API")
                        .version("1.0.0")
                        .description("API REST del sistema LocalSpot para gestion de usuarios, preferencias y categorias.\n\n" +
                                "## Arquitectura Hexagonal\n" +
                                "Esta API sigue el patron de Arquitectura Hexagonal (Puertos y Adaptadores):\n" +
                                "- **Dominio**: Modelos, Excepciones y Puertos (interfaces puras, sin frameworks)\n" +
                                "- **Aplicacion**: Casos de uso (logica de negocio pura)\n" +
                                "- **Infraestructura**: Adaptadores REST, Persistencia JPA y Seguridad Spring/JWT\n\n" +
                                "## Autenticacion\n" +
                                "1. Llama a **POST /api/usuarios/registro** para crear una cuenta\n" +
                                "2. Llama a **POST /api/usuarios/login** para obtener un token JWT\n" +
                                "3. Usa el boton **Authorize** (arriba derecha) o envia el header:\n" +
                                "   `Authorization: Bearer <tu-token>`\n" +
                                "4. Los endpoints de **preferencias** requieren autenticacion.")
                        .contact(new Contact()
                                .name("LocalSpot")
                                .email("soporte@localspot.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor local de desarrollo")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Token JWT obtenido al iniciar sesion. Ingresa solo el token SIN el prefijo 'Bearer '.")));
    }
}
