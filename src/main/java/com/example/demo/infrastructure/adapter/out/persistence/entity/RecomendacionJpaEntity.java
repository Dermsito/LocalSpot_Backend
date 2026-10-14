package com.example.demo.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recomendacion",
       uniqueConstraints = @UniqueConstraint(name = "uk_recomendacion_usuario_lugar",
                                              columnNames = {"id_usuario", "id_lugar"}))
public class RecomendacionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recomendacion")
    private Long idRecomendacion;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "id_lugar", nullable = false)
    private Long idLugar;

    @Column(name = "puntaje_afinidad", precision = 5, scale = 2)
    private BigDecimal puntajeAfinidad;

    @Column(name = "motivo", length = 200)
    private String motivo;

    @Column(name = "fecha_generacion")
    private LocalDateTime fechaGeneracion;

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
