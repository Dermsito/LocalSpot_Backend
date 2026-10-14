package com.example.demo.domain.model;

/**
 * Parametros de paginacion a nivel de dominio (sin dependencias de Spring).
 */
public record Paginacion(int page, int size) {

    public Paginacion {
        if (page < 0) page = 0;
        if (size <= 0) size = 10;
    }
}
