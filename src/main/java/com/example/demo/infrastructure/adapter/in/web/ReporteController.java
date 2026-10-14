package com.example.demo.infrastructure.adapter.in.web;

import com.example.demo.application.usecase.GestionarReportesUseCase;
import com.example.demo.domain.model.Reporte;
import com.example.demo.infrastructure.adapter.in.web.dto.ReporteRequest;
import com.example.demo.infrastructure.adapter.in.web.dto.ReporteResponse;
import com.example.demo.infrastructure.config.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calificaciones/{idCalificacion}/reportes")
@Tag(name = "Reportes", description = "Reportar calificaciones para moderacion")
public class ReporteController {

    private final GestionarReportesUseCase gestionarReportesUseCase;

    public ReporteController(GestionarReportesUseCase gestionarReportesUseCase) {
        this.gestionarReportesUseCase = gestionarReportesUseCase;
    }

    @PostMapping
    @Operation(summary = "Reportar calificacion", description = "Un usuario reporta una calificacion (una vez por calificacion).")
    public ResponseEntity<ReporteResponse> reportar(
            @PathVariable Long idCalificacion,
            @Valid @RequestBody ReporteRequest request) {
        Long idUsuario = SecurityUtils.getCurrentUsuarioId();
        Reporte reporte = gestionarReportesUseCase.reportarCalificacion(
                idUsuario, idCalificacion, request.getMotivo());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(reporte));
    }

    private ReporteResponse toResponse(Reporte r) {
        ReporteResponse response = new ReporteResponse();
        response.setIdReporte(r.getIdReporte());
        response.setIdCalificacion(r.getIdCalificacion());
        response.setIdUsuario(r.getIdUsuario());
        response.setMotivo(r.getMotivo());
        response.setEstado(r.getEstado() != null ? r.getEstado().name() : null);
        response.setFechaCreacion(r.getFechaCreacion());
        response.setFechaResolucion(r.getFechaResolucion());
        return response;
    }
}
