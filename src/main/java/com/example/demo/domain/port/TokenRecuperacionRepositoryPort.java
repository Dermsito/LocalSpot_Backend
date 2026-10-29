package com.example.demo.domain.port;

import com.example.demo.domain.model.TokenRecuperacionContrasena;

import java.util.Optional;

public interface TokenRecuperacionRepositoryPort {

    TokenRecuperacionContrasena save(TokenRecuperacionContrasena token);

    Optional<TokenRecuperacionContrasena> findByToken(String token);

    void invalidarTokensDeUsuario(Long idUsuario);
}
