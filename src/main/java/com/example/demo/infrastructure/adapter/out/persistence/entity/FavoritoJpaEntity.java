package com.example.demo.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favorito")
public class FavoritoJpaEntity {

    @EmbeddedId
    private FavoritoId id;

    @Column(name = "fecha_agregado")
    private LocalDateTime fechaAgregado;

    public FavoritoJpaEntity() {
    }

    public FavoritoId getId() {
        return id;
    }

    public void setId(FavoritoId id) {
        this.id = id;
    }

    public Long getIdUsuario() {
        return id != null ? id.getIdUsuario() : null;
    }

    public Long getIdLugar() {
        return id != null ? id.getIdLugar() : null;
    }

    public LocalDateTime getFechaAgregado() {
        return fechaAgregado;
    }

    public void setFechaAgregado(LocalDateTime fechaAgregado) {
        this.fechaAgregado = fechaAgregado;
    }
}
