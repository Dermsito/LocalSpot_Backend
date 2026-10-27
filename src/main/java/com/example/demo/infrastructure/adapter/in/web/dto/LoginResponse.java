package com.example.demo.infrastructure.adapter.in.web.dto;

public class LoginResponse {

    private String token;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UsuarioInfo usuario;

    public LoginResponse() {
    }

    public LoginResponse(String token, Long expiresIn, Long id, String nombre, String rol) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.usuario = new UsuarioInfo(id, nombre, rol);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UsuarioInfo getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioInfo usuario) {
        this.usuario = usuario;
    }

    public static class UsuarioInfo {
        private Long id;
        private String nombre;
        private String rol;

        public UsuarioInfo() {
        }

        public UsuarioInfo(Long id, String nombre, String rol) {
            this.id = id;
            this.nombre = nombre;
            this.rol = rol;
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
}
