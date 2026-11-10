package com.example.demo.application.usecase;

import com.example.demo.domain.model.TokenRecuperacionContrasena;
import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.port.EmailPort;
import com.example.demo.domain.port.TokenRecuperacionRepositoryPort;
import com.example.demo.domain.port.UsuarioRepositoryPort;

/**
 * Solicita el restablecimiento de contrasena generando un token y enviando
 * el correo correspondiente.
 */
public class SolicitarRecuperacionContrasenaUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final TokenRecuperacionRepositoryPort tokenRecuperacionRepositoryPort;
    private final EmailPort emailPort;
    private final long expiracionMinutos;
    private final String baseUrlRecuperacion;

    public SolicitarRecuperacionContrasenaUseCase(UsuarioRepositoryPort usuarioRepositoryPort,
                                                TokenRecuperacionRepositoryPort tokenRecuperacionRepositoryPort,
                                                EmailPort emailPort,
                                                long expiracionMinutos,
                                                String baseUrlRecuperacion) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.tokenRecuperacionRepositoryPort = tokenRecuperacionRepositoryPort;
        this.emailPort = emailPort;
        this.expiracionMinutos = expiracionMinutos;
        this.baseUrlRecuperacion = baseUrlRecuperacion;
    }

    public void ejecutar(String correo) {
        if (correo == null || correo.isBlank()) {
            return;
        }

        Usuario usuario = usuarioRepositoryPort.findByCorreo(correo.trim().toLowerCase()).orElse(null);
        if (usuario == null) {
            return;
        }

        tokenRecuperacionRepositoryPort.invalidarTokensDeUsuario(usuario.getIdUsuario());

        TokenRecuperacionContrasena token = TokenRecuperacionContrasena.nuevo(usuario.getIdUsuario(), expiracionMinutos);
        tokenRecuperacionRepositoryPort.save(token);

        emailPort.enviar(EmailRecuperacionBuilder.construir(
                usuario.getNombre(), usuario.getCorreo(), token.getToken(), baseUrlRecuperacion));
    }
}
