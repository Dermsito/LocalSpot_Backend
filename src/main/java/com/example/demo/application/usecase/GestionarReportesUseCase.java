package com.example.demo.application.usecase;

import com.example.demo.domain.exception.CalificacionNoEncontradaException;
import com.example.demo.domain.exception.EstadoReporteInvalidoException;
import com.example.demo.domain.exception.ReporteDuplicadoException;
import com.example.demo.domain.exception.ReporteNoEncontradoException;
import com.example.demo.domain.model.EstadoReporte;
import com.example.demo.domain.model.Reporte;
import com.example.demo.domain.port.AuditoriaPort;
import com.example.demo.domain.port.CalificacionRepositoryPort;
import com.example.demo.domain.port.ReporteRepositoryPort;

import java.time.LocalDateTime;
import java.util.List;

/**
 * RF-12: Moderacion de reportes.
 * - Usuario reporta una calificacion (una vez).
 * - Admin lista reportes, acepta (oculta comentario) o descarta.
 * - Admin tambien puede ocultar una calificacion sin reporte.
 */
public class GestionarReportesUseCase {

    private final ReporteRepositoryPort reporteRepositoryPort;
    private final CalificacionRepositoryPort calificacionRepositoryPort;
    private final AuditoriaPort auditoriaPort;

    public GestionarReportesUseCase(ReporteRepositoryPort reporteRepositoryPort,
                                     CalificacionRepositoryPort calificacionRepositoryPort,
                                     AuditoriaPort auditoriaPort) {
        this.reporteRepositoryPort = reporteRepositoryPort;
        this.calificacionRepositoryPort = calificacionRepositoryPort;
        this.auditoriaPort = auditoriaPort;
    }

    public Reporte reportarCalificacion(Long idUsuario, Long idCalificacion, String motivo) {
        if (!calificacionRepositoryPort.findById(idCalificacion).isPresent()) {
            throw new CalificacionNoEncontradaException("Calificacion no encontrada con id: " + idCalificacion);
        }
        if (reporteRepositoryPort.existsByCalificacionIdAndUsuarioId(idCalificacion, idUsuario)) {
            throw new ReporteDuplicadoException("Ya reportaste esta calificacion");
        }

        Reporte reporte = new Reporte();
        reporte.setIdCalificacion(idCalificacion);
        reporte.setIdUsuario(idUsuario);
        reporte.setMotivo(motivo);
        reporte.setEstado(EstadoReporte.PENDIENTE);
        reporte.setFechaCreacion(LocalDateTime.now());

        return reporteRepositoryPort.save(reporte);
    }

    public List<Reporte> listarReportes() {
        return reporteRepositoryPort.findAllOrderByEstado();
    }

    public Reporte resolverReporte(Long idAdmin, Long idReporte, EstadoReporte estado) {
        if (estado == null || (estado != EstadoReporte.ACEPTADO && estado != EstadoReporte.DESCARTADO)) {
            throw new EstadoReporteInvalidoException("El estado debe ser ACEPTADO o DESCARTADO");
        }
        Reporte reporte = reporteRepositoryPort.findById(idReporte)
                .orElseThrow(() -> new ReporteNoEncontradoException("Reporte no encontrado con id: " + idReporte));

        reporteRepositoryPort.updateEstado(idReporte, estado);
        reporte.setEstado(estado);
        reporte.setFechaResolucion(LocalDateTime.now());

        if (estado == EstadoReporte.ACEPTADO) {
            // Ocultar el comentario de la calificacion
            calificacionRepositoryPort.updateVisible(reporte.getIdCalificacion(), false);
            auditoriaPort.registrar(idAdmin, "CALIFICACION", reporte.getIdCalificacion(), "UPDATE",
                    "visible=true", "visible=false");
        }
        auditoriaPort.registrar(idAdmin, "REPORTE", idReporte, "UPDATE",
                "estado=PENDIENTE", "estado=" + estado);
        return reporte;
    }

    public void ocultarCalificacion(Long idAdmin, Long idCalificacion) {
        if (!calificacionRepositoryPort.findById(idCalificacion).isPresent()) {
            throw new CalificacionNoEncontradaException("Calificacion no encontrada con id: " + idCalificacion);
        }
        calificacionRepositoryPort.updateVisible(idCalificacion, false);
        auditoriaPort.registrar(idAdmin, "CALIFICACION", idCalificacion, "UPDATE",
                "visible=true", "visible=false");
    }
}
