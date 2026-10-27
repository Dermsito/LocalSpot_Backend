package com.example.demo.infrastructure.adapter.out.persistence.repository;

import com.example.demo.infrastructure.adapter.out.persistence.entity.VerificacionCorreoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificacionCorreoJpaRepository extends JpaRepository<VerificacionCorreoJpaEntity, Long> {

    Optional<VerificacionCorreoJpaEntity> findByToken(String token);

    @Modifying
    @Query("update VerificacionCorreoJpaEntity v set v.usado = true "
            + "where v.idUsuario = :idUsuario and v.usado = false")
    int invalidarTokensDeUsuario(@Param("idUsuario") Long idUsuario);
}
