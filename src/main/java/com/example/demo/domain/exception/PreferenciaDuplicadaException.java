package com.example.demo.domain.exception;

public class PreferenciaDuplicadaException extends RuntimeException {
    public PreferenciaDuplicadaException(String mensaje) {
        super(mensaje);
    }
}
