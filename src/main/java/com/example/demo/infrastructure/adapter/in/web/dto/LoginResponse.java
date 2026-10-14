package com.example.demo.infrastructure.adapter.in.web.dto;

public class LoginResponse {

    private String token;
    private String tipoToken = "Bearer";
    private Long expiracionMs;
    private Long id;
    private String nombre;
    private String rol;

    public LoginResponse() {
    }

    public LoginResponse(String token, Long expiracionMs, Long id, String nombre, String rol) {
        this.token = token;
        this.expiracionMs = expiracionMs;
        this.id = id;
        this.nombre = nombre;
        this.rol = rol;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipoToken() {
        return tipoToken;
    }

    public void setTipoToken(String tipoToken) {
        this.tipoToken = tipoToken;
    }

    public Long getExpiracionMs() {
        return expiracionMs;
    }

    public void setExpiracionMs(Long expiracionMs) {
        this.expiracionMs = expiracionMs;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
