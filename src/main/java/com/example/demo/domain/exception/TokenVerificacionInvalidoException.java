package com.example.demo.domain.exception;

/**
 * El token de verificacion de correo no existe, ya fue usado o expiro.
 */
public class TokenVerificacionInvalidoException extends RuntimeException {
    public TokenVerificacionInvalidoException(String mensaje) {
        super(mensaje);
    }
}
