package com.example.demo.domain.model;

import java.time.LocalDateTime;

/**
 * Foto asociada a un lugar.
 */
public class FotoLugar {

    private Long idFotoLugar;
    private Long idLugar;
    private String urlFoto;
    private LocalDateTime fechaSubida;

    public FotoLugar() {
    }

    public Long getIdFotoLugar() {
        return idFotoLugar;
    }

    public void setIdFotoLugar(Long idFotoLugar) {
        this.idFotoLugar = idFotoLugar;
    }

    public Long getIdLugar() {
        return idLugar;
    }

    public void setIdLugar(Long idLugar) {
        this.idLugar = idLugar;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }
}
