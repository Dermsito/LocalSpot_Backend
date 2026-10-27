package com.example.demo.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Token recibido por correo para verificar la cuenta")
public class VerificarCorreoRequest {

    @NotBlank(message = "El token es obligatorio")
    @Schema(description = "Token de verificacion", example = "8f14e45f-ceea-467f-a8f5-2f9b6f4b1e11")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
