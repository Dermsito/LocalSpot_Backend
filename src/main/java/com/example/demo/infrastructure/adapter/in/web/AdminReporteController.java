package com.example.demo.infrastructure.adapter.in.web;

import com.example.demo.application.usecase.GestionarReportesUseCase;
import com.example.demo.domain.model.EstadoReporte;
import com.example.demo.domain.model.Reporte;
import com.example.demo.infrastructure.adapter.in.web.dto.ReporteResponse;
import com.example.demo.infrastructure.adapter.in.web.dto.ResolverReporteRequest;
import com.example.demo.infrastructure.config.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin Moderacion", description = "Administrador: gestion de reportes y moderacion")
public class AdminReporteController {

    private final GestionarReportesUseCase gestionarReportesUseCase;

    public AdminReporteController(GestionarReportesUseCase gestionarReportesUseCase) {
        this.gestionarReportesUseCase = gestionarReportesUseCase;
    }

    @GetMapping("/reportes")
    @Operation(summary = "Listar reportes", description = "Lista todos los reportes ordenados por estado.")
    public ResponseEntity<List<ReporteResponse>> listarReportes() {
        List<Reporte> reportes = gestionarReportesUseCase.listarReportes();
        List<ReporteResponse> response = reportes.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/reportes/{id}")
    @Operation(summary = "Resolver reporte", description = "Acepta (oculta comentario) o descarta un reporte.")
    public ResponseEntity<ReporteResponse> resolverReporte(
            @PathVariable Long id,
            @Valid @RequestBody ResolverReporteRequest request) {
        Long idAdmin = SecurityUtils.getCurrentUsuarioId();
        EstadoReporte estado = EstadoReporte.valueOf(request.getEstado().toUpperCase());
        Reporte reporte = gestionarReportesUseCase.resolverReporte(idAdmin, id, estado);
        return ResponseEntity.ok(toResponse(reporte));
    }

    @PatchMapping("/calificaciones/{id}/ocultar")
    @Operation(summary = "Ocultar calificacion", description = "Oculta una calificacion sin reporte previo.")
    public ResponseEntity<Void> ocultarCalificacion(@PathVariable Long id) {
        Long idAdmin = SecurityUtils.getCurrentUsuarioId();
        gestionarReportesUseCase.ocultarCalificacion(idAdmin, id);
        return ResponseEntity.noContent().build();
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
