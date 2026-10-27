package com.example.demo.domain.port;

import com.example.demo.domain.model.TokenVerificacionCorreo;

import java.util.Optional;

public interface TokenVerificacionRepositoryPort {

    TokenVerificacionCorreo save(TokenVerificacionCorreo token);

    Optional<TokenVerificacionCorreo> findByToken(String token);

    /**
     * Marca como usados todos los tokens vigentes del usuario,
     * para que al reenviar la verificacion solo sirva el token mas reciente.
     */
    void invalidarTokensDeUsuario(Long idUsuario);
}
