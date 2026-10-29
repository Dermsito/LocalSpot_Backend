package com.example.demo.infrastructure.adapter.out.persistence.repository;

import com.example.demo.infrastructure.adapter.out.persistence.entity.TokenRecuperacionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRecuperacionJpaRepository extends JpaRepository<TokenRecuperacionJpaEntity, Long> {

    Optional<TokenRecuperacionJpaEntity> findByToken(String token);

    @Modifying
    @Query("update TokenRecuperacionJpaEntity t set t.usado = true "
            + "where t.idUsuario = :idUsuario and t.usado = false")
    int invalidarTokensDeUsuario(@Param("idUsuario") Long idUsuario);
}
