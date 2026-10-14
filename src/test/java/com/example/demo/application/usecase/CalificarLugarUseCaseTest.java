package com.example.demo.application.usecase;

import com.example.demo.domain.exception.CalificacionDuplicadaException;
import com.example.demo.domain.exception.CalificacionNoEncontradaException;
import com.example.demo.domain.exception.LugarNoEncontradoException;
import com.example.demo.domain.model.Calificacion;
import com.example.demo.domain.model.Lugar;
import com.example.demo.domain.port.AuditoriaPort;
import com.example.demo.domain.port.CalificacionRepositoryPort;
import com.example.demo.domain.port.LugarRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalificarLugarUseCaseTest {

    @Mock
    private CalificacionRepositoryPort calificacionRepositoryPort;

    @Mock
    private LugarRepositoryPort lugarRepositoryPort;

    @Mock
    private AuditoriaPort auditoriaPort;

    @InjectMocks
    private CalificarLugarUseCase calificarLugarUseCase;

    @Test
    void crear_cuandoLugarExisteYNoHayCalificacion_debeGuardar() {
        when(lugarRepositoryPort.findById(1L)).thenReturn(Optional.of(new Lugar()));
        when(calificacionRepositoryPort.existsByUsuarioIdAndLugarId(1L, 1L)).thenReturn(false);
        when(calificacionRepositoryPort.save(any(Calificacion.class))).thenAnswer(inv -> inv.getArgument(0));

        Calificacion c = calificarLugarUseCase.crear(1L, 1L, (short) 5, "Excelente");

        assertThat(c.getPuntuacion()).isEqualTo((short) 5);
        assertThat(c.getComentario()).isEqualTo("Excelente");
    }

    @Test
    void crear_cuandoLugarNoExiste_debeLanzarExcepcion() {
        when(lugarRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> calificarLugarUseCase.crear(1L, 99L, (short) 5, "test"))
                .isInstanceOf(LugarNoEncontradoException.class);
    }

    @Test
    void crear_cuandoYaExisteCalificacion_debeLanzarExcepcion() {
        when(lugarRepositoryPort.findById(1L)).thenReturn(Optional.of(new Lugar()));
        when(calificacionRepositoryPort.existsByUsuarioIdAndLugarId(1L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> calificarLugarUseCase.crear(1L, 1L, (short) 5, "test"))
                .isInstanceOf(CalificacionDuplicadaException.class);
    }

    @Test
    void editar_cuandoExisteCalificacion_debeActualizarYAuditar() {
        Calificacion existente = new Calificacion();
        existente.setIdCalificacion(1L);
        existente.setIdUsuario(1L);
        existente.setIdLugar(1L);
        existente.setPuntuacion((short) 3);
        when(calificacionRepositoryPort.findByUsuarioIdAndLugarId(1L, 1L))
                .thenReturn(Optional.of(existente));
        when(calificacionRepositoryPort.save(any(Calificacion.class))).thenAnswer(inv -> inv.getArgument(0));

        Calificacion editada = calificarLugarUseCase.editar(1L, 1L, (short) 5, "Mejor");

        assertThat(editada.getPuntuacion()).isEqualTo((short) 5);
        verify(auditoriaPort, times(1)).registrar(any(), anyString(), any(), anyString(), any(), any());
    }

    @Test
    void editar_cuandoNoExisteCalificacion_debeLanzarExcepcion() {
        when(calificacionRepositoryPort.findByUsuarioIdAndLugarId(1L, 1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> calificarLugarUseCase.editar(1L, 1L, (short) 5, "test"))
                .isInstanceOf(CalificacionNoEncontradaException.class);
    }
}
