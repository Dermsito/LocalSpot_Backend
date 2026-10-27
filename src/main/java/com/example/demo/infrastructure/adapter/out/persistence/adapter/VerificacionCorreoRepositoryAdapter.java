package com.example.demo.infrastructure.adapter.out.persistence.adapter;

import com.example.demo.domain.model.TokenVerificacionCorreo;
import com.example.demo.domain.port.TokenVerificacionRepositoryPort;
import com.example.demo.infrastructure.adapter.out.persistence.entity.VerificacionCorreoJpaEntity;
import com.example.demo.infrastructure.adapter.out.persistence.repository.VerificacionCorreoJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class VerificacionCorreoRepositoryAdapter implements TokenVerificacionRepositoryPort {

    private final VerificacionCorreoJpaRepository verificacionCorreoJpaRepository;

    public VerificacionCorreoRepositoryAdapter(VerificacionCorreoJpaRepository verificacionCorreoJpaRepository) {
        this.verificacionCorreoJpaRepository = verificacionCorreoJpaRepository;
    }

    @Override
    public TokenVerificacionCorreo save(TokenVerificacionCorreo token) {
        VerificacionCorreoJpaEntity entity = toEntity(token);
        VerificacionCorreoJpaEntity saved = verificacionCorreoJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<TokenVerificacionCorreo> findByToken(String token) {
        return verificacionCorreoJpaRepository.findByToken(token)
                .map(this::toDomain);
    }

    @Override
    @Transactional
    public void invalidarTokensDeUsuario(Long idUsuario) {
        verificacionCorreoJpaRepository.invalidarTokensDeUsuario(idUsuario);
    }

    private TokenVerificacionCorreo toDomain(VerificacionCorreoJpaEntity entity) {
        return new TokenVerificacionCorreo(
                entity.getIdToken(),
                entity.getIdUsuario(),
                entity.getToken(),
                entity.getFechaCreacion(),
                entity.getFechaExpiracion(),
                Boolean.TRUE.equals(entity.getUsado())
        );
    }

    private VerificacionCorreoJpaEntity toEntity(TokenVerificacionCorreo token) {
        VerificacionCorreoJpaEntity entity = new VerificacionCorreoJpaEntity();
        entity.setIdToken(token.getIdToken());
        entity.setIdUsuario(token.getIdUsuario());
        entity.setToken(token.getToken());
        entity.setFechaCreacion(token.getFechaCreacion());
        entity.setFechaExpiracion(token.getFechaExpiracion());
        entity.setUsado(token.isUsado());
        return entity;
    }
}
