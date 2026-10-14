package com.example.demo.infrastructure.adapter.out.persistence.repository;

import com.example.demo.infrastructure.adapter.out.persistence.entity.CalificacionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalificacionJpaRepository extends JpaRepository<CalificacionJpaEntity, Long> {

    Optional<CalificacionJpaEntity> findByIdUsuarioAndIdLugar(Long idUsuario, Long idLugar);

    List<CalificacionJpaEntity> findByIdLugarAndVisibleTrue(Long idLugar);

    List<CalificacionJpaEntity> findByIdLugar(Long idLugar);

    boolean existsByIdUsuarioAndIdLugar(Long idUsuario, Long idLugar);

    @Modifying
    @Transactional
    @Query("UPDATE CalificacionJpaEntity c SET c.visible = :visible WHERE c.idCalificacion = :id")
    void updateVisible(@Param("id") Long idCalificacion, @Param("visible") boolean visible);

    @Query("SELECT COALESCE(AVG(c.puntuacion), 0.0) FROM CalificacionJpaEntity c WHERE c.idLugar = :idLugar AND c.visible = true")
    double calcularPromedioByIdLugar(@Param("idLugar") Long idLugar);
}
