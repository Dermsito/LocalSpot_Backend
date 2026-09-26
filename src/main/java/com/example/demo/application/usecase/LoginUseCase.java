package com.example.demo.application.usecase;

import com.example.demo.domain.exception.CredencialesInvalidasException;
import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.port.JwtServicePort;
import com.example.demo.domain.port.PasswordEncoderPort;
import com.example.demo.domain.port.UsuarioRepositoryPort;

public class LoginUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final JwtServicePort jwtServicePort;

    public LoginUseCase(UsuarioRepositoryPort usuarioRepositoryPort,
                        PasswordEncoderPort passwordEncoderPort,
                        JwtServicePort jwtServicePort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.jwtServicePort = jwtServicePort;
    }

    public String ejecutar(String correo, String contrasenaPlana) {
        Usuario usuario = usuarioRepositoryPort.findByCorreo(correo)
                .orElse(null);

        if (usuario == null) {
            throw new CredencialesInvalidasException("Credenciales inválidas");
        }

        if (!passwordEncoderPort.matches(contrasenaPlana, usuario.getContrasenaHash())) {
            throw new CredencialesInvalidasException("Credenciales inválidas");
        }

        return jwtServicePort.generateToken(usuario);
    }
}
