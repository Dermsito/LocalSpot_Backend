package com.example.demo.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Peticion de busqueda inteligente con IA")
public class BusquedaIaRequest {

    @NotBlank(message = "La consulta de busqueda no puede estar vacia")
    @Schema(description = "Consulta en lenguaje natural de lo que busca el usuario", example = "un café tranquilo para trabajar con buena conexión")
    private String consulta;

    public String getConsulta() {
        return consulta;
    }

    public void setConsulta(String consulta) {
        this.consulta = consulta;
    }
}
