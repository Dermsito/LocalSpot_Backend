package com.example.demo.domain.port;

import com.example.demo.domain.model.EstadoReporte;
import com.example.demo.domain.model.Reporte;

import java.util.List;
import java.util.Optional;

public interface ReporteRepositoryPort {
    Reporte save(Reporte reporte);
    List<Reporte> findAllOrderByEstado();
    Optional<Reporte> findById(Long idReporte);
    boolean existsByCalificacionIdAndUsuarioId(Long idCalificacion, Long idUsuario);
    void updateEstado(Long idReporte, EstadoReporte estado);
}
