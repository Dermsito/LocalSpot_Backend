package com.example.demo.application.usecase;

import com.example.demo.domain.exception.TokenRecuperacionInvalidoException;
import com.example.demo.domain.model.TokenRecuperacionContrasena;
import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.port.PasswordEncoderPort;
import com.example.demo.domain.port.TokenRecuperacionRepositoryPort;
import com.example.demo.domain.port.UsuarioRepositoryPort;

import java.time.LocalDateTime;

/**
 * Restablece la contrasena de un usuario a partir de un token de recuperacion valido.
 */
public class RestablecerContrasenaUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final TokenRecuperacionRepositoryPort tokenRecuperacionRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;

    public RestablecerContrasenaUseCase(UsuarioRepositoryPort usuarioRepositoryPort,
                                        TokenRecuperacionRepositoryPort tokenRecuperacionRepositoryPort,
                                        PasswordEncoderPort passwordEncoderPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.tokenRecuperacionRepositoryPort = tokenRecuperacionRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    public void ejecutar(String token, String nuevaContrasena) {
        if (token == null || token.isBlank()) {
            throw new TokenRecuperacionInvalidoException("El token es obligatorio");
        }
        if (nuevaContrasena == null || nuevaContrasena.trim().length() < 6) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 6 caracteres");
        }

        TokenRecuperacionContrasena tokenRecuperacion = tokenRecuperacionRepositoryPort.findByToken(token)
                .orElseThrow(() -> new TokenRecuperacionInvalidoException(
                        "El enlace de recuperación no es válido"));

        if (!tokenRecuperacion.estaVigente(LocalDateTime.now())) {
            throw new TokenRecuperacionInvalidoException(
                    "El enlace de recuperación ya fue usado o ha expirado");
        }

        Usuario usuario = usuarioRepositoryPort.findById(tokenRecuperacion.getIdUsuario())
                .orElseThrow(() -> new TokenRecuperacionInvalidoException(
                        "El usuario asociado al token no existe"));

        usuario.setContrasenaHash(passwordEncoderPort.encode(nuevaContrasena.trim()));
        usuarioRepositoryPort.save(usuario);

        tokenRecuperacion.setUsado(true);
        tokenRecuperacionRepositoryPort.save(tokenRecuperacion);
    }
}
