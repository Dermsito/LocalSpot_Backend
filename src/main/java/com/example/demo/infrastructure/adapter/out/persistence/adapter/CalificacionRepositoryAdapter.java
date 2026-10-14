package com.example.demo.infrastructure.adapter.out.persistence.adapter;

import com.example.demo.domain.model.Calificacion;
import com.example.demo.domain.port.CalificacionRepositoryPort;
import com.example.demo.infrastructure.adapter.out.persistence.entity.CalificacionJpaEntity;
import com.example.demo.infrastructure.adapter.out.persistence.repository.CalificacionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CalificacionRepositoryAdapter implements CalificacionRepositoryPort {

    private final CalificacionJpaRepository calificacionJpaRepository;

    public CalificacionRepositoryAdapter(CalificacionJpaRepository calificacionJpaRepository) {
        this.calificacionJpaRepository = calificacionJpaRepository;
    }

    @Override
    public Calificacion save(Calificacion calificacion) {
        CalificacionJpaEntity entity = toEntity(calificacion);
        CalificacionJpaEntity saved = calificacionJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Calificacion> findByUsuarioIdAndLugarId(Long idUsuario, Long idLugar) {
        return calificacionJpaRepository.findByIdUsuarioAndIdLugar(idUsuario, idLugar)
                .map(this::toDomain);
    }

    @Override
    public List<Calificacion> findByLugarIdAndVisibleTrue(Long idLugar) {
        return calificacionJpaRepository.findByIdLugarAndVisibleTrue(idLugar).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Calificacion> findByLugarId(Long idLugar) {
        return calificacionJpaRepository.findByIdLugar(idLugar).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Calificacion> findById(Long idCalificacion) {
        return calificacionJpaRepository.findById(idCalificacion).map(this::toDomain);
    }

    @Override
    public boolean existsByUsuarioIdAndLugarId(Long idUsuario, Long idLugar) {
        return calificacionJpaRepository.existsByIdUsuarioAndIdLugar(idUsuario, idLugar);
    }

    @Override
    public void updateVisible(Long idCalificacion, boolean visible) {
        calificacionJpaRepository.updateVisible(idCalificacion, visible);
    }

    @Override
    public double calcularPromedioByLugarId(Long idLugar) {
        return calificacionJpaRepository.calcularPromedioByIdLugar(idLugar);
    }

    private Calificacion toDomain(CalificacionJpaEntity entity) {
        Calificacion c = new Calificacion();
        c.setIdCalificacion(entity.getIdCalificacion());
        c.setIdUsuario(entity.getIdUsuario());
        c.setIdLugar(entity.getIdLugar());
        c.setPuntuacion(entity.getPuntuacion());
        c.setComentario(entity.getComentario());
        c.setVisible(entity.getVisible());
        c.setFechaRegistro(entity.getFechaRegistro());
        c.setFechaActualizacion(entity.getFechaActualizacion());
        return c;
    }

    private CalificacionJpaEntity toEntity(Calificacion c) {
        CalificacionJpaEntity entity = new CalificacionJpaEntity();
        entity.setIdCalificacion(c.getIdCalificacion());
        entity.setIdUsuario(c.getIdUsuario());
        entity.setIdLugar(c.getIdLugar());
        entity.setPuntuacion(c.getPuntuacion());
        entity.setComentario(c.getComentario());
        entity.setVisible(c.getVisible() != null ? c.getVisible() : true);
        entity.setFechaRegistro(c.getFechaRegistro());
        entity.setFechaActualizacion(c.getFechaActualizacion());
        return entity;
    }
}
