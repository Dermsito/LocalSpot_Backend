package com.example.demo.infrastructure.adapter.in.web;

import com.example.demo.application.usecase.GenerarRecomendacionesUseCase;
import com.example.demo.domain.model.Lugar;
import com.example.demo.domain.model.Recomendacion;
import com.example.demo.domain.port.LugarRepositoryPort;
import com.example.demo.domain.port.RecomendacionRepositoryPort;
import com.example.demo.infrastructure.adapter.in.web.dto.RecomendacionResponse;
import com.example.demo.infrastructure.config.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recomendaciones")
@Tag(name = "Recomendaciones", description = "Recomendaciones de lugares poco conocidos")
public class RecomendacionController {

    private final GenerarRecomendacionesUseCase generarRecomendacionesUseCase;
    private final RecomendacionRepositoryPort recomendacionRepositoryPort;
    private final LugarRepositoryPort lugarRepositoryPort;

    public RecomendacionController(GenerarRecomendacionesUseCase generarRecomendacionesUseCase,
                                    RecomendacionRepositoryPort recomendacionRepositoryPort,
                                    LugarRepositoryPort lugarRepositoryPort) {
        this.generarRecomendacionesUseCase = generarRecomendacionesUseCase;
        this.recomendacionRepositoryPort = recomendacionRepositoryPort;
        this.lugarRepositoryPort = lugarRepositoryPort;
    }

    @PostMapping("/generar")
    @Operation(summary = "Generar recomendaciones", description = "Genera recomendaciones basadas en preferencias del usuario.")
    public ResponseEntity<List<RecomendacionResponse>> generar() {
        Long idUsuario = SecurityUtils.getCurrentUsuarioId();
        List<Recomendacion> recomendaciones = generarRecomendacionesUseCase.ejecutar(idUsuario);
        List<RecomendacionResponse> response = recomendaciones.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar recomendaciones", description = "Lista las recomendaciones generadas para el usuario.")
    public ResponseEntity<List<RecomendacionResponse>> listar() {
        Long idUsuario = SecurityUtils.getCurrentUsuarioId();
        List<Recomendacion> recomendaciones =
                recomendacionRepositoryPort.findByUsuarioIdOrderByPuntajeDesc(idUsuario);
        List<RecomendacionResponse> response = recomendaciones.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private RecomendacionResponse toResponse(Recomendacion r) {
        RecomendacionResponse response = new RecomendacionResponse();
        response.setIdRecomendacion(r.getIdRecomendacion());
        response.setIdUsuario(r.getIdUsuario());
        response.setIdLugar(r.getIdLugar());
        response.setPuntajeAfinidad(r.getPuntajeAfinidad());
        response.setMotivo(r.getMotivo());
        response.setFechaGeneracion(r.getFechaGeneracion());
        lugarRepositoryPort.findById(r.getIdLugar())
                .map(Lugar::getNombre)
                .ifPresent(response::setNombreLugar);
        return response;
    }
}
