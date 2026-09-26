package com.example.demo.domain.exception;

public class PreferenciaNoEncontradaException extends RuntimeException {
    public PreferenciaNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}
