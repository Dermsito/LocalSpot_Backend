package com.example.demo.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public class ResolverReporteRequest {

    @NotBlank(message = "El estado es obligatorio (ACEPTADO o DESCARTADO)")
    private String estado;

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
