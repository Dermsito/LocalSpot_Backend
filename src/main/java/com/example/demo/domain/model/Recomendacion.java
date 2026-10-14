package com.example.demo.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Recomendacion de un lugar generada para un usuario.
 * Un lugar solo puede ser recomendado una vez por usuario (UNIQUE id_usuario, id_lugar).
 */
public class Recomendacion {

    private Long idRecomendacion;
    private Long idUsuario;
    private Long idLugar;
    private BigDecimal puntajeAfinidad;
    private String motivo;
    private LocalDateTime fechaGeneracion;

    public Recomendacion() {
    }

    public Long getIdRecomendacion() {
        return idRecomendacion;
    }

    public void setIdRecomendacion(Long idRecomendacion) {
        this.idRecomendacion = idRecomendacion;
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

    public BigDecimal getPuntajeAfinidad() {
        return puntajeAfinidad;
    }

    public void setPuntajeAfinidad(BigDecimal puntajeAfinidad) {
        this.puntajeAfinidad = puntajeAfinidad;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }
}
