package com.example.demo.infrastructure.adapter.out.persistence.repository;

import com.example.demo.infrastructure.adapter.out.persistence.entity.LogEventoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogEventoJpaRepository extends JpaRepository<LogEventoJpaEntity, Long> {
    List<LogEventoJpaEntity> findByNivel(String nivel);
    List<LogEventoJpaEntity> findByCorrelationId(String correlationId);
    List<LogEventoJpaEntity> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
}
