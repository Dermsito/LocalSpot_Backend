package com.example.demo.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "log_evento")
public class LogEventoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_log_evento")
    private Long idLogEvento;

    @Column(name = "correlation_id", length = 64)
    private String correlationId;

    @Column(name = "nivel", nullable = false, length = 10)
    private String nivel;

    @Column(name = "origen", nullable = false, length = 255)
    private String origen;

    @Column(name = "mensaje", nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "detalles", columnDefinition = "TEXT")
    private String detalles;

    @Column(name = "usuario_correo", length = 150)
    private String usuarioCorreo;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    public LogEventoJpaEntity() {
    }

    public LogEventoJpaEntity(String correlationId, String nivel, String origen,
                              String mensaje, String detalles, String usuarioCorreo,
                              LocalDateTime fechaCreacion) {
        this.correlationId = correlationId;
        this.nivel = nivel;
        this.origen = origen;
        this.mensaje = mensaje;
        this.detalles = detalles;
        this.usuarioCorreo = usuarioCorreo;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getIdLogEvento() {
        return idLogEvento;
    }

    public void setIdLogEvento(Long idLogEvento) {
        this.idLogEvento = idLogEvento;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public String getUsuarioCorreo() {
        return usuarioCorreo;
    }

    public void setUsuarioCorreo(String usuarioCorreo) {
        this.usuarioCorreo = usuarioCorreo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
