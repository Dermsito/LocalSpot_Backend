package com.example.demo.infrastructure.adapter.in.web;

import com.example.demo.application.usecase.GestionarFavoritosUseCase;
import com.example.demo.domain.model.Favorito;
import com.example.demo.domain.model.Lugar;
import com.example.demo.domain.port.LugarRepositoryPort;
import com.example.demo.infrastructure.adapter.in.web.dto.FavoritoResponse;
import com.example.demo.infrastructure.config.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favoritos")
@Tag(name = "Favoritos", description = "Gestion de lugares favoritos")
public class FavoritoController {

    private final GestionarFavoritosUseCase gestionarFavoritosUseCase;
    private final LugarRepositoryPort lugarRepositoryPort;

    public FavoritoController(GestionarFavoritosUseCase gestionarFavoritosUseCase,
            LugarRepositoryPort lugarRepositoryPort) {
        this.gestionarFavoritosUseCase = gestionarFavoritosUseCase;
        this.lugarRepositoryPort = lugarRepositoryPort;
    }

    @GetMapping
    @Operation(summary = "Listar favoritos", description = "Lista los lugares favoritos del usuario autenticado.")
    public ResponseEntity<List<FavoritoResponse>> listar() {
        Long idUsuario = SecurityUtils.getCurrentUsuarioId();
        List<Favorito> favoritos = gestionarFavoritosUseCase.listar(idUsuario);
        List<FavoritoResponse> response = favoritos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{lugarId}")
    @Operation(summary = "Agregar favorito", description = "Agrega un lugar a los favoritos del usuario.")
    public ResponseEntity<FavoritoResponse> agregar(@PathVariable Long lugarId) {
        Long idUsuario = SecurityUtils.getCurrentUsuarioId();
        Favorito favorito = gestionarFavoritosUseCase.agregar(idUsuario, lugarId);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(favorito));
    }

    @DeleteMapping("/{lugarId}")
    @Operation(summary = "Quitar favorito", description = "Quita un lugar de los favoritos del usuario.")
    public ResponseEntity<Void> quitar(@PathVariable Long lugarId) {
        Long idUsuario = SecurityUtils.getCurrentUsuarioId();
        gestionarFavoritosUseCase.quitar(idUsuario, lugarId);
        return ResponseEntity.noContent().build();
    }

    private FavoritoResponse toResponse(Favorito f) {
        String nombreLugar = lugarRepositoryPort.findById(f.getIdLugar())
                .map(Lugar::getNombre)
                .orElse(null);
        return new FavoritoResponse(f.getIdUsuario(), f.getIdLugar(), nombreLugar, f.getFechaAgregado());
    }
}
