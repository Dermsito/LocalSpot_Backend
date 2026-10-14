package com.example.demo.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ReporteRequest {

    @NotBlank(message = "El motivo del reporte es obligatorio")
    @Size(max = 250, message = "El motivo no puede exceder 250 caracteres")
    private String motivo;

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
