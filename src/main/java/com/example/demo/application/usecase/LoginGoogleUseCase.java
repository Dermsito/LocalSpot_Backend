package com.example.demo.application.usecase;

import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.port.GoogleTokenVerifierPort;
import com.example.demo.domain.port.GoogleTokenVerifierPort.PerfilGoogle;
import com.example.demo.domain.port.JwtServicePort;
import com.example.demo.domain.port.UsuarioRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Caso de uso para iniciar sesion o registrarse automaticamente con Google (Google Sign-In).
 * Cumple con SRP y DIP desacoplando la verificacion HTTP a traves de GoogleTokenVerifierPort.
 */
public class LoginGoogleUseCase {

    private static final Logger log = LoggerFactory.getLogger(LoginGoogleUseCase.class);

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final JwtServicePort jwtServicePort;
    private final GoogleTokenVerifierPort googleTokenVerifierPort;

    public LoginGoogleUseCase(
            UsuarioRepositoryPort usuarioRepositoryPort,
            JwtServicePort jwtServicePort,
            GoogleTokenVerifierPort googleTokenVerifierPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.jwtServicePort = jwtServicePort;
        this.googleTokenVerifierPort = googleTokenVerifierPort;
    }

    public LoginUseCase.ResultadoLogin ejecutar(String idToken) {
        PerfilGoogle perfil = googleTokenVerifierPort.verify(idToken);

        Usuario usuario = usuarioRepositoryPort.findByCorreo(perfil.email())
                .orElseGet(() -> registrarUsuarioGoogle(perfil));

        if (!usuario.isVerificado() && perfil.emailVerified()) {
            usuario.setVerificado(true);
            usuario.setFechaVerificacion(LocalDateTime.now());
            usuarioRepositoryPort.save(usuario);
        }

        String token = jwtServicePort.generateToken(usuario);
        String rol = usuario.getTipoUsuario() != null ? usuario.getTipoUsuario().name() : "FINAL";

        return new LoginUseCase.ResultadoLogin(
                token,
                jwtServicePort.getExpirationMs(),
                usuario.getIdUsuario(),
                usuario.getNombre(),
                rol
        );
    }

    private Usuario registrarUsuarioGoogle(PerfilGoogle perfil) {
        log.info("Creando nuevo usuario a traves de Google Sign-In: {}", perfil.email());
        Usuario nuevo = new Usuario();
        nuevo.setNombre(perfil.name() != null ? perfil.name() : "Usuario Google");
        nuevo.setCorreo(perfil.email());
        nuevo.setContrasenaHash("{noop}GOOGLE_OAUTH_" + UUID.randomUUID());
        nuevo.setTipoUsuario(Usuario.TipoUsuario.FINAL);
        nuevo.setFechaRegistro(LocalDateTime.now());
        nuevo.setVerificado(perfil.emailVerified());
        if (perfil.emailVerified()) {
            nuevo.setFechaVerificacion(LocalDateTime.now());
        }

        return usuarioRepositoryPort.save(nuevo);
    }
}
