package com.example.demo.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "foto_lugar")
public class FotoLugarJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_foto_lugar")
    private Long idFotoLugar;

    @Column(name = "id_lugar", nullable = false)
    private Long idLugar;

    @Column(name = "url_foto", nullable = false, length = 500)
    private String urlFoto;

    @Column(name = "fecha_subida")
    private LocalDateTime fechaSubida;

    public Long getIdFotoLugar() {
        return idFotoLugar;
    }

    public void setIdFotoLugar(Long idFotoLugar) {
        this.idFotoLugar = idFotoLugar;
    }

    public Long getIdLugar() {
        return idLugar;
    }

    public void setIdLugar(Long idLugar) {
        this.idLugar = idLugar;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }
}
