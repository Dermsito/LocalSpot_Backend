package com.example.demo.infrastructure.adapter.in.web;

import com.example.demo.domain.model.Categoria;
import com.example.demo.domain.port.CategoriaRepositoryPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias", description = "Endpoints publicos para consultar el catalogo de categorias")
public class CategoriaController {

    private final CategoriaRepositoryPort categoriaRepositoryPort;

    public CategoriaController(CategoriaRepositoryPort categoriaRepositoryPort) {
        this.categoriaRepositoryPort = categoriaRepositoryPort;
    }

    @GetMapping
    @Operation(summary = "Listar categorias",
            description = "Devuelve la lista completa de categorias disponibles (Comida, Entretenimiento, Cultura, Aire libre, Compras).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de categorias",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoriaResponse.class))))
    })
    public ResponseEntity<List<Map<String, Object>>> listar() {
        List<Categoria> categorias = categoriaRepositoryPort.findAll();
        List<Map<String, Object>> respuesta = categorias.stream()
                .map(c -> Map.<String, Object>of(
                        "idCategoria", c.getIdCategoria(),
                        "nombreCategoria", c.getNombreCategoria()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(respuesta);
    }

    public record CategoriaResponse(Long idCategoria, String nombreCategoria) {}
}
