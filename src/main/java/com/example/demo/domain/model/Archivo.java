package com.example.demo.domain.model;

/**
 * Representacion de un archivo a nivel de dominio (sin dependencias de Spring MultipartFile).
 */
public record Archivo(String nombreOriginal, byte[] contenido, String contentType) {
}
