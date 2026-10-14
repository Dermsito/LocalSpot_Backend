package com.example.demo.infrastructure.adapter.in.web.dto;

public class CategoriaResponse {

    private Long idCategoria;
    private String nombreCategoria;

    public CategoriaResponse() {
    }

    public CategoriaResponse(Long idCategoria, String nombreCategoria) {
        this.idCategoria = idCategoria;
        this.nombreCategoria = nombreCategoria;
    }

    public Long getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Long idCategoria) { this.idCategoria = idCategoria; }

    public String getNombreCategoria() { return nombreCategoria; }
    public void setNombreCategoria(String nombreCategoria) { this.nombreCategoria = nombreCategoria; }
}
