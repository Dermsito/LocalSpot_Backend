package com.example.demo.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Token de un solo uso para verificar el correo de un usuario recien
 * registrado.
 */
public class TokenVerificacionCorreo {

    private Long idToken;
    private Long idUsuario;
    private String token;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaExpiracion;
    private boolean usado;

    public TokenVerificacionCorreo() {
    }

    /**
     * Crea un token nuevo vigente durante los minutos indicados.
     */
    public static TokenVerificacionCorreo nuevo(Long idUsuario, long expiracionMinutos) {
        LocalDateTime ahora = LocalDateTime.now();
        return new TokenVerificacionCorreo(null, idUsuario, UUID.randomUUID().toString(),
                ahora, ahora.plusMinutes(expiracionMinutos), false);
    }

    public TokenVerificacionCorreo(Long idToken, Long idUsuario, String token,
            LocalDateTime fechaCreacion, LocalDateTime fechaExpiracion, boolean usado) {
        this.idToken = idToken;
        this.idUsuario = idUsuario;
        this.token = token;
        this.fechaCreacion = fechaCreacion;
        this.fechaExpiracion = fechaExpiracion;
        this.usado = usado;
    }

    /**
     * Indica si el token puede usarse en el momento indicado.
     */
    public boolean estaVigente(LocalDateTime momento) {
        return !usado && fechaExpiracion != null && fechaExpiracion.isAfter(momento);
    }

    public Long getIdToken() {
        return idToken;
    }

    public void setIdToken(Long idToken) {
        this.idToken = idToken;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public boolean isUsado() {
        return usado;
    }

    public void setUsado(boolean usado) {
        this.usado = usado;
    }
}
