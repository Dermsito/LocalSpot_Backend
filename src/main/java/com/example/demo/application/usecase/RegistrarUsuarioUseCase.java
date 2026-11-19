package com.example.demo.application.usecase;

import com.example.demo.domain.exception.CorreoDuplicadoException;
import com.example.demo.domain.model.TokenVerificacionCorreo;
import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.port.EmailPort;
import com.example.demo.domain.port.PasswordEncoderPort;
import com.example.demo.domain.port.TokenVerificacionRepositoryPort;
import com.example.demo.domain.port.UsuarioRepositoryPort;

import java.time.LocalDateTime;

public class RegistrarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final TokenVerificacionRepositoryPort tokenVerificacionRepositoryPort;
    private final EmailPort emailPort;
    private final long expiracionMinutos;
    private final String baseUrlVerificacion;

    public RegistrarUsuarioUseCase(UsuarioRepositoryPort usuarioRepositoryPort,
                                   PasswordEncoderPort passwordEncoderPort,
                                   TokenVerificacionRepositoryPort tokenVerificacionRepositoryPort,
                                   EmailPort emailPort,
                                   long expiracionMinutos,
                                   String baseUrlVerificacion) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.tokenVerificacionRepositoryPort = tokenVerificacionRepositoryPort;
        this.emailPort = emailPort;
        this.expiracionMinutos = expiracionMinutos;
        this.baseUrlVerificacion = baseUrlVerificacion;
    }

    public Usuario ejecutar(String nombre, String correo, String contrasenaPlana,
                            Usuario.TipoUsuario tipoUsuario) {
        String correoLimpio = correo != null ? correo.trim().toLowerCase() : "";
        if (usuarioRepositoryPort.existsByCorreo(correoLimpio)) {
            throw new CorreoDuplicadoException("El correo " + correoLimpio + " ya se encuentra registrado");
        }

        String hash = passwordEncoderPort.encode(contrasenaPlana);

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre != null ? nombre.trim() : "");
        usuario.setCorreo(correoLimpio);
        usuario.setContrasenaHash(hash);
        usuario.setTipoUsuario(tipoUsuario != null ? tipoUsuario : Usuario.TipoUsuario.FINAL);
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setVerificado(true);

        Usuario guardado = usuarioRepositoryPort.save(usuario);

        try {
            enviarCorreoVerificacion(guardado);
        } catch (Exception ignored) {
            // Si el servidor SMTP no está disponible, el usuario igual queda activo en PostgreSQL
        }
        return guardado;
    }

    private void enviarCorreoVerificacion(Usuario usuario) {
        TokenVerificacionCorreo token = TokenVerificacionCorreo.nuevo(usuario.getIdUsuario(), expiracionMinutos);
        tokenVerificacionRepositoryPort.save(token);
        emailPort.enviar(EmailVerificacionBuilder.construir(
                usuario.getNombre(), usuario.getCorreo(), token.getToken(), baseUrlVerificacion));
    }
}