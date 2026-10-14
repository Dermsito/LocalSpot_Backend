package com.example.demo.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FavoritoId implements Serializable {

    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "id_lugar")
    private Long idLugar;

    public FavoritoId() {
    }

    public FavoritoId(Long idUsuario, Long idLugar) {
        this.idUsuario = idUsuario;
        this.idLugar = idLugar;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FavoritoId that = (FavoritoId) o;
        return Objects.equals(idUsuario, that.idUsuario) && Objects.equals(idLugar, that.idLugar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, idLugar);
    }
}
