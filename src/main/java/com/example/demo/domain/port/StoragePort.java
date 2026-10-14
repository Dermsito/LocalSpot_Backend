package com.example.demo.domain.port;

import com.example.demo.domain.model.Archivo;

/**
 * Puerto para almacenar archivos (fotos de lugares).
 * El adaptador concreto puede ser filesystem, S3, etc.
 */
public interface StoragePort {

    /**
     * Almacena un archivo y devuelve su URL de acceso.
     *
     * @param archivo archivo a almacenar
     * @return URL de acceso al archivo
     */
    String store(Archivo archivo);

    /**
     * Elimina un archivo por su URL.
     *
     * @param url URL del archivo a eliminar
     */
    void delete(String url);
}
