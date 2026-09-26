package com.example.demo.domain.port;

import com.example.demo.domain.model.Usuario;

public interface JwtServicePort {
    String generateToken(Usuario usuario);
    Long extractUsuarioId(String token);
    boolean isTokenValid(String token);
}
