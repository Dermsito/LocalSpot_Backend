package com.example.demo.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para restablecer la contrasena")
public class RestablecerContrasenaRequest {

    @NotBlank(message = "El token es obligatorio")
    @Schema(description = "Token de recuperacion recibido por correo")
    private String token;

    @NotBlank(message = "La nueva contrasena es obligatoria")
    @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres")
    @Schema(description = "Nueva contrasena", example = "secreta123")
    private String nuevaContrasena;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNuevaContrasena() {
        return nuevaContrasena;
    }

    public void setNuevaContrasena(String nuevaContrasena) {
        this.nuevaContrasena = nuevaContrasena;
    }
}
