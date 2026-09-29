package com.example.demo.infrastructure.adapter.in.web;

import com.example.demo.application.usecase.ActualizarPreferenciaUseCase;
import com.example.demo.application.usecase.ConsultarPreferenciasUsuarioUseCase;
import com.example.demo.application.usecase.CrearPreferenciaUseCase;
import com.example.demo.domain.model.Categoria;
import com.example.demo.domain.model.Preferencia;
import com.example.demo.domain.port.CategoriaRepositoryPort;
import com.example.demo.infrastructure.adapter.in.web.dto.ErrorResponse;
import com.example.demo.infrastructure.adapter.in.web.dto.PreferenciaRequest;
import com.example.demo.infrastructure.adapter.in.web.dto.PreferenciaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Preferencias", description = "Endpoints protegidos para gestion de preferencias de usuario (requieren token JWT)")
@SecurityRequirement(name = "bearerAuth")
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
    @Operation(summary = "Crear preferencia",
            description = "Crea una nueva preferencia para el usuario autenticado. Recibe idCategoria (1..5 - ver data.sql) y presupuestoMax.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Preferencia creada correctamente",
                    content = @Content(schema = @Schema(implementation = PreferenciaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos (idCategoria o presupuestoMax faltantes/negativos)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT faltante o invalido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Categoria o usuario no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PreferenciaResponse> crear(
            @Valid @RequestBody
            @Parameter(description = "Datos de la preferencia", required = true)
            PreferenciaRequest request,
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
    @Operation(summary = "Actualizar preferencia",
            description = "Modifica categoria y/o presupuesto de una preferencia existente. Solo puede actualizarla el propio usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Preferencia actualizada correctamente",
                    content = @Content(schema = @Schema(implementation = PreferenciaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT faltante o invalido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No autorizado (la preferencia pertenece a otro usuario)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Preferencia o categoria no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PreferenciaResponse> actualizar(
            @Parameter(description = "ID de la preferencia a actualizar", required = true, in = ParameterIn.PATH)
            @PathVariable Long id,
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
    @Operation(summary = "Consultar preferencias de un usuario",
            description = "Devuelve la lista de preferencias asociadas a un usuario, incluyendo el nombre de la categoria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de preferencias",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PreferenciaResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Token JWT faltante o invalido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<PreferenciaResponse>> consultarPorUsuario(
            @Parameter(description = "ID del usuario del cual consultar preferencias", required = true, in = ParameterIn.PATH)
            @PathVariable Long idUsuario,
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

