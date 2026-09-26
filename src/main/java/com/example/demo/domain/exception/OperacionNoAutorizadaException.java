package com.example.demo.domain.exception;

public class OperacionNoAutorizadaException extends RuntimeException {
    public OperacionNoAutorizadaException(String mensaje) {
        super(mensaje);
    }
}
