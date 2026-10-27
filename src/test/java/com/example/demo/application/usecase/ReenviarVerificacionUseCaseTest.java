package com.example.demo.application.usecase;

import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.port.EmailPort;
import com.example.demo.domain.port.TokenVerificacionRepositoryPort;
import com.example.demo.domain.port.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReenviarVerificacionUseCaseTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private TokenVerificacionRepositoryPort tokenVerificacionRepositoryPort;

    @Mock
    private EmailPort emailPort;

    private ReenviarVerificacionUseCase reenviarVerificacionUseCase;

    @BeforeEach
    void setUp() {
        reenviarVerificacionUseCase = new ReenviarVerificacionUseCase(usuarioRepositoryPort,
                tokenVerificacionRepositoryPort, emailPort, 60, "http://localhost:3000");
    }

    private Usuario usuario(boolean verificado) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(9L);
        usuario.setNombre("Test");
        usuario.setCorreo("test@test.com");
        usuario.setVerificado(verificado);
        return usuario;
    }

    @Test
    void ejecutar_cuandoUsuarioSinVerificar_debeInvalidarTokensYReenviarCorreo() {
        when(usuarioRepositoryPort.findByCorreo("test@test.com")).thenReturn(Optional.of(usuario(false)));

        reenviarVerificacionUseCase.ejecutar("test@test.com");

        verify(tokenVerificacionRepositoryPort).invalidarTokensDeUsuario(9L);
        verify(tokenVerificacionRepositoryPort).save(any());
        verify(emailPort).enviar(any());
    }

    @Test
    void ejecutar_cuandoUsuarioYaVerificado_noDebeEnviarCorreo() {
        when(usuarioRepositoryPort.findByCorreo("test@test.com")).thenReturn(Optional.of(usuario(true)));

        reenviarVerificacionUseCase.ejecutar("test@test.com");

        verify(tokenVerificacionRepositoryPort, never()).save(any());
        verify(emailPort, never()).enviar(any());
    }

    @Test
    void ejecutar_cuandoCorreoNoExiste_noDebeEnviarCorreo() {
        when(usuarioRepositoryPort.findByCorreo("no@existe.com")).thenReturn(Optional.empty());

        reenviarVerificacionUseCase.ejecutar("no@existe.com");

        verify(tokenVerificacionRepositoryPort, never()).invalidarTokensDeUsuario(anyLong());
        verify(emailPort, never()).enviar(any());
    }
}
