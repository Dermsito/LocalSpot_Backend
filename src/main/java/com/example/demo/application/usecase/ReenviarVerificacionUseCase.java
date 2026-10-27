package com.example.demo.application.usecase;

import com.example.demo.domain.model.TokenVerificacionCorreo;
import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.port.EmailPort;
import com.example.demo.domain.port.TokenVerificacionRepositoryPort;
import com.example.demo.domain.port.UsuarioRepositoryPort;

/**
 * Reenvia el correo de verificacion. Si el correo no existe o el usuario ya
 * esta verificado no hace nada: el endpoint responde siempre lo mismo para
 * no revelar si una cuenta existe.
 */
public class ReenviarVerificacionUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final TokenVerificacionRepositoryPort tokenVerificacionRepositoryPort;
    private final EmailPort emailPort;
    private final long expiracionMinutos;
    private final String baseUrlVerificacion;

    public ReenviarVerificacionUseCase(UsuarioRepositoryPort usuarioRepositoryPort,
                                       TokenVerificacionRepositoryPort tokenVerificacionRepositoryPort,
                                       EmailPort emailPort,
                                       long expiracionMinutos,
                                       String baseUrlVerificacion) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.tokenVerificacionRepositoryPort = tokenVerificacionRepositoryPort;
        this.emailPort = emailPort;
        this.expiracionMinutos = expiracionMinutos;
        this.baseUrlVerificacion = baseUrlVerificacion;
    }

    public void ejecutar(String correo) {
        Usuario usuario = usuarioRepositoryPort.findByCorreo(correo).orElse(null);
        if (usuario == null || usuario.isVerificado()) {
            return;
        }

        tokenVerificacionRepositoryPort.invalidarTokensDeUsuario(usuario.getIdUsuario());

        TokenVerificacionCorreo token = TokenVerificacionCorreo.nuevo(usuario.getIdUsuario(), expiracionMinutos);
        tokenVerificacionRepositoryPort.save(token);
        emailPort.enviar(EmailVerificacionBuilder.construir(
                usuario.getNombre(), usuario.getCorreo(), token.getToken(), baseUrlVerificacion));
    }
}
