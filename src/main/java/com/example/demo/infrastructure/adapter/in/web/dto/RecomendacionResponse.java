package com.example.demo.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RecomendacionResponse {

    private Long idRecomendacion;
    private Long idUsuario;
    private Long idLugar;
    private String nombreLugar;
    private BigDecimal puntajeAfinidad;
    private String motivo;
    private LocalDateTime fechaGeneracion;

    public Long getIdRecomendacion() { return idRecomendacion; }
    public void setIdRecomendacion(Long idRecomendacion) { this.idRecomendacion = idRecomendacion; }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public Long getIdLugar() { return idLugar; }
    public void setIdLugar(Long idLugar) { this.idLugar = idLugar; }

    public String getNombreLugar() { return nombreLugar; }
    public void setNombreLugar(String nombreLugar) { this.nombreLugar = nombreLugar; }

    public BigDecimal getPuntajeAfinidad() { return puntajeAfinidad; }
    public void setPuntajeAfinidad(BigDecimal puntajeAfinidad) { this.puntajeAfinidad = puntajeAfinidad; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
}
