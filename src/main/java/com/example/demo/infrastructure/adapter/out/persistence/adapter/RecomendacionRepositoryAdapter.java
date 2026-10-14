package com.example.demo.infrastructure.adapter.out.persistence.adapter;

import com.example.demo.domain.model.Recomendacion;
import com.example.demo.domain.port.RecomendacionRepositoryPort;
import com.example.demo.infrastructure.adapter.out.persistence.entity.RecomendacionJpaEntity;
import com.example.demo.infrastructure.adapter.out.persistence.repository.RecomendacionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RecomendacionRepositoryAdapter implements RecomendacionRepositoryPort {

    private final RecomendacionJpaRepository recomendacionJpaRepository;

    public RecomendacionRepositoryAdapter(RecomendacionJpaRepository recomendacionJpaRepository) {
        this.recomendacionJpaRepository = recomendacionJpaRepository;
    }

    @Override
    public Recomendacion save(Recomendacion recomendacion) {
        RecomendacionJpaEntity entity = toEntity(recomendacion);
        RecomendacionJpaEntity saved = recomendacionJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<Recomendacion> findByUsuarioIdOrderByPuntajeDesc(Long idUsuario) {
        return recomendacionJpaRepository.findByIdUsuarioOrderByPuntajeAfinidadDesc(idUsuario).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByUsuarioIdAndLugarId(Long idUsuario, Long idLugar) {
        return recomendacionJpaRepository.existsByIdUsuarioAndIdLugar(idUsuario, idLugar);
    }

    @Override
    public Optional<Recomendacion> findByUsuarioIdAndLugarId(Long idUsuario, Long idLugar) {
        return recomendacionJpaRepository.findByIdUsuarioAndIdLugar(idUsuario, idLugar)
                .map(this::toDomain);
    }

    private Recomendacion toDomain(RecomendacionJpaEntity entity) {
        Recomendacion r = new Recomendacion();
        r.setIdRecomendacion(entity.getIdRecomendacion());
        r.setIdUsuario(entity.getIdUsuario());
        r.setIdLugar(entity.getIdLugar());
        r.setPuntajeAfinidad(entity.getPuntajeAfinidad());
        r.setMotivo(entity.getMotivo());
        r.setFechaGeneracion(entity.getFechaGeneracion());
        return r;
    }

    private RecomendacionJpaEntity toEntity(Recomendacion r) {
        RecomendacionJpaEntity entity = new RecomendacionJpaEntity();
        entity.setIdRecomendacion(r.getIdRecomendacion());
        entity.setIdUsuario(r.getIdUsuario());
        entity.setIdLugar(r.getIdLugar());
        entity.setPuntajeAfinidad(r.getPuntajeAfinidad());
        entity.setMotivo(r.getMotivo());
        entity.setFechaGeneracion(r.getFechaGeneracion());
        return entity;
    }
}
