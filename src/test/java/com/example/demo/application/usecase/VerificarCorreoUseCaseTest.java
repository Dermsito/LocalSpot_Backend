package com.example.demo.application.usecase;

import com.example.demo.domain.exception.TokenVerificacionInvalidoException;
import com.example.demo.domain.model.TokenVerificacionCorreo;
import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.port.TokenVerificacionRepositoryPort;
import com.example.demo.domain.port.UsuarioRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificarCorreoUseCaseTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private TokenVerificacionRepositoryPort tokenVerificacionRepositoryPort;

    @InjectMocks
    private VerificarCorreoUseCase verificarCorreoUseCase;

    private TokenVerificacionCorreo token(boolean usado, LocalDateTime expiracion) {
        return new TokenVerificacionCorreo(1L, 5L, "token-abc",
                LocalDateTime.now().minusMinutes(1), expiracion, usado);
    }

    @Test
    void ejecutar_cuandoTokenVigente_debeMarcarUsuarioVerificado() {
        when(tokenVerificacionRepositoryPort.findByToken("token-abc"))
                .thenReturn(Optional.of(token(false, LocalDateTime.now().plusHours(1))));
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(5L);
        usuario.setVerificado(false);
        when(usuarioRepositoryPort.findById(5L)).thenReturn(Optional.of(usuario));

        verificarCorreoUseCase.ejecutar("token-abc");

        assertThat(usuario.isVerificado()).isTrue();
        assertThat(usuario.getFechaVerificacion()).isNotNull();
        verify(usuarioRepositoryPort).save(usuario);
        verify(tokenVerificacionRepositoryPort).save(argThat(TokenVerificacionCorreo::isUsado));
    }

    @Test
    void ejecutar_cuandoTokenNoExiste_debeLanzarExcepcion() {
        when(tokenVerificacionRepositoryPort.findByToken("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> verificarCorreoUseCase.ejecutar("inexistente"))
                .isInstanceOf(TokenVerificacionInvalidoException.class);
        verify(usuarioRepositoryPort, never()).save(any());
    }

    @Test
    void ejecutar_cuandoTokenExpirado_debeLanzarExcepcion() {
        when(tokenVerificacionRepositoryPort.findByToken("token-abc"))
                .thenReturn(Optional.of(token(false, LocalDateTime.now().minusMinutes(5))));

        assertThatThrownBy(() -> verificarCorreoUseCase.ejecutar("token-abc"))
                .isInstanceOf(TokenVerificacionInvalidoException.class);
        verify(usuarioRepositoryPort, never()).save(any());
    }

    @Test
    void ejecutar_cuandoTokenYaUsado_debeLanzarExcepcion() {
        when(tokenVerificacionRepositoryPort.findByToken("token-abc"))
                .thenReturn(Optional.of(token(true, LocalDateTime.now().plusHours(1))));

        assertThatThrownBy(() -> verificarCorreoUseCase.ejecutar("token-abc"))
                .isInstanceOf(TokenVerificacionInvalidoException.class);
    }
}
