package com.example.demo.domain.exception;

public class VerificacionInvalidaException extends RuntimeException {
    public VerificacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
