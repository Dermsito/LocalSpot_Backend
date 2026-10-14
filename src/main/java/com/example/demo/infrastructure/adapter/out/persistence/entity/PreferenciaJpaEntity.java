package com.example.demo.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "preferencias")
public class PreferenciaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_preferencia")
    private Long idPreferencia;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "id_categoria", nullable = false)
    private Long idCategoria;

    @Column(name = "presupuesto_max", precision = 12, scale = 2)
    private BigDecimal presupuestoMax;

    public Long getIdPreferencia() {
        return idPreferencia;
    }

    public void setIdPreferencia(Long idPreferencia) {
        this.idPreferencia = idPreferencia;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public BigDecimal getPresupuestoMax() {
        return presupuestoMax;
    }

    public void setPresupuestoMax(BigDecimal presupuestoMax) {
        this.presupuestoMax = presupuestoMax;
    }
}
