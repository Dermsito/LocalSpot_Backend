package com.example.demo.infrastructure.adapter.out.persistence.repository;

import com.example.demo.infrastructure.adapter.out.persistence.entity.LogHttpJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogHttpJpaRepository extends JpaRepository<LogHttpJpaEntity, Long> {
    List<LogHttpJpaEntity> findByCorrelationId(String correlationId);
    List<LogHttpJpaEntity> findByStatusCodeGreaterThanEqual(Integer statusCode);
    List<LogHttpJpaEntity> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
}
