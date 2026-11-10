package com.example.demo.application.usecase;

import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.port.GoogleTokenVerifierPort;
import com.example.demo.domain.port.GoogleTokenVerifierPort.PerfilGoogle;
import com.example.demo.domain.port.JwtServicePort;
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
class LoginGoogleUseCaseTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private JwtServicePort jwtServicePort;

    @Mock
    private GoogleTokenVerifierPort googleTokenVerifierPort;

    private LoginGoogleUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new LoginGoogleUseCase(usuarioRepositoryPort, jwtServicePort, googleTokenVerifierPort);
    }

    @Test
    @DisplayName("Debe autenticar y emitir JWT si el usuario de Google ya existe")
    void debeAutenticarUsuarioGoogleExistente() {
        // Arrange
        String idToken = "valid.google.token";
        PerfilGoogle perfil = new PerfilGoogle("daniel@gmail.com", "Daniel", true);
        when(googleTokenVerifierPort.verify(idToken)).thenReturn(perfil);

        Usuario existente = new Usuario();
        existente.setIdUsuario(109L);
        existente.setNombre("Daniel");
        existente.setCorreo("daniel@gmail.com");
        existente.setTipoUsuario(Usuario.TipoUsuario.FINAL);
        existente.setVerificado(true);

        when(usuarioRepositoryPort.findByCorreo("daniel@gmail.com")).thenReturn(Optional.of(existente));
        when(jwtServicePort.generateToken(existente)).thenReturn("jwt.token.simulado");
        when(jwtServicePort.getExpirationMs()).thenReturn(86400000L);

        // Act
        LoginUseCase.ResultadoLogin resultado = useCase.ejecutar(idToken);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.token()).isEqualTo("jwt.token.simulado");
        assertThat(resultado.id()).isEqualTo(109L);
        assertThat(resultado.nombre()).isEqualTo("Daniel");
        assertThat(resultado.rol()).isEqualTo("FINAL");

        verify(usuarioRepositoryPort, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe registrar automaticamente al usuario si inicia sesion por primera vez con Google")
    void debeRegistrarNuevoUsuarioGoogle() {
        // Arrange
        String idToken = "new.user.token";
        PerfilGoogle perfil = new PerfilGoogle("nuevo@gmail.com", "Usuario Nuevo", true);
        when(googleTokenVerifierPort.verify(idToken)).thenReturn(perfil);

        when(usuarioRepositoryPort.findByCorreo("nuevo@gmail.com")).thenReturn(Optional.empty());

        Usuario guardado = new Usuario();
        guardado.setIdUsuario(200L);
        guardado.setNombre("Usuario Nuevo");
        guardado.setCorreo("nuevo@gmail.com");
        guardado.setTipoUsuario(Usuario.TipoUsuario.FINAL);
        guardado.setVerificado(true);

        when(usuarioRepositoryPort.save(any(Usuario.class))).thenReturn(guardado);
        when(jwtServicePort.generateToken(guardado)).thenReturn("jwt.nuevo.token");
        when(jwtServicePort.getExpirationMs()).thenReturn(86400000L);

        // Act
        LoginUseCase.ResultadoLogin resultado = useCase.ejecutar(idToken);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(200L);
        assertThat(resultado.token()).isEqualTo("jwt.nuevo.token");

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepositoryPort).save(captor.capture());
        Usuario capturado = captor.getValue();
        assertThat(capturado.getCorreo()).isEqualTo("nuevo@gmail.com");
        assertThat(capturado.getNombre()).isEqualTo("Usuario Nuevo");
        assertThat(capturado.isVerificado()).isTrue();
    }
}
