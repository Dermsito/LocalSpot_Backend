package com.example.demo.infrastructure.adapter.out.persistence.repository;

import com.example.demo.infrastructure.adapter.out.persistence.entity.LugarJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LugarJpaRepository extends JpaRepository<LugarJpaEntity, Long> {

    boolean existsByIdCategoria(Long idCategoria);

    Page<LugarJpaEntity> findByIdPropietario(Long idPropietario, Pageable pageable);
}
