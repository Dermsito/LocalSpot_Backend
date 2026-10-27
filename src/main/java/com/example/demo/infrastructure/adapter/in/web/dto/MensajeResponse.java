package com.example.demo.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Mensaje informativo de respuesta")
public class MensajeResponse {

    @Schema(description = "Mensaje para el usuario", example = "Correo verificado correctamente")
    private String mensaje;

    public MensajeResponse() {
    }

    public MensajeResponse(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
