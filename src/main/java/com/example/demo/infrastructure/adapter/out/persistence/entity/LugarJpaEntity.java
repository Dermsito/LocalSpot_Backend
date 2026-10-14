package com.example.demo.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "lugar")
public class LugarJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lugar")
    private Long idLugar;

    @Column(name = "id_categoria", nullable = false)
    private Long idCategoria;

    @Column(name = "id_propietario")
    private Long idPropietario;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "direccion", length = 255)
    private String direccion;

    @Column(name = "horario", length = 255)
    private String horario;

    @Column(name = "latitud", precision = 10, scale = 7)
    private BigDecimal latitud;

    @Column(name = "longitud", precision = 10, scale = 7)
    private BigDecimal longitud;

    @Column(name = "precio_min", precision = 12, scale = 2)
    private BigDecimal precioMin;

    @Column(name = "precio_max", precision = 12, scale = 2)
    private BigDecimal precioMax;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_verificacion", length = 20)
    private EstadoVerificacionJpa estadoVerificacion;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public enum EstadoVerificacionJpa {
        PENDIENTE, VERIFICADO, RECHAZADO
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

    public EstadoVerificacionJpa getEstadoVerificacion() {
        return estadoVerificacion;
    }

    public void setEstadoVerificacion(EstadoVerificacionJpa estadoVerificacion) {
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
