package com.example.demo.domain.exception;

/**
 * El usuario existe y sus credenciales son validas, pero aun no ha verificado su correo.
 */
public class CorreoNoVerificadoException extends RuntimeException {
    public CorreoNoVerificadoException(String mensaje) {
        super(mensaje);
    }
}
