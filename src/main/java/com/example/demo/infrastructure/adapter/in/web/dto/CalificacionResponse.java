package com.example.demo.infrastructure.adapter.in.web.dto;

import java.time.LocalDateTime;

public class CalificacionResponse {

    private Long idCalificacion;
    private Long idUsuario;
    private String nombreUsuario;
    private Long idLugar;
    private Short puntuacion;
    private String comentario;
    private Boolean visible;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaActualizacion;

    public Long getIdCalificacion() { return idCalificacion; }
    public void setIdCalificacion(Long idCalificacion) { this.idCalificacion = idCalificacion; }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public Long getIdLugar() { return idLugar; }
    public void setIdLugar(Long idLugar) { this.idLugar = idLugar; }

    public Short getPuntuacion() { return puntuacion; }
    public void setPuntuacion(Short puntuacion) { this.puntuacion = puntuacion; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public Boolean getVisible() { return visible; }
    public void setVisible(Boolean visible) { this.visible = visible; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}
