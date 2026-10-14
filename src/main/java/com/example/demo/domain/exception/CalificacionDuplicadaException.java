package com.example.demo.domain.exception;

public class CalificacionDuplicadaException extends RuntimeException {
    public CalificacionDuplicadaException(String mensaje) {
        super(mensaje);
    }
}
