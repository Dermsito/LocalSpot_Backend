package com.example.demo.domain.model;

import java.time.LocalDateTime;

/**
 * Lugar marcado como favorito por un usuario.
 * PK compuesta: idUsuario + idLugar.
 */
public class Favorito {

    private Long idUsuario;
    private Long idLugar;
    private LocalDateTime fechaAgregado;

    public Favorito() {
    }

    public Favorito(Long idUsuario, Long idLugar, LocalDateTime fechaAgregado) {
        this.idUsuario = idUsuario;
        this.idLugar = idLugar;
        this.fechaAgregado = fechaAgregado;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdLugar() {
        return idLugar;
    }

    public void setIdLugar(Long idLugar) {
        this.idLugar = idLugar;
    }

    public LocalDateTime getFechaAgregado() {
        return fechaAgregado;
    }

    public void setFechaAgregado(LocalDateTime fechaAgregado) {
        this.fechaAgregado = fechaAgregado;
    }
}
