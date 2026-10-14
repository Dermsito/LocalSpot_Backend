package com.example.demo.domain.exception;

public class EstadoReporteInvalidoException extends RuntimeException {
    public EstadoReporteInvalidoException(String mensaje) {
        super(mensaje);
    }
}
