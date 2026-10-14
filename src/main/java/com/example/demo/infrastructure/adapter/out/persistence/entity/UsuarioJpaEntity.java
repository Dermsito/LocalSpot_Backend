package com.example.demo.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class UsuarioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "correo", nullable = false, unique = true, length = 150)
    private String correo;

    @Column(name = "contrasena_hash", length = 255)
    private String contrasenaHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", length = 20)
    private TipoUsuarioJpa tipoUsuario;

    @Column(name = "id_rol")
    private Short rolId;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    public enum TipoUsuarioJpa {
        FINAL, ADMINISTRADOR, PROPIETARIO
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

    public TipoUsuarioJpa getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuarioJpa tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public Short getRolId() {
        return rolId;
    }

    public void setRolId(Short rolId) {
        this.rolId = rolId;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
