package com.example.demo.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class LugarDetalleResponse {

    private Long idLugar;
    private Long idCategoria;
    private String nombreCategoria;
    private String nombre;
    private String descripcion;
    private String direccion;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private BigDecimal precioMin;
    private BigDecimal precioMax;
    private String estadoVerificacion;
    private double promedioCalificacion;
    private List<FotoResponse> fotos;
    private List<CalificacionResponse> calificaciones;
    private LocalDateTime fechaRegistro;

    public Long getIdLugar() { return idLugar; }
    public void setIdLugar(Long idLugar) { this.idLugar = idLugar; }

    public Long getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Long idCategoria) { this.idCategoria = idCategoria; }

    public String getNombreCategoria() { return nombreCategoria; }
    public void setNombreCategoria(String nombreCategoria) { this.nombreCategoria = nombreCategoria; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public BigDecimal getLatitud() { return latitud; }
    public void setLatitud(BigDecimal latitud) { this.latitud = latitud; }

    public BigDecimal getLongitud() { return longitud; }
    public void setLongitud(BigDecimal longitud) { this.longitud = longitud; }

    public BigDecimal getPrecioMin() { return precioMin; }
    public void setPrecioMin(BigDecimal precioMin) { this.precioMin = precioMin; }

    public BigDecimal getPrecioMax() { return precioMax; }
    public void setPrecioMax(BigDecimal precioMax) { this.precioMax = precioMax; }

    public String getEstadoVerificacion() { return estadoVerificacion; }
    public void setEstadoVerificacion(String estadoVerificacion) { this.estadoVerificacion = estadoVerificacion; }

    public double getPromedioCalificacion() { return promedioCalificacion; }
    public void setPromedioCalificacion(double promedioCalificacion) { this.promedioCalificacion = promedioCalificacion; }

    public List<FotoResponse> getFotos() { return fotos; }
    public void setFotos(List<FotoResponse> fotos) { this.fotos = fotos; }

    public List<CalificacionResponse> getCalificaciones() { return calificaciones; }
    public void setCalificaciones(List<CalificacionResponse> calificaciones) { this.calificaciones = calificaciones; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
