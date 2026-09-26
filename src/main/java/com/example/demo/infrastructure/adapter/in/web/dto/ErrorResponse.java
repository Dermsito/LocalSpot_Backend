package com.example.demo.infrastructure.adapter.in.web.dto;

public class ErrorResponse {

    private int codigo;
    private String error;
    private String mensaje;
    private String timestamp;

    public ErrorResponse() {
    }

    public ErrorResponse(int codigo, String error, String mensaje, String timestamp) {
        this.codigo = codigo;
        this.error = error;
        this.mensaje = mensaje;
        this.timestamp = timestamp;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
