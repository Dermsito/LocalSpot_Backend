package com.example.demo.domain.model;

/**
 * Rol del catalogo de roles.
 * id: 1=ADMINISTRADOR, 2=FINAL, 3=PROPIETARIO.
 */
public class Rol {

    private Short id;
    private String nombre;
    private String descripcion;

    public Rol() {
    }

    public Rol(Short id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
