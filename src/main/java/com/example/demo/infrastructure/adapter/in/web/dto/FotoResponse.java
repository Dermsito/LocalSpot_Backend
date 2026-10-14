package com.example.demo.infrastructure.adapter.in.web.dto;

import java.time.LocalDateTime;

public class FotoResponse {

    private Long idFotoLugar;
    private Long idLugar;
    private String urlFoto;
    private LocalDateTime fechaSubida;

    public FotoResponse() {
    }

    public FotoResponse(Long idFotoLugar, Long idLugar, String urlFoto, LocalDateTime fechaSubida) {
        this.idFotoLugar = idFotoLugar;
        this.idLugar = idLugar;
        this.urlFoto = urlFoto;
        this.fechaSubida = fechaSubida;
    }

    public Long getIdFotoLugar() { return idFotoLugar; }
    public void setIdFotoLugar(Long idFotoLugar) { this.idFotoLugar = idFotoLugar; }

    public Long getIdLugar() { return idLugar; }
    public void setIdLugar(Long idLugar) { this.idLugar = idLugar; }

    public String getUrlFoto() { return urlFoto; }
    public void setUrlFoto(String urlFoto) { this.urlFoto = urlFoto; }

    public LocalDateTime getFechaSubida() { return fechaSubida; }
    public void setFechaSubida(LocalDateTime fechaSubida) { this.fechaSubida = fechaSubida; }
}
