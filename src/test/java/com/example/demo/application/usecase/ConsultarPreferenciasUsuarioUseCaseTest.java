package com.example.demo.application.usecase;

import com.example.demo.domain.exception.UsuarioNoEncontradoException;
import com.example.demo.domain.model.Preferencia;
import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.port.PreferenciaRepositoryPort;
import com.example.demo.domain.port.UsuarioRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarPreferenciasUsuarioUseCaseTest {

    @Mock
    private PreferenciaRepositoryPort preferenciaRepositoryPort;

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @InjectMocks
    private ConsultarPreferenciasUsuarioUseCase useCase;

    @Test
    void ejecutar_usuarioNoExiste_debeLanzarExcepcion() {
        when(usuarioRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.ejecutar(1L))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }

    @Test
    void ejecutar_exitoso_retornaPreferencias() {
        when(usuarioRepositoryPort.findById(1L)).thenReturn(Optional.of(new Usuario()));
        when(preferenciaRepositoryPort.findByIdUsuario(1L)).thenReturn(List.of(new Preferencia()));

        List<Preferencia> resultado = useCase.ejecutar(1L);
        assertThat(resultado).hasSize(1);
    }
}
