package com.example.demo.application.usecase;

import com.example.demo.domain.exception.OperacionNoAutorizadaException;
import com.example.demo.domain.exception.PreferenciaNoEncontradaException;
import com.example.demo.domain.model.Preferencia;
import com.example.demo.domain.port.PreferenciaRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EliminarPreferenciaUseCaseTest {

    @Mock
    private PreferenciaRepositoryPort preferenciaRepositoryPort;

    @InjectMocks
    private EliminarPreferenciaUseCase eliminarPreferenciaUseCase;

    @Test
    void ejecutar_cuandoExisteYEsPropietario_debeEliminar() {
        Preferencia p = new Preferencia();
        p.setIdPreferencia(1L);
        p.setIdUsuario(1L);
        when(preferenciaRepositoryPort.findById(1L)).thenReturn(Optional.of(p));

        eliminarPreferenciaUseCase.ejecutar(1L, 1L);

        verify(preferenciaRepositoryPort, times(1)).deleteById(1L);
    }

    @Test
    void ejecutar_cuandoNoExiste_debeLanzarExcepcion() {
        when(preferenciaRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eliminarPreferenciaUseCase.ejecutar(99L, 1L))
                .isInstanceOf(PreferenciaNoEncontradaException.class);
    }

    @Test
    void ejecutar_cuandoNoEsPropietario_debeLanzarExcepcion() {
        Preferencia p = new Preferencia();
        p.setIdPreferencia(1L);
        p.setIdUsuario(2L);
        when(preferenciaRepositoryPort.findById(1L)).thenReturn(Optional.of(p));

        assertThatThrownBy(() -> eliminarPreferenciaUseCase.ejecutar(1L, 1L))
                .isInstanceOf(OperacionNoAutorizadaException.class);
    }
}
