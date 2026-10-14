package com.example.demo.infrastructure.adapter.out.persistence.repository;

import com.example.demo.infrastructure.adapter.out.persistence.entity.RecomendacionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecomendacionJpaRepository extends JpaRepository<RecomendacionJpaEntity, Long> {

    List<RecomendacionJpaEntity> findByIdUsuarioOrderByPuntajeAfinidadDesc(Long idUsuario);

    boolean existsByIdUsuarioAndIdLugar(Long idUsuario, Long idLugar);

    Optional<RecomendacionJpaEntity> findByIdUsuarioAndIdLugar(Long idUsuario, Long idLugar);
}
