package com.example.demo.infrastructure.adapter.out.persistence.repository;

import com.example.demo.infrastructure.adapter.out.persistence.entity.LugarJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface LugarJpaRepository extends JpaRepository<LugarJpaEntity, Long> {

    boolean existsByIdCategoria(Long idCategoria);

    Page<LugarJpaEntity> findByIdPropietario(Long idPropietario, Pageable pageable);

    @Query(value = """
            SELECT COUNT(*) FROM lugar l
            WHERE (:q IS NULL OR LOWER(l.nombre) LIKE LOWER(CONCAT('%', :q, '%'))
                   OR LOWER(l.descripcion) LIKE LOWER(CONCAT('%', :q, '%')))
              AND (:categoriaId IS NULL OR l.id_categoria = :categoriaId)
              AND (:estado IS NULL OR l.estado_verificacion = :estado)
              AND (:precioMin IS NULL OR l.precio_max >= :precioMin)
              AND (:precioMax IS NULL OR l.precio_min <= :precioMax)
              AND (:latitud IS NULL OR :longitud IS NULL OR :radioKm IS NULL
                   OR (6371 * ACOS(LEAST(1.0, GREATEST(-1.0,
                       COS(RADIANS(:latitud)) * COS(RADIANS(l.latitud))
                       * COS(RADIANS(l.longitud) - RADIANS(:longitud))
                       + SIN(RADIANS(:latitud)) * SIN(RADIANS(l.latitud))
                   ))) <= :radioKm)
            """, nativeQuery = true)
    long contarConFiltros(
            @Param("q") String q,
            @Param("categoriaId") Long categoriaId,
            @Param("latitud") BigDecimal latitud,
            @Param("longitud") BigDecimal longitud,
            @Param("radioKm") BigDecimal radioKm,
            @Param("precioMin") BigDecimal precioMin,
            @Param("precioMax") BigDecimal precioMax,
            @Param("estado") String estado);
}
