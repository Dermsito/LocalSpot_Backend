package com.example.demo.infrastructure.adapter.in.web;

import com.example.demo.application.usecase.ActualizarPreferenciaUseCase;
import com.example.demo.application.usecase.ConsultarPreferenciasUsuarioUseCase;
import com.example.demo.application.usecase.CrearPreferenciaUseCase;
import com.example.demo.domain.model.Categoria;
import com.example.demo.domain.model.Preferencia;
import com.example.demo.domain.port.CategoriaRepositoryPort;
import com.example.demo.infrastructure.adapter.in.web.dto.PreferenciaRequest;
import com.example.demo.infrastructure.adapter.in.web.dto.PreferenciaResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/preferencias")
public class PreferenciaController {

    private final CrearPreferenciaUseCase crearPreferenciaUseCase;
    private final ActualizarPreferenciaUseCase actualizarPreferenciaUseCase;
    private final ConsultarPreferenciasUsuarioUseCase consultarPreferenciasUsuarioUseCase;
    private final CategoriaRepositoryPort categoriaRepositoryPort;

    public PreferenciaController(CrearPreferenciaUseCase crearPreferenciaUseCase,
                                 ActualizarPreferenciaUseCase actualizarPreferenciaUseCase,
                                 ConsultarPreferenciasUsuarioUseCase consultarPreferenciasUsuarioUseCase,
                                 CategoriaRepositoryPort categoriaRepositoryPort) {
        this.crearPreferenciaUseCase = crearPreferenciaUseCase;
        this.actualizarPreferenciaUseCase = actualizarPreferenciaUseCase;
        this.consultarPreferenciasUsuarioUseCase = consultarPreferenciasUsuarioUseCase;
        this.categoriaRepositoryPort = categoriaRepositoryPort;
    }

    @PostMapping
    public ResponseEntity<PreferenciaResponse> crear(@Valid @RequestBody PreferenciaRequest request,
                                                     Authentication authentication) {
        Long idUsuarioAutenticado = (Long) authentication.getPrincipal();
        Preferencia preferencia = crearPreferenciaUseCase.ejecutar(
                idUsuarioAutenticado,
                request.getIdCategoria(),
                request.getPresupuestoMax()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(preferencia));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PreferenciaResponse> actualizar(@PathVariable Long id,
                                                          @Valid @RequestBody PreferenciaRequest request,
                                                          Authentication authentication) {
        Long idUsuarioAutenticado = (Long) authentication.getPrincipal();
        Preferencia preferencia = actualizarPreferenciaUseCase.ejecutar(
                id,
                idUsuarioAutenticado,
                request.getIdCategoria(),
                request.getPresupuestoMax()
        );
        return ResponseEntity.ok(toResponse(preferencia));
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<PreferenciaResponse>> consultarPorUsuario(@PathVariable Long idUsuario,
                                                                         Authentication authentication) {
        List<Preferencia> preferencias = consultarPreferenciasUsuarioUseCase.ejecutar(idUsuario);
        Map<Long, String> categorias = categoriaRepositoryPort.findAll().stream()
                .collect(Collectors.toMap(Categoria::getIdCategoria, Categoria::getNombreCategoria));
        List<PreferenciaResponse> respuestas = preferencias.stream()
                .map(p -> toResponse(p, categorias.get(p.getIdCategoria())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(respuestas);
    }

    private PreferenciaResponse toResponse(Preferencia preferencia) {
        String nombreCategoria = categoriaRepositoryPort.findById(preferencia.getIdCategoria())
                .map(Categoria::getNombreCategoria)
                .orElse(null);
        return toResponse(preferencia, nombreCategoria);
    }

    private PreferenciaResponse toResponse(Preferencia preferencia, String nombreCategoria) {
        return new PreferenciaResponse(
                preferencia.getIdPreferencia(),
                preferencia.getIdUsuario(),
                preferencia.getIdCategoria(),
                nombreCategoria,
                preferencia.getPresupuestoMax()
        );
    }
}
