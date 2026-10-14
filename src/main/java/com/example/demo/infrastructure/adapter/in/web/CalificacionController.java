package com.example.demo.infrastructure.adapter.in.web;

import com.example.demo.application.usecase.CalificarLugarUseCase;
import com.example.demo.domain.model.Calificacion;
import com.example.demo.infrastructure.adapter.in.web.dto.CalificacionRequest;
import com.example.demo.infrastructure.adapter.in.web.dto.CalificacionResponse;
import com.example.demo.infrastructure.config.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lugares/{idLugar}/calificaciones")
@Tag(name = "Calificaciones", description = "Calificacion de lugares")
public class CalificacionController {

    private final CalificarLugarUseCase calificarLugarUseCase;

    public CalificacionController(CalificarLugarUseCase calificarLugarUseCase) {
        this.calificarLugarUseCase = calificarLugarUseCase;
    }

    @PostMapping
    @Operation(summary = "Calificar lugar", description = "Crea una calificacion (1-5) y comentario para un lugar. Una por usuario.")
    public ResponseEntity<CalificacionResponse> crear(
            @PathVariable Long idLugar,
            @Valid @RequestBody CalificacionRequest request) {
        Long idUsuario = SecurityUtils.getCurrentUsuarioId();
        Calificacion calificacion = calificarLugarUseCase.crear(
                idUsuario, idLugar, request.getPuntuacion(), request.getComentario());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(calificacion));
    }

    @PutMapping
    @Operation(summary = "Editar calificacion", description = "Edita la calificacion del usuario para un lugar. Registra auditoria.")
    public ResponseEntity<CalificacionResponse> editar(
            @PathVariable Long idLugar,
            @Valid @RequestBody CalificacionRequest request) {
        Long idUsuario = SecurityUtils.getCurrentUsuarioId();
        Calificacion calificacion = calificarLugarUseCase.editar(
                idUsuario, idLugar, request.getPuntuacion(), request.getComentario());
        return ResponseEntity.ok(toResponse(calificacion));
    }

    private CalificacionResponse toResponse(Calificacion c) {
        CalificacionResponse r = new CalificacionResponse();
        r.setIdCalificacion(c.getIdCalificacion());
        r.setIdUsuario(c.getIdUsuario());
        r.setIdLugar(c.getIdLugar());
        r.setPuntuacion(c.getPuntuacion());
        r.setComentario(c.getComentario());
        r.setVisible(c.getVisible());
        r.setFechaRegistro(c.getFechaRegistro());
        r.setFechaActualizacion(c.getFechaActualizacion());
        return r;
    }
}
