package com.example.demo.application.usecase;

import com.example.demo.domain.exception.CorreoDuplicadoException;
import com.example.demo.domain.model.TokenVerificacionCorreo;
import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.port.EmailPort;
import com.example.demo.domain.port.PasswordEncoderPort;
import com.example.demo.domain.port.TokenVerificacionRepositoryPort;
import com.example.demo.domain.port.UsuarioRepositoryPort;

import java.time.LocalDateTime;

/**
 * RF-01: Registro de usuario con rol FINAL y verificacion de correo.
 * El usuario se crea sin verificar y se le envia un token por correo;
 * no podra iniciar sesion hasta confirmarlo.
 */
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
        if (usuarioRepositoryPort.existsByCorreo(correo)) {
            throw new CorreoDuplicadoException("El correo " + correo + " ya se encuentra registrado");
        }

        String hash = passwordEncoderPort.encode(contrasenaPlana);

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setContrasenaHash(hash);
        usuario.setTipoUsuario(tipoUsuario != null ? tipoUsuario : Usuario.TipoUsuario.FINAL);
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setVerificado(false);

        Usuario guardado = usuarioRepositoryPort.save(usuario);

        enviarCorreoVerificacion(guardado);
        return guardado;
    }

    private void enviarCorreoVerificacion(Usuario usuario) {
        TokenVerificacionCorreo token = TokenVerificacionCorreo.nuevo(usuario.getIdUsuario(), expiracionMinutos);
        tokenVerificacionRepositoryPort.save(token);
        emailPort.enviar(EmailVerificacionBuilder.construir(
                usuario.getNombre(), usuario.getCorreo(), token.getToken(), baseUrlVerificacion));
    }
}
