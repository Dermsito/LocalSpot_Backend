package com.example.demo.application.usecase;

import com.example.demo.domain.exception.TokenVerificacionInvalidoException;
import com.example.demo.domain.model.TokenVerificacionCorreo;
import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.port.TokenVerificacionRepositoryPort;
import com.example.demo.domain.port.UsuarioRepositoryPort;

import java.time.LocalDateTime;

/**
 * Confirma el correo de un usuario a partir del token recibido por correo.
 */
public class VerificarCorreoUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final TokenVerificacionRepositoryPort tokenVerificacionRepositoryPort;

    public VerificarCorreoUseCase(UsuarioRepositoryPort usuarioRepositoryPort,
                                  TokenVerificacionRepositoryPort tokenVerificacionRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.tokenVerificacionRepositoryPort = tokenVerificacionRepositoryPort;
    }

    public void ejecutar(String token) {
        TokenVerificacionCorreo tokenVerificacion = tokenVerificacionRepositoryPort.findByToken(token)
                .orElseThrow(() -> new TokenVerificacionInvalidoException(
                        "El enlace de verificacion no es valido"));

        if (!tokenVerificacion.estaVigente(LocalDateTime.now())) {
            throw new TokenVerificacionInvalidoException(
                    "El enlace de verificacion ya fue usado o ha expirado");
        }

        Usuario usuario = usuarioRepositoryPort.findById(tokenVerificacion.getIdUsuario())
                .orElseThrow(() -> new TokenVerificacionInvalidoException(
                        "El enlace de verificacion no es valido"));

        usuario.setVerificado(true);
        usuario.setFechaVerificacion(LocalDateTime.now());
        usuarioRepositoryPort.save(usuario);

        tokenVerificacion.setUsado(true);
        tokenVerificacionRepositoryPort.save(tokenVerificacion);
    }
}
