package com.example.demo.application.usecase;

import com.example.demo.domain.model.TokenRecuperacionContrasena;
import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.port.EmailPort;
import com.example.demo.domain.port.TokenRecuperacionRepositoryPort;
import com.example.demo.domain.port.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitarRecuperacionContrasenaUseCaseTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private TokenRecuperacionRepositoryPort tokenRecuperacionRepositoryPort;

    @Mock
    private EmailPort emailPort;

    private SolicitarRecuperacionContrasenaUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new SolicitarRecuperacionContrasenaUseCase(
                usuarioRepositoryPort,
                tokenRecuperacionRepositoryPort,
                emailPort,
                60L,
                "http://localhost:3000/restablecer-contrasena"
        );
    }

    @Test
    @DisplayName("Debe generar token de recuperacion y enviar correo si el usuario existe y esta verificado")
    void debeGenerarTokenYEnviarCorreoParaUsuarioValido() {
        // Arrange
        String correo = "daniel@gmail.com";
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(10L);
        usuario.setNombre("Daniel");
        usuario.setCorreo(correo);
        usuario.setVerificado(true);

        when(usuarioRepositoryPort.findByCorreo(correo)).thenReturn(Optional.of(usuario));

        // Act
        useCase.ejecutar(correo);

        // Assert
        ArgumentCaptor<TokenRecuperacionContrasena> tokenCaptor = ArgumentCaptor.forClass(TokenRecuperacionContrasena.class);
        verify(tokenRecuperacionRepositoryPort).save(tokenCaptor.capture());
        TokenRecuperacionContrasena tokenGuardado = tokenCaptor.getValue();
        assertThat(tokenGuardado.getIdUsuario()).isEqualTo(10L);
        assertThat(tokenGuardado.getToken()).isNotBlank();

        verify(emailPort).enviar(any());
    }

    @Test
    @DisplayName("No debe fallar ni filtrar informacion si el correo no existe (anti-enumeration)")
    void noDebeFallarSiCorreoNoExiste() {
        // Arrange
        String correo = "desconocido@gmail.com";
        when(usuarioRepositoryPort.findByCorreo(correo)).thenReturn(Optional.empty());

        // Act
        useCase.ejecutar(correo);

        // Assert
        verify(tokenRecuperacionRepositoryPort, never()).save(any());
        verify(emailPort, never()).enviar(any());
    }
}
