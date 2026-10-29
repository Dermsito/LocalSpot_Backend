package com.example.demo.domain.model;

import java.util.Arrays;
import java.util.Objects;

/**
 * Representacion de un archivo a nivel de dominio (sin dependencias de Spring MultipartFile).
 */
public record Archivo(String nombreOriginal, byte[] contenido, String contentType) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Archivo archivo = (Archivo) o;
        return Objects.equals(nombreOriginal, archivo.nombreOriginal) &&
                Arrays.equals(contenido, archivo.contenido) &&
                Objects.equals(contentType, archivo.contentType);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(nombreOriginal, contentType);
        result = 31 * result + Arrays.hashCode(contenido);
        return result;
    }

    @Override
    public String toString() {
        return "Archivo{" +
                "nombreOriginal='" + nombreOriginal + '\'' +
                ", tamano=" + (contenido != null ? contenido.length : 0) +
                ", contentType='" + contentType + '\'' +
                '}';
    }
}
