package com.example.demo.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;

public class PreferenciaResponse {

    private Long idPreferencia;
    private Long idUsuario;
    private Long idCategoria;
    private String nombreCategoria;
    private BigDecimal presupuestoMax;

    public PreferenciaResponse() {
    }

    public PreferenciaResponse(Long idPreferencia, Long idUsuario, Long idCategoria,
                               String nombreCategoria, BigDecimal presupuestoMax) {
        this.idPreferencia = idPreferencia;
        this.idUsuario = idUsuario;
        this.idCategoria = idCategoria;
        this.nombreCategoria = nombreCategoria;
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

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public BigDecimal getPresupuestoMax() {
        return presupuestoMax;
    }

    public void setPresupuestoMax(BigDecimal presupuestoMax) {
        this.presupuestoMax = presupuestoMax;
    }
}
