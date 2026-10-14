package com.example.demo.infrastructure.adapter.out.persistence.adapter;

import com.example.demo.domain.model.EstadoReporte;
import com.example.demo.domain.model.Reporte;
import com.example.demo.domain.port.ReporteRepositoryPort;
import com.example.demo.infrastructure.adapter.out.persistence.entity.ReporteJpaEntity;
import com.example.demo.infrastructure.adapter.out.persistence.repository.ReporteJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ReporteRepositoryAdapter implements ReporteRepositoryPort {

    private final ReporteJpaRepository reporteJpaRepository;

    public ReporteRepositoryAdapter(ReporteJpaRepository reporteJpaRepository) {
        this.reporteJpaRepository = reporteJpaRepository;
    }

    @Override
    public Reporte save(Reporte reporte) {
        ReporteJpaEntity entity = toEntity(reporte);
        ReporteJpaEntity saved = reporteJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<Reporte> findAllOrderByEstado() {
        return reporteJpaRepository.findAllByOrderByEstadoAscFechaCreacionDesc().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Reporte> findById(Long idReporte) {
        return reporteJpaRepository.findById(idReporte).map(this::toDomain);
    }

    @Override
    public boolean existsByCalificacionIdAndUsuarioId(Long idCalificacion, Long idUsuario) {
        return reporteJpaRepository.existsByIdCalificacionAndIdUsuario(idCalificacion, idUsuario);
    }

    @Override
    public void updateEstado(Long idReporte, EstadoReporte estado) {
        ReporteJpaEntity.EstadoReporteJpa estadoJpa = ReporteJpaEntity.EstadoReporteJpa.valueOf(estado.name());
        reporteJpaRepository.updateEstado(idReporte, estadoJpa, LocalDateTime.now());
    }

    private Reporte toDomain(ReporteJpaEntity entity) {
        Reporte r = new Reporte();
        r.setIdReporte(entity.getIdReporte());
        r.setIdCalificacion(entity.getIdCalificacion());
        r.setIdUsuario(entity.getIdUsuario());
        r.setMotivo(entity.getMotivo());
        if (entity.getEstado() != null) {
            r.setEstado(EstadoReporte.valueOf(entity.getEstado().name()));
        }
        r.setFechaCreacion(entity.getFechaCreacion());
        r.setFechaResolucion(entity.getFechaResolucion());
        return r;
    }

    private ReporteJpaEntity toEntity(Reporte r) {
        ReporteJpaEntity entity = new ReporteJpaEntity();
        entity.setIdReporte(r.getIdReporte());
        entity.setIdCalificacion(r.getIdCalificacion());
        entity.setIdUsuario(r.getIdUsuario());
        entity.setMotivo(r.getMotivo());
        if (r.getEstado() != null) {
            entity.setEstado(ReporteJpaEntity.EstadoReporteJpa.valueOf(r.getEstado().name()));
        }
        entity.setFechaCreacion(r.getFechaCreacion());
        entity.setFechaResolucion(r.getFechaResolucion());
        return entity;
    }
}
