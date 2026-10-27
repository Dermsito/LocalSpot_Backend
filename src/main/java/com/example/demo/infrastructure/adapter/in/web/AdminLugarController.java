package com.example.demo.infrastructure.adapter.in.web;

import com.example.demo.application.usecase.AdministrarLugaresUseCase;
import com.example.demo.domain.model.EstadoVerificacion;
import com.example.demo.domain.model.Lugar;
import com.example.demo.domain.model.Pagina;
import com.example.demo.domain.model.Paginacion;
import com.example.demo.infrastructure.adapter.in.web.dto.LugarRequest;
import com.example.demo.infrastructure.adapter.in.web.dto.LugarResponse;
import com.example.demo.infrastructure.adapter.in.web.dto.PaginaResponse;
import com.example.demo.infrastructure.adapter.in.web.dto.VerificacionRequest;
import com.example.demo.infrastructure.config.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/lugares")
@Tag(name = "Admin Lugares", description = "Administrador: CRUD de lugares y verificacion")
public class AdminLugarController {

    private final AdministrarLugaresUseCase administrarLugaresUseCase;

    public AdminLugarController(AdministrarLugaresUseCase administrarLugaresUseCase) {
        this.administrarLugaresUseCase = administrarLugaresUseCase;
    }

    @GetMapping
    @Operation(summary = "Listar lugares", description = "Lista todos los lugares con paginacion (admin).")
    public ResponseEntity<PaginaResponse<LugarResponse>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pagina<Lugar> pagina = administrarLugaresUseCase.listar(new Paginacion(page, size));
        List<LugarResponse> content = pagina.content().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        PaginaResponse<LugarResponse> response = new PaginaResponse<>(
                content, pagina.page(), pagina.size(), pagina.totalElements(), pagina.totalPages());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Crear lugar", description = "Crea un lugar verificado (admin).")
    public ResponseEntity<LugarResponse> crear(@Valid @RequestBody LugarRequest request) {
        Long idAdmin = SecurityUtils.getCurrentUsuarioId();
        Lugar lugar = administrarLugaresUseCase.crear(idAdmin, toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(lugar));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar lugar", description = "Actualiza los datos de un lugar (admin).")
    public ResponseEntity<LugarResponse> actualizar(@PathVariable Long id,
            @Valid @RequestBody LugarRequest request) {
        Long idAdmin = SecurityUtils.getCurrentUsuarioId();
        Lugar lugar = administrarLugaresUseCase.actualizar(idAdmin, id, toDomain(request));
        return ResponseEntity.ok(toResponse(lugar));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar lugar", description = "Elimina un lugar y sus fotos (admin).")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Long idAdmin = SecurityUtils.getCurrentUsuarioId();
        administrarLugaresUseCase.eliminar(idAdmin, id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/verificacion")
    @Operation(summary = "Cambiar verificacion", description = "Verifica o rechaza un lugar (admin).")
    public ResponseEntity<LugarResponse> cambiarVerificacion(@PathVariable Long id,
            @Valid @RequestBody VerificacionRequest request) {
        Long idAdmin = SecurityUtils.getCurrentUsuarioId();
        EstadoVerificacion estado = EstadoVerificacion.valueOf(request.getEstado().toUpperCase());
        Lugar lugar = administrarLugaresUseCase.cambiarVerificacion(idAdmin, id, estado);
        return ResponseEntity.ok(toResponse(lugar));
    }

    private Lugar toDomain(LugarRequest request) {
        Lugar lugar = new Lugar();
        lugar.setNombre(request.getNombre());
        lugar.setDescripcion(request.getDescripcion());
        lugar.setDireccion(request.getDireccion());
        lugar.setHorario(request.getHorario());
        lugar.setLatitud(request.getLatitud());
        lugar.setLongitud(request.getLongitud());
        lugar.setPrecioMin(request.getPrecioMin());
        lugar.setPrecioMax(request.getPrecioMax());
        lugar.setIdCategoria(request.getIdCategoria());
        return lugar;
    }

    private LugarResponse toResponse(Lugar lugar) {
        LugarResponse r = new LugarResponse();
        r.setIdLugar(lugar.getIdLugar());
        r.setIdCategoria(lugar.getIdCategoria());
        r.setIdPropietario(lugar.getIdPropietario());
        r.setNombre(lugar.getNombre());
        r.setDescripcion(lugar.getDescripcion());
        r.setDireccion(lugar.getDireccion());
        r.setHorario(lugar.getHorario());
        r.setLatitud(lugar.getLatitud());
        r.setLongitud(lugar.getLongitud());
        r.setPrecioMin(lugar.getPrecioMin());
        r.setPrecioMax(lugar.getPrecioMax());
        r.setEstadoVerificacion(lugar.getEstadoVerificacion() != null ? lugar.getEstadoVerificacion().name() : null);
        r.setFechaRegistro(lugar.getFechaRegistro());
        r.setFechaActualizacion(lugar.getFechaActualizacion());
        return r;
    }
}
