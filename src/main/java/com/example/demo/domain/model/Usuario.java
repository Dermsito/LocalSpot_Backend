package com.example.demo.domain.model;

import java.time.LocalDateTime;

public class Usuario {

    private Long idUsuario;
    private String nombre;
    private String correo;
    private String contrasenaHash;
    private TipoUsuario tipoUsuario;
    private Short rolId;
    private LocalDateTime fechaRegistro;

    public enum TipoUsuario {
        FINAL, ADMINISTRADOR, PROPIETARIO;

        public static TipoUsuario fromRolId(Short rolId) {
            if (rolId == null) return FINAL;
            return switch (rolId) {
                case 1 -> ADMINISTRADOR;
                case 3 -> PROPIETARIO;
                default -> FINAL;
            };
        }

        public Short toRolId() {
            return switch (this) {
                case ADMINISTRADOR -> 1;
                case PROPIETARIO -> 3;
                default -> 2;
            };
        }
    }

    public Usuario() {
    }

    public Usuario(Long idUsuario, String nombre, String correo, String contrasenaHash,
                   TipoUsuario tipoUsuario, LocalDateTime fechaRegistro) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasenaHash = contrasenaHash;
        this.tipoUsuario = tipoUsuario;
        this.rolId = tipoUsuario == null ? 2 : tipoUsuario.toRolId();
        this.fechaRegistro = fechaRegistro;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public void setContrasenaHash(String contrasenaHash) {
        this.contrasenaHash = contrasenaHash;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
        this.rolId = tipoUsuario == null ? null : tipoUsuario.toRolId();
    }

    public Short getRolId() {
        return rolId;
    }

    public void setRolId(Short rolId) {
        this.rolId = rolId;
        this.tipoUsuario = TipoUsuario.fromRolId(rolId);
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
