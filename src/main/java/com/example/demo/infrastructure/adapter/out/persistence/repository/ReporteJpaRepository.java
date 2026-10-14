package com.example.demo.infrastructure.adapter.out.persistence.repository;

import com.example.demo.infrastructure.adapter.out.persistence.entity.ReporteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReporteJpaRepository extends JpaRepository<ReporteJpaEntity, Long> {

    List<ReporteJpaEntity> findAllByOrderByEstadoAscFechaCreacionDesc();

    boolean existsByIdCalificacionAndIdUsuario(Long idCalificacion, Long idUsuario);

    @Modifying
    @Transactional
    @Query("UPDATE ReporteJpaEntity r SET r.estado = :estado, r.fechaResolucion = :fecha WHERE r.idReporte = :id")
    void updateEstado(@Param("id") Long idReporte,
                      @Param("estado") ReporteJpaEntity.EstadoReporteJpa estado,
                      @Param("fecha") LocalDateTime fechaResolucion);
}
