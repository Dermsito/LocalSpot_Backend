package com.example.demo.domain.model;

import java.math.BigDecimal;

public class Preferencia {

    private Long idPreferencia;
    private Long idUsuario;
    private Long idCategoria;
    private BigDecimal presupuestoMax;

    public Preferencia() {
    }

    public Preferencia(Long idPreferencia, Long idUsuario, Long idCategoria, BigDecimal presupuestoMax) {
        this.idPreferencia = idPreferencia;
        this.idUsuario = idUsuario;
        this.idCategoria = idCategoria;
        this.presupuestoMax = presupuestoMax;
    }

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
