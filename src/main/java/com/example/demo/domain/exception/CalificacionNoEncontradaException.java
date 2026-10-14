package com.example.demo.domain.exception;

public class CalificacionNoEncontradaException extends RuntimeException {
    public CalificacionNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}
