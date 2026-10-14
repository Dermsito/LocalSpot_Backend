package com.example.demo.infrastructure.adapter.out.storage;

import com.example.demo.domain.model.Archivo;
import com.example.demo.domain.port.StoragePort;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Adaptador de almacenamiento en sistema de archivos.
 * Guarda las fotos en el directorio configurado (UPLOAD_DIR) y devuelve
 * una URL relativa que se sirve mediante el handler de recursos estaticos.
 */
@Component
public class FileSystemStorageAdapter implements StoragePort {

    private static final Logger log = LoggerFactory.getLogger(FileSystemStorageAdapter.class);

    private final Path uploadDir;

    public FileSystemStorageAdapter(@Value("${localspot.storage.upload-dir:./uploads}") String uploadDir) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(uploadDir);
            log.info("Directorio de uploads inicializado en: {}", uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el directorio de uploads: " + uploadDir, e);
        }
    }

    @Override
    public String store(Archivo archivo) {
        String extension = obtenerExtension(archivo.nombreOriginal());
        String nombreArchivo = UUID.randomUUID() + extension;
        Path destino = uploadDir.resolve(nombreArchivo);

        try {
            Files.copy(new java.io.ByteArrayInputStream(archivo.contenido()), destino,
                    StandardCopyOption.REPLACE_EXISTING);
            // URL relativa que se servira desde /uploads/**
            return "/uploads/" + nombreArchivo;
        } catch (IOException e) {
            throw new RuntimeException("Error al almacenar el archivo: " + archivo.nombreOriginal(), e);
        }
    }

    @Override
    public void delete(String url) {
        if (url == null || !url.startsWith("/uploads/")) {
            return;
        }
        String nombreArchivo = url.substring("/uploads/".length());
        Path archivo = uploadDir.resolve(nombreArchivo);
        try {
            Files.deleteIfExists(archivo);
        } catch (IOException e) {
            log.warn("No se pudo eliminar el archivo {}: {}", url, e.getMessage());
        }
    }

    private String obtenerExtension(String nombreOriginal) {
        if (nombreOriginal == null) {
            return "";
        }
        int punto = nombreOriginal.lastIndexOf('.');
        if (punto >= 0) {
            return nombreOriginal.substring(punto);
        }
        return "";
    }
}
