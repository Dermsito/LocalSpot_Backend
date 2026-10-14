package com.example.demo.infrastructure.adapter.out.persistence.repository;

import com.example.demo.infrastructure.adapter.out.persistence.entity.FavoritoId;
import com.example.demo.infrastructure.adapter.out.persistence.entity.FavoritoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritoJpaRepository extends JpaRepository<FavoritoJpaEntity, FavoritoId> {

    List<FavoritoJpaEntity> findByIdIdUsuario(Long idUsuario);

    Optional<FavoritoJpaEntity> findByIdIdUsuarioAndIdIdLugar(Long idUsuario, Long idLugar);

    boolean existsByIdIdUsuarioAndIdIdLugar(Long idUsuario, Long idLugar);

    @Transactional
    void deleteByIdIdUsuarioAndIdIdLugar(Long idUsuario, Long idLugar);
}
