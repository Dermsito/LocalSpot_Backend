package com.example.demo.infrastructure.adapter.out.security;

import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.port.JwtServicePort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtServiceAdapter implements JwtServicePort {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtServiceAdapter(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    @Override
    public String generateToken(Usuario usuario) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(String.valueOf(usuario.getIdUsuario()))
                .claim("correo", usuario.getCorreo())
                .claim("tipoUsuario", usuario.getTipoUsuario() != null ? usuario.getTipoUsuario().name() : null)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    @Override
    public Long extractUsuarioId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.valueOf(claims.getSubject());
    }

    @Override
    public String extractTipoUsuario(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("tipoUsuario", String.class);
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public long getExpirationMs() {
        return expirationMs;
    }
}
