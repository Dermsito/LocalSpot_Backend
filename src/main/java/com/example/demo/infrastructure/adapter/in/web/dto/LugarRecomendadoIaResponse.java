package com.example.demo.infrastructure.adapter.in.web.dto;

public class LugarRecomendadoIaResponse {

    private LugarResponse lugar;
    private String motivoIa;

    public LugarRecomendadoIaResponse() {
    }

    public LugarRecomendadoIaResponse(LugarResponse lugar, String motivoIa) {
        this.lugar = lugar;
        this.motivoIa = motivoIa;
    }

    public LugarResponse getLugar() {
        return lugar;
    }

    public void setLugar(LugarResponse lugar) {
        this.lugar = lugar;
    }

    public String getMotivoIa() {
        return motivoIa;
    }

    public void setMotivoIa(String motivoIa) {
        this.motivoIa = motivoIa;
    }
}
