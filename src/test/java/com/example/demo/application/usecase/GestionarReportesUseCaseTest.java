package com.example.demo.application.usecase;

import com.example.demo.domain.exception.CalificacionNoEncontradaException;
import com.example.demo.domain.exception.EstadoReporteInvalidoException;
import com.example.demo.domain.exception.ReporteDuplicadoException;
import com.example.demo.domain.exception.ReporteNoEncontradoException;
import com.example.demo.domain.model.Calificacion;
import com.example.demo.domain.model.EstadoReporte;
import com.example.demo.domain.model.Reporte;
import com.example.demo.domain.port.AuditoriaPort;
import com.example.demo.domain.port.CalificacionRepositoryPort;
import com.example.demo.domain.port.ReporteRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestionarReportesUseCaseTest {

    @Mock
    private ReporteRepositoryPort reporteRepositoryPort;

    @Mock
    private CalificacionRepositoryPort calificacionRepositoryPort;

    @Mock
    private AuditoriaPort auditoriaPort;

    @InjectMocks
    private GestionarReportesUseCase useCase;

    @Test
    void reportarCalificacion_cuandoNoExiste_debeLanzarExcepcion() {
        when(calificacionRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.reportarCalificacion(2L, 1L, "Ofensivo"))
                .isInstanceOf(CalificacionNoEncontradaException.class);
    }

    @Test
    void reportarCalificacion_cuandoYaReporto_debeLanzarExcepcion() {
        when(calificacionRepositoryPort.findById(1L)).thenReturn(Optional.of(new Calificacion()));
        when(reporteRepositoryPort.existsByCalificacionIdAndUsuarioId(1L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> useCase.reportarCalificacion(2L, 1L, "Ofensivo"))
                .isInstanceOf(ReporteDuplicadoException.class);
    }

    @Test
    void reportarCalificacion_exitoso() {
        when(calificacionRepositoryPort.findById(1L)).thenReturn(Optional.of(new Calificacion()));
        when(reporteRepositoryPort.existsByCalificacionIdAndUsuarioId(1L, 2L)).thenReturn(false);
        when(reporteRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Reporte rep = useCase.reportarCalificacion(2L, 1L, "Spam");
        assertThat(rep.getEstado()).isEqualTo(EstadoReporte.PENDIENTE);
        assertThat(rep.getMotivo()).isEqualTo("Spam");
    }

    @Test
    void listarReportes_debeRetornarLista() {
        when(reporteRepositoryPort.findAllOrderByEstado()).thenReturn(List.of(new Reporte()));
        assertThat(useCase.listarReportes()).hasSize(1);
    }

    @Test
    void resolverReporte_estadoInvalido_debeLanzarExcepcion() {
        assertThatThrownBy(() -> useCase.resolverReporte(1L, 2L, null))
                .isInstanceOf(EstadoReporteInvalidoException.class);

        assertThatThrownBy(() -> useCase.resolverReporte(1L, 2L, EstadoReporte.PENDIENTE))
                .isInstanceOf(EstadoReporteInvalidoException.class);
    }

    @Test
    void resolverReporte_noEncontrado_debeLanzarExcepcion() {
        when(reporteRepositoryPort.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.resolverReporte(1L, 2L, EstadoReporte.ACEPTADO))
                .isInstanceOf(ReporteNoEncontradoException.class);
    }

    @Test
    void resolverReporte_aceptado_debeOcultarYAuditar() {
        Reporte r = new Reporte();
        r.setIdReporte(2L);
        r.setIdCalificacion(10L);
        when(reporteRepositoryPort.findById(2L)).thenReturn(Optional.of(r));

        Reporte res = useCase.resolverReporte(1L, 2L, EstadoReporte.ACEPTADO);

        assertThat(res.getEstado()).isEqualTo(EstadoReporte.ACEPTADO);
        verify(calificacionRepositoryPort).updateVisible(10L, false);
        verify(auditoriaPort, times(2)).registrar(anyLong(), anyString(), anyLong(), anyString(), anyString(), anyString());
    }

    @Test
    void resolverReporte_descartado_noOcultaCalificacion() {
        Reporte r = new Reporte();
        r.setIdReporte(2L);
        r.setIdCalificacion(10L);
        when(reporteRepositoryPort.findById(2L)).thenReturn(Optional.of(r));

        Reporte res = useCase.resolverReporte(1L, 2L, EstadoReporte.DESCARTADO);

        assertThat(res.getEstado()).isEqualTo(EstadoReporte.DESCARTADO);
        verify(calificacionRepositoryPort, never()).updateVisible(anyLong(), anyBoolean());
        verify(auditoriaPort, times(1)).registrar(anyLong(), anyString(), anyLong(), anyString(), anyString(), anyString());
    }

    @Test
    void ocultarCalificacion_noExiste_debeLanzarExcepcion() {
        when(calificacionRepositoryPort.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.ocultarCalificacion(1L, 5L))
                .isInstanceOf(CalificacionNoEncontradaException.class);
    }

    @Test
    void ocultarCalificacion_exitoso() {
        when(calificacionRepositoryPort.findById(5L)).thenReturn(Optional.of(new Calificacion()));

        useCase.ocultarCalificacion(1L, 5L);

        verify(calificacionRepositoryPort).updateVisible(5L, false);
        verify(auditoriaPort).registrar(1L, "CALIFICACION", 5L, "UPDATE", "visible=true", "visible=false");
    }
}
