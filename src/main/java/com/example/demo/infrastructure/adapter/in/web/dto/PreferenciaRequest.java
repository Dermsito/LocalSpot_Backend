package com.example.demo.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class PreferenciaRequest {

    @NotNull(message = "El id de categoria es obligatorio")
    private Long idCategoria;

    @NotNull(message = "El presupuesto maximo es obligatorio")
    @DecimalMin(value = "0.0", message = "El presupuesto no puede ser negativo")
    private BigDecimal presupuestoMax;

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
