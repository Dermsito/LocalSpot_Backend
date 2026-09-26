package com.example.demo.infrastructure.config;

import com.example.demo.application.usecase.ActualizarPreferenciaUseCase;
import com.example.demo.application.usecase.ConsultarPreferenciasUsuarioUseCase;
import com.example.demo.application.usecase.CrearPreferenciaUseCase;
import com.example.demo.application.usecase.LoginUseCase;
import com.example.demo.application.usecase.RegistrarUsuarioUseCase;
import com.example.demo.domain.port.CategoriaRepositoryPort;
import com.example.demo.domain.port.JwtServicePort;
import com.example.demo.domain.port.PasswordEncoderPort;
import com.example.demo.domain.port.PreferenciaRepositoryPort;
import com.example.demo.domain.port.UsuarioRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public RegistrarUsuarioUseCase registrarUsuarioUseCase(UsuarioRepositoryPort usuarioRepositoryPort,
                                                           PasswordEncoderPort passwordEncoderPort) {
        return new RegistrarUsuarioUseCase(usuarioRepositoryPort, passwordEncoderPort);
    }

    @Bean
    public LoginUseCase loginUseCase(UsuarioRepositoryPort usuarioRepositoryPort,
                                     PasswordEncoderPort passwordEncoderPort,
                                     JwtServicePort jwtServicePort) {
        return new LoginUseCase(usuarioRepositoryPort, passwordEncoderPort, jwtServicePort);
    }

    @Bean
    public CrearPreferenciaUseCase crearPreferenciaUseCase(PreferenciaRepositoryPort preferenciaRepositoryPort,
                                                           UsuarioRepositoryPort usuarioRepositoryPort,
                                                           CategoriaRepositoryPort categoriaRepositoryPort) {
        return new CrearPreferenciaUseCase(preferenciaRepositoryPort, usuarioRepositoryPort, categoriaRepositoryPort);
    }

    @Bean
    public ActualizarPreferenciaUseCase actualizarPreferenciaUseCase(PreferenciaRepositoryPort preferenciaRepositoryPort) {
        return new ActualizarPreferenciaUseCase(preferenciaRepositoryPort);
    }

    @Bean
    public ConsultarPreferenciasUsuarioUseCase consultarPreferenciasUsuarioUseCase(
            PreferenciaRepositoryPort preferenciaRepositoryPort,
            UsuarioRepositoryPort usuarioRepositoryPort) {
        return new ConsultarPreferenciasUsuarioUseCase(preferenciaRepositoryPort, usuarioRepositoryPort);
    }
}
