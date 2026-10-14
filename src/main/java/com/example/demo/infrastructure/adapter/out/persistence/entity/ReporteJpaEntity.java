package com.example.demo.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reporte",
       uniqueConstraints = @UniqueConstraint(name = "uk_reporte_calificacion_usuario",
                                              columnNames = {"id_calificacion", "id_usuario"}))
public class ReporteJpaEntity {

    public enum EstadoReporteJpa {
        PENDIENTE, ACEPTADO, DESCARTADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reporte")
    private Long idReporte;

    @Column(name = "id_calificacion", nullable = false)
    private Long idCalificacion;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "motivo", length = 250)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    private EstadoReporteJpa estado;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    public Long getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(Long idReporte) {
        this.idReporte = idReporte;
    }

    public Long getIdCalificacion() {
        return idCalificacion;
    }

    public void setIdCalificacion(Long idCalificacion) {
        this.idCalificacion = idCalificacion;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public EstadoReporteJpa getEstado() {
        return estado;
    }

    public void setEstado(EstadoReporteJpa estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaResolucion() {
        return fechaResolucion;
    }

    public void setFechaResolucion(LocalDateTime fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }
}
