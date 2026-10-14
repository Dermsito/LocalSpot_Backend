package com.example.demo.integration;

import com.example.demo.domain.model.Categoria;
import com.example.demo.domain.port.CategoriaRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de integracion con PostgreSQL via Testcontainers.
 * Verifica que el contexto arranca correctamente y que los repositorios funcionan.
 */
@SpringBootTest
@Testcontainers
class PostgresIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("localspot_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("jwt.secret", () -> "test-secret-key-very-long-12345678901234567890123456789012");
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    private CategoriaRepositoryPort categoriaRepositoryPort;

    @Test
    void contextLoads() {
        assertThat(categoriaRepositoryPort).isNotNull();
    }

    @Test
    void findById_categoriaExistente_debeRetornarCategoria() {
        // Las categorias son sembradas por Flyway (V1 o V6)
        Optional<Categoria> categoria = categoriaRepositoryPort.findById(1L);
        assertThat(categoria).isPresent();
    }
}
