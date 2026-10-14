package com.example.demo.infrastructure.adapter.in.web.dto;

import java.time.LocalDateTime;

public class FavoritoResponse {

    private Long idUsuario;
    private Long idLugar;
    private String nombreLugar;
    private LocalDateTime fechaAgregado;

    public FavoritoResponse() {
    }

    public FavoritoResponse(Long idUsuario, Long idLugar, String nombreLugar, LocalDateTime fechaAgregado) {
        this.idUsuario = idUsuario;
        this.idLugar = idLugar;
        this.nombreLugar = nombreLugar;
        this.fechaAgregado = fechaAgregado;
    }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public Long getIdLugar() { return idLugar; }
    public void setIdLugar(Long idLugar) { this.idLugar = idLugar; }

    public String getNombreLugar() { return nombreLugar; }
    public void setNombreLugar(String nombreLugar) { this.nombreLugar = nombreLugar; }

    public LocalDateTime getFechaAgregado() { return fechaAgregado; }
    public void setFechaAgregado(LocalDateTime fechaAgregado) { this.fechaAgregado = fechaAgregado; }
}
