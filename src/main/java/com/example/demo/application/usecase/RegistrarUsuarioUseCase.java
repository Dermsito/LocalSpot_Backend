package com.example.demo.application.usecase;

import com.example.demo.domain.exception.CorreoDuplicadoException;
import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.port.PasswordEncoderPort;
import com.example.demo.domain.port.UsuarioRepositoryPort;

import java.time.LocalDate;

public class RegistrarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;

    public RegistrarUsuarioUseCase(UsuarioRepositoryPort usuarioRepositoryPort,
                                   PasswordEncoderPort passwordEncoderPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
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
        usuario.setFechaRegistro(LocalDate.now());

        return usuarioRepositoryPort.save(usuario);
    }
}
