package com.example.demo.domain.exception;

public class PresupuestoInvalidoException extends RuntimeException {
    public PresupuestoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
