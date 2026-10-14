package com.example.demo.infrastructure.adapter.out.persistence.adapter;

import com.example.demo.domain.model.Favorito;
import com.example.demo.domain.port.FavoritoRepositoryPort;
import com.example.demo.infrastructure.adapter.out.persistence.entity.FavoritoId;
import com.example.demo.infrastructure.adapter.out.persistence.entity.FavoritoJpaEntity;
import com.example.demo.infrastructure.adapter.out.persistence.repository.FavoritoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FavoritoRepositoryAdapter implements FavoritoRepositoryPort {

    private final FavoritoJpaRepository favoritoJpaRepository;

    public FavoritoRepositoryAdapter(FavoritoJpaRepository favoritoJpaRepository) {
        this.favoritoJpaRepository = favoritoJpaRepository;
    }

    @Override
    public Favorito save(Favorito favorito) {
        FavoritoJpaEntity entity = toEntity(favorito);
        FavoritoJpaEntity saved = favoritoJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteByUsuarioIdAndLugarId(Long idUsuario, Long idLugar) {
        favoritoJpaRepository.deleteByIdIdUsuarioAndIdIdLugar(idUsuario, idLugar);
    }

    @Override
    public List<Favorito> findByUsuarioId(Long idUsuario) {
        return favoritoJpaRepository.findByIdIdUsuario(idUsuario).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByUsuarioIdAndLugarId(Long idUsuario, Long idLugar) {
        return favoritoJpaRepository.existsByIdIdUsuarioAndIdIdLugar(idUsuario, idLugar);
    }

    @Override
    public Optional<Favorito> findByUsuarioIdAndLugarId(Long idUsuario, Long idLugar) {
        return favoritoJpaRepository.findByIdIdUsuarioAndIdIdLugar(idUsuario, idLugar)
                .map(this::toDomain);
    }

    private Favorito toDomain(FavoritoJpaEntity entity) {
        return new Favorito(entity.getIdUsuario(), entity.getIdLugar(), entity.getFechaAgregado());
    }

    private FavoritoJpaEntity toEntity(Favorito favorito) {
        FavoritoJpaEntity entity = new FavoritoJpaEntity();
        entity.setId(new FavoritoId(favorito.getIdUsuario(), favorito.getIdLugar()));
        entity.setFechaAgregado(favorito.getFechaAgregado());
        return entity;
    }
}
