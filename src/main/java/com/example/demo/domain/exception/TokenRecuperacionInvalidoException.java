package com.example.demo.domain.exception;

public class TokenRecuperacionInvalidoException extends RuntimeException {

    public TokenRecuperacionInvalidoException(String mensaje) {
        super(mensaje);
    }
}
