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

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarPreferenciaUseCaseTest {

    @Mock
    private PreferenciaRepositoryPort preferenciaRepositoryPort;

    @InjectMocks
    private ActualizarPreferenciaUseCase actualizarPreferenciaUseCase;

    @Test
    void ejecutar_cuandoPreferenciaPerteneceAlUsuario_debeActualizar() {
        Preferencia p = new Preferencia();
        p.setIdPreferencia(1L);
        p.setIdUsuario(1L);
        p.setIdCategoria(1L);
        p.setPresupuestoMax(new BigDecimal("10000"));
        when(preferenciaRepositoryPort.findById(1L)).thenReturn(Optional.of(p));
        when(preferenciaRepositoryPort.save(any(Preferencia.class))).thenAnswer(inv -> inv.getArgument(0));

        Preferencia actualizada = actualizarPreferenciaUseCase.ejecutar(1L, 1L, 2L, new BigDecimal("50000"));

        assertThat(actualizada.getIdCategoria()).isEqualTo(2L);
        assertThat(actualizada.getPresupuestoMax()).isEqualByComparingTo("50000");
    }

    @Test
    void ejecutar_cuandoNoExiste_debeLanzarExcepcion() {
        when(preferenciaRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> actualizarPreferenciaUseCase.ejecutar(99L, 1L, 1L, BigDecimal.TEN))
                .isInstanceOf(PreferenciaNoEncontradaException.class);
    }

    @Test
    void ejecutar_cuandoNoPerteneceAlUsuario_debeLanzarExcepcion() {
        Preferencia p = new Preferencia();
        p.setIdPreferencia(1L);
        p.setIdUsuario(2L);
        when(preferenciaRepositoryPort.findById(1L)).thenReturn(Optional.of(p));

        assertThatThrownBy(() -> actualizarPreferenciaUseCase.ejecutar(1L, 1L, 1L, BigDecimal.TEN))
                .isInstanceOf(OperacionNoAutorizadaException.class);
    }
}
