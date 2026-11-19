package com.example.demo.application.usecase;

import com.example.demo.domain.model.TokenRecuperacionContrasena;
import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.port.EmailPort;
import com.example.demo.domain.port.TokenRecuperacionRepositoryPort;
import com.example.demo.domain.port.UsuarioRepositoryPort;

import java.util.concurrent.CompletableFuture;

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

    public String ejecutar(String correo) {
        if (correo == null || correo.isBlank()) {
            return null;
        }

        Usuario usuario = usuarioRepositoryPort.findByCorreo(correo.trim().toLowerCase()).orElse(null);
        if (usuario == null) {
            return null;
        }

        tokenRecuperacionRepositoryPort.invalidarTokensDeUsuario(usuario.getIdUsuario());

        TokenRecuperacionContrasena token = TokenRecuperacionContrasena.nuevo(usuario.getIdUsuario(), expiracionMinutos);
        tokenRecuperacionRepositoryPort.save(token);

        final String nombre = usuario.getNombre();
        final String destino = usuario.getCorreo();
        final String valorToken = token.getToken();

        CompletableFuture.runAsync(() -> {
            try {
                emailPort.enviar(EmailRecuperacionBuilder.construir(
                        nombre, destino, valorToken, baseUrlRecuperacion));
            } catch (Exception ignored) {
            }
        });

        return valorToken;
    }
}