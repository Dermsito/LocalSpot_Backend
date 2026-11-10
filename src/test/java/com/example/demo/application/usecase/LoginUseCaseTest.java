package com.example.demo.application.usecase;

import com.example.demo.domain.exception.CredencialesInvalidasException;
import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.port.JwtServicePort;
import com.example.demo.domain.port.PasswordEncoderPort;
import com.example.demo.domain.port.UsuarioRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @Mock
    private JwtServicePort jwtServicePort;

    @InjectMocks
    private LoginUseCase loginUseCase;

    @Test
    void ejecutar_cuandoCredencialesValidas_debeRetornarToken() {
        Usuario u = new Usuario();
        u.setIdUsuario(1L);
        u.setNombre("Test");
        u.setContrasenaHash("hash");
        u.setTipoUsuario(Usuario.TipoUsuario.ADMINISTRADOR);
        u.setVerificado(true);
        when(usuarioRepositoryPort.findByCorreo("admin@test.com")).thenReturn(Optional.of(u));
        when(passwordEncoderPort.matches("pass1234", "hash")).thenReturn(true);
        when(jwtServicePort.generateToken(u)).thenReturn("jwt-token");
        when(jwtServicePort.getExpirationMs()).thenReturn(86400000L);

        LoginUseCase.ResultadoLogin resultado = loginUseCase.ejecutar("admin@test.com", "pass1234");

        assertThat(resultado.token()).isEqualTo("jwt-token");
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.rol()).isEqualTo("ADMINISTRADOR");
    }

    @Test
    void ejecutar_cuandoCorreoSinVerificar_debePermitirElLogin() {
        Usuario u = new Usuario();
        u.setIdUsuario(2L);
        u.setNombre("Nuevo");
        u.setContrasenaHash("hash");
        u.setTipoUsuario(Usuario.TipoUsuario.FINAL);
        u.setVerificado(false);
        when(usuarioRepositoryPort.findByCorreo("nuevo@test.com")).thenReturn(Optional.of(u));
        when(passwordEncoderPort.matches("pass1234", "hash")).thenReturn(true);
        when(jwtServicePort.generateToken(u)).thenReturn("jwt-token");
        when(jwtServicePort.getExpirationMs()).thenReturn(86400000L);

        LoginUseCase.ResultadoLogin resultado = loginUseCase.ejecutar("nuevo@test.com", "pass1234");

        assertThat(resultado.token()).isEqualTo("jwt-token");
        assertThat(resultado.id()).isEqualTo(2L);
        assertThat(resultado.rol()).isEqualTo("FINAL");
    }

    @Test
    void ejecutar_cuandoUsuarioNoExiste_debeLanzarExcepcion() {
        when(usuarioRepositoryPort.findByCorreo("no@existe.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginUseCase.ejecutar("no@existe.com", "pass"))
                .isInstanceOf(CredencialesInvalidasException.class);
    }

    @Test
    void ejecutar_cuandoContrasenaIncorrecta_debeLanzarExcepcion() {
        Usuario u = new Usuario();
        u.setContrasenaHash("hash");
        when(usuarioRepositoryPort.findByCorreo("test@test.com")).thenReturn(Optional.of(u));
        when(passwordEncoderPort.matches("wrong", "hash")).thenReturn(false);

        assertThatThrownBy(() -> loginUseCase.ejecutar("test@test.com", "wrong"))
                .isInstanceOf(CredencialesInvalidasException.class);
    }
}
