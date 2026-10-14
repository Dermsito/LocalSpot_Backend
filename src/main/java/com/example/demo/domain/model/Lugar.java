package com.example.demo.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Lugar/establecimiento de la plataforma.
 * Puede ser creado por un administrador (idPropietario null) o por un
 * propietario.
 */
public class Lugar {

    private Long idLugar;
    private Long idCategoria;
    private Long idPropietario;
    private String nombre;
    private String descripcion;
    private String direccion;
    private String horario;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private BigDecimal precioMin;
    private BigDecimal precioMax;
    private EstadoVerificacion estadoVerificacion;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaActualizacion;

    public Lugar() {
    }

    public Long getIdLugar() {
        return idLugar;
    }

    public void setIdLugar(Long idLugar) {
        this.idLugar = idLugar;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public Long getIdPropietario() {
        return idPropietario;
    }

    public void setIdPropietario(Long idPropietario) {
        this.idPropietario = idPropietario;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public BigDecimal getLatitud() {
        return latitud;
    }

    public void setLatitud(BigDecimal latitud) {
        this.latitud = latitud;
    }

    public BigDecimal getLongitud() {
        return longitud;
    }

    public void setLongitud(BigDecimal longitud) {
        this.longitud = longitud;
    }

    public BigDecimal getPrecioMin() {
        return precioMin;
    }

    public void setPrecioMin(BigDecimal precioMin) {
        this.precioMin = precioMin;
    }

    public BigDecimal getPrecioMax() {
        return precioMax;
    }

    public void setPrecioMax(BigDecimal precioMax) {
        this.precioMax = precioMax;
    }

    public EstadoVerificacion getEstadoVerificacion() {
        return estadoVerificacion;
    }

    public void setEstadoVerificacion(EstadoVerificacion estadoVerificacion) {
        this.estadoVerificacion = estadoVerificacion;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
