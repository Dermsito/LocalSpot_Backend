package com.example.demo.application.usecase;

import com.example.demo.domain.exception.CorreoDuplicadoException;
import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.port.PasswordEncoderPort;
import com.example.demo.domain.port.UsuarioRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrarUsuarioUseCaseTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @InjectMocks
    private RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @Test
    void ejecutar_cuandoCorreoNoExiste_debeGuardarConHash() {
        when(usuarioRepositoryPort.existsByCorreo("test@test.com")).thenReturn(false);
        when(passwordEncoderPort.encode("pass1234")).thenReturn("hash");
        when(usuarioRepositoryPort.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario u = registrarUsuarioUseCase.ejecutar("Test", "test@test.com", "pass1234", null);

        assertThat(u.getNombre()).isEqualTo("Test");
        assertThat(u.getCorreo()).isEqualTo("test@test.com");
        assertThat(u.getContrasenaHash()).isEqualTo("hash");
        assertThat(u.getTipoUsuario()).isEqualTo(Usuario.TipoUsuario.FINAL);
    }

    @Test
    void ejecutar_cuandoCorreoExiste_debeLanzarExcepcion() {
        when(usuarioRepositoryPort.existsByCorreo("test@test.com")).thenReturn(true);

        assertThatThrownBy(() -> registrarUsuarioUseCase.ejecutar("Test", "test@test.com", "pass1234", null))
                .isInstanceOf(CorreoDuplicadoException.class);
        verify(usuarioRepositoryPort, never()).save(any());
    }
}
