package com.example.demo.infrastructure.adapter.out.persistence.repository;

import com.example.demo.infrastructure.adapter.out.persistence.entity.FotoLugarJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FotoLugarJpaRepository extends JpaRepository<FotoLugarJpaEntity, Long> {

    List<FotoLugarJpaEntity> findByIdLugar(Long idLugar);

    @Transactional
    void deleteByIdLugar(Long idLugar);
}
