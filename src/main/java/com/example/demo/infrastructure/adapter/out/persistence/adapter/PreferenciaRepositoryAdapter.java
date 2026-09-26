package com.example.demo.infrastructure.adapter.out.persistence.adapter;

import com.example.demo.domain.model.Preferencia;
import com.example.demo.domain.port.PreferenciaRepositoryPort;
import com.example.demo.infrastructure.adapter.out.persistence.entity.PreferenciaJpaEntity;
import com.example.demo.infrastructure.adapter.out.persistence.repository.PreferenciaJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PreferenciaRepositoryAdapter implements PreferenciaRepositoryPort {

    private final PreferenciaJpaRepository preferenciaJpaRepository;

    public PreferenciaRepositoryAdapter(PreferenciaJpaRepository preferenciaJpaRepository) {
        this.preferenciaJpaRepository = preferenciaJpaRepository;
    }

    @Override
    public Optional<Preferencia> findById(Long idPreferencia) {
        return preferenciaJpaRepository.findById(idPreferencia)
                .map(this::toDomain);
    }

    @Override
    public List<Preferencia> findByIdUsuario(Long idUsuario) {
        return preferenciaJpaRepository.findByIdUsuario(idUsuario).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Preferencia> findByIdUsuarioAndIdCategoria(Long idUsuario, Long idCategoria) {
        return preferenciaJpaRepository.findByIdUsuarioAndIdCategoria(idUsuario, idCategoria)
                .map(this::toDomain);
    }

    @Override
    public Preferencia save(Preferencia preferencia) {
        PreferenciaJpaEntity entity = toEntity(preferencia);
        PreferenciaJpaEntity saved = preferenciaJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteById(Long idPreferencia) {
        preferenciaJpaRepository.deleteById(idPreferencia);
    }

    @Override
    public boolean existsById(Long idPreferencia) {
        return preferenciaJpaRepository.existsById(idPreferencia);
    }

    private Preferencia toDomain(PreferenciaJpaEntity entity) {
        return new Preferencia(
                entity.getIdPreferencia(),
                entity.getIdUsuario(),
                entity.getIdCategoria(),
                entity.getPresupuestoMax()
        );
    }

    private PreferenciaJpaEntity toEntity(Preferencia preferencia) {
        PreferenciaJpaEntity entity = new PreferenciaJpaEntity();
        entity.setIdPreferencia(preferencia.getIdPreferencia());
        entity.setIdUsuario(preferencia.getIdUsuario());
        entity.setIdCategoria(preferencia.getIdCategoria());
        entity.setPresupuestoMax(preferencia.getPresupuestoMax());
        return entity;
    }
}
