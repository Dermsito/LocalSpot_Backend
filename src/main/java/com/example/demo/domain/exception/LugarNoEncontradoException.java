package com.example.demo.domain.exception;

public class LugarNoEncontradoException extends RuntimeException {
    public LugarNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
