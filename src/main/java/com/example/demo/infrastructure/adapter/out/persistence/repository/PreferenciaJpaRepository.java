package com.example.demo.infrastructure.adapter.out.persistence.repository;

import com.example.demo.infrastructure.adapter.out.persistence.entity.PreferenciaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PreferenciaJpaRepository extends JpaRepository<PreferenciaJpaEntity, Long> {
    List<PreferenciaJpaEntity> findByIdUsuario(Long idUsuario);
    Optional<PreferenciaJpaEntity> findByIdUsuarioAndIdCategoria(Long idUsuario, Long idCategoria);
}
