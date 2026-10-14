package com.example.demo.domain.exception;

public class ReporteNoEncontradoException extends RuntimeException {
    public ReporteNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
