package com.example.demo.domain.model;

import java.util.List;

/**
 * Resultado paginado a nivel de dominio (sin dependencias de Spring).
 */
public record Pagina<T>(List<T> content, int page, int size, long totalElements, int totalPages) {

    public Pagina {
        content = content == null ? List.of() : List.copyOf(content);
        totalPages = (int) Math.ceil((double) totalElements / size);
        if (totalPages < 0) totalPages = 0;
    }
}
