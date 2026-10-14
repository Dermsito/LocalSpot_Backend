package com.example.demo.domain.exception;

public class CategoriaEnUsoException extends RuntimeException {
    public CategoriaEnUsoException(String mensaje) {
        super(mensaje);
    }
}
