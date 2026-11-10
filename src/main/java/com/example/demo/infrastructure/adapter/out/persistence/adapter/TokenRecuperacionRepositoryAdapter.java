package com.example.demo.infrastructure.adapter.out.persistence.adapter;

import com.example.demo.domain.model.TokenRecuperacionContrasena;
import com.example.demo.domain.port.TokenRecuperacionRepositoryPort;
import com.example.demo.infrastructure.adapter.out.persistence.entity.TokenRecuperacionJpaEntity;
import com.example.demo.infrastructure.adapter.out.persistence.repository.TokenRecuperacionJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class TokenRecuperacionRepositoryAdapter implements TokenRecuperacionRepositoryPort {

    private final TokenRecuperacionJpaRepository tokenRecuperacionJpaRepository;

    public TokenRecuperacionRepositoryAdapter(TokenRecuperacionJpaRepository tokenRecuperacionJpaRepository) {
        this.tokenRecuperacionJpaRepository = tokenRecuperacionJpaRepository;
    }

    @Override
    public TokenRecuperacionContrasena save(TokenRecuperacionContrasena token) {
        TokenRecuperacionJpaEntity entity = toEntity(token);
        TokenRecuperacionJpaEntity saved = tokenRecuperacionJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<TokenRecuperacionContrasena> findByToken(String token) {
        return tokenRecuperacionJpaRepository.findByToken(token)
                .map(this::toDomain);
    }

    @Override
    @Transactional
    public void invalidarTokensDeUsuario(Long idUsuario) {
        tokenRecuperacionJpaRepository.invalidarTokensDeUsuario(idUsuario);
    }

    private TokenRecuperacionContrasena toDomain(TokenRecuperacionJpaEntity entity) {
        return new TokenRecuperacionContrasena(
                entity.getIdToken(),
                entity.getIdUsuario(),
                entity.getToken(),
                entity.getFechaCreacion(),
                entity.getFechaExpiracion(),
                Boolean.TRUE.equals(entity.getUsado())
        );
    }

    private TokenRecuperacionJpaEntity toEntity(TokenRecuperacionContrasena token) {
        TokenRecuperacionJpaEntity entity = new TokenRecuperacionJpaEntity();
        entity.setIdToken(token.getIdToken());
        entity.setIdUsuario(token.getIdUsuario());
        entity.setToken(token.getToken());
        entity.setFechaCreacion(token.getFechaCreacion());
        entity.setFechaExpiracion(token.getFechaExpiracion());
        entity.setUsado(token.isUsado());
        return entity;
    }
}
