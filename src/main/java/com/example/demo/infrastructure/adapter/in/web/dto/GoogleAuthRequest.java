package com.example.demo.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Peticion de autenticacion con Google OAuth2")
public class GoogleAuthRequest {

    @NotBlank(message = "El token de Google es obligatorio")
    @Schema(description = "ID token emitido por Google Identity Services (GIS)")
    private String idToken;

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}
