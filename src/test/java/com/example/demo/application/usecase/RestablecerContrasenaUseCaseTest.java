package com.example.demo.application.usecase;

import com.example.demo.domain.exception.TokenRecuperacionInvalidoException;
import com.example.demo.domain.model.TokenRecuperacionContrasena;
import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.port.PasswordEncoderPort;
import com.example.demo.domain.port.TokenRecuperacionRepositoryPort;
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
class RestablecerContrasenaUseCaseTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private TokenRecuperacionRepositoryPort tokenRecuperacionRepositoryPort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @InjectMocks
    private RestablecerContrasenaUseCase useCase;

    @Test
    void ejecutar_tokenInvalidoOBlanco_debeLanzarExcepcion() {
        assertThatThrownBy(() -> useCase.ejecutar(null, "123456"))
                .isInstanceOf(TokenRecuperacionInvalidoException.class);
        assertThatThrownBy(() -> useCase.ejecutar("  ", "123456"))
                .isInstanceOf(TokenRecuperacionInvalidoException.class);
    }

    @Test
    void ejecutar_contrasenaCorta_debeLanzarExcepcion() {
        assertThatThrownBy(() -> useCase.ejecutar("token123", "123"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void ejecutar_tokenNoExiste_debeLanzarExcepcion() {
        when(tokenRecuperacionRepositoryPort.findByToken("tok")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.ejecutar("tok", "nuevaclave"))
                .isInstanceOf(TokenRecuperacionInvalidoException.class);
    }

    @Test
    void ejecutar_tokenExpirado_debeLanzarExcepcion() {
        TokenRecuperacionContrasena token = new TokenRecuperacionContrasena();
        token.setFechaExpiracion(LocalDateTime.now().minusMinutes(10));
        when(tokenRecuperacionRepositoryPort.findByToken("tok")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> useCase.ejecutar("tok", "nuevaclave"))
                .isInstanceOf(TokenRecuperacionInvalidoException.class);
    }

    @Test
    void ejecutar_usuarioAsociadoNoExiste_debeLanzarExcepcion() {
        TokenRecuperacionContrasena token = new TokenRecuperacionContrasena();
        token.setIdUsuario(99L);
        token.setFechaExpiracion(LocalDateTime.now().plusMinutes(10));
        token.setUsado(false);

        when(tokenRecuperacionRepositoryPort.findByToken("tok")).thenReturn(Optional.of(token));
        when(usuarioRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.ejecutar("tok", "nuevaclave"))
                .isInstanceOf(TokenRecuperacionInvalidoException.class);
    }

    @Test
    void ejecutar_exitoso() {
        TokenRecuperacionContrasena token = new TokenRecuperacionContrasena();
        token.setIdUsuario(1L);
        token.setFechaExpiracion(LocalDateTime.now().plusMinutes(10));
        token.setUsado(false);

        Usuario u = new Usuario();
        u.setIdUsuario(1L);

        when(tokenRecuperacionRepositoryPort.findByToken("tok")).thenReturn(Optional.of(token));
        when(usuarioRepositoryPort.findById(1L)).thenReturn(Optional.of(u));
        when(passwordEncoderPort.encode("nuevaclave")).thenReturn("hashClave");

        useCase.ejecutar("tok", "nuevaclave");

        assertThat(u.getContrasenaHash()).isEqualTo("hashClave");
        assertThat(token.isUsado()).isTrue();
        verify(usuarioRepositoryPort).save(u);
        verify(tokenRecuperacionRepositoryPort).save(token);
    }
}
