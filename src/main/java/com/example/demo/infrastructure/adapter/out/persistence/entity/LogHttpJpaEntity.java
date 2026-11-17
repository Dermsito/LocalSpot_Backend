package com.example.demo.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "log_http")
public class LogHttpJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_log_http")
    private Long idLogHttp;

    @Column(name = "correlation_id", length = 64)
    private String correlationId;

    @Column(name = "metodo", nullable = false, length = 10)
    private String metodo;

    @Column(name = "uri", nullable = false, length = 500)
    private String uri;

    @Column(name = "query_params", length = 1000)
    private String queryParams;

    @Column(name = "status_code", nullable = false)
    private Integer statusCode;

    @Column(name = "tiempo_ms", nullable = false)
    private Long tiempoMs;

    @Column(name = "ip_origen", length = 64)
    private String ipOrigen;

    @Column(name = "usuario_correo", length = 150)
    private String usuarioCorreo;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    public LogHttpJpaEntity() {
    }

    public LogHttpJpaEntity(String correlationId, String metodo, String uri, String queryParams,
                            Integer statusCode, Long tiempoMs, String ipOrigen,
                            String usuarioCorreo, LocalDateTime fechaCreacion) {
        this.correlationId = correlationId;
        this.metodo = metodo;
        this.uri = uri;
        this.queryParams = queryParams;
        this.statusCode = statusCode;
        this.tiempoMs = tiempoMs;
        this.ipOrigen = ipOrigen;
        this.usuarioCorreo = usuarioCorreo;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getIdLogHttp() {
        return idLogHttp;
    }

    public void setIdLogHttp(Long idLogHttp) {
        this.idLogHttp = idLogHttp;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(String queryParams) {
        this.queryParams = queryParams;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Long getTiempoMs() {
        return tiempoMs;
    }

    public void setTiempoMs(Long tiempoMs) {
        this.tiempoMs = tiempoMs;
    }

    public String getIpOrigen() {
        return ipOrigen;
    }

    public void setIpOrigen(String ipOrigen) {
        this.ipOrigen = ipOrigen;
    }

    public String getUsuarioCorreo() {
        return usuarioCorreo;
    }

    public void setUsuarioCorreo(String usuarioCorreo) {
        this.usuarioCorreo = usuarioCorreo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
