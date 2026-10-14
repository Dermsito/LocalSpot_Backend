package com.example.demo.infrastructure.adapter.in.web.dto;

import java.time.LocalDateTime;

public class ReporteResponse {

    private Long idReporte;
    private Long idCalificacion;
    private Long idUsuario;
    private String motivo;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaResolucion;

    public Long getIdReporte() { return idReporte; }
    public void setIdReporte(Long idReporte) { this.idReporte = idReporte; }

    public Long getIdCalificacion() { return idCalificacion; }
    public void setIdCalificacion(Long idCalificacion) { this.idCalificacion = idCalificacion; }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDateTime fechaResolucion) { this.fechaResolucion = fechaResolucion; }
}
