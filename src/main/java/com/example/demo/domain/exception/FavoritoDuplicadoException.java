package com.example.demo.domain.exception;

public class FavoritoDuplicadoException extends RuntimeException {
    public FavoritoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
