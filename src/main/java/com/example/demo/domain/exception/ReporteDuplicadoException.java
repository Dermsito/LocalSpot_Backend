package com.example.demo.domain.exception;

public class ReporteDuplicadoException extends RuntimeException {
    public ReporteDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
