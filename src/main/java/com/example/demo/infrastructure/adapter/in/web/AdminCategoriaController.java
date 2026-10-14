package com.example.demo.infrastructure.adapter.in.web;

import com.example.demo.application.usecase.AdministrarCategoriasUseCase;
import com.example.demo.domain.model.Categoria;
import com.example.demo.infrastructure.adapter.in.web.dto.CategoriaRequest;
import com.example.demo.infrastructure.adapter.in.web.dto.CategoriaResponse;
import com.example.demo.infrastructure.config.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/categorias")
@Tag(name = "Admin Categorias", description = "Administrador: CRUD de categorias")
public class AdminCategoriaController {

    private final AdministrarCategoriasUseCase administrarCategoriasUseCase;

    public AdminCategoriaController(AdministrarCategoriasUseCase administrarCategoriasUseCase) {
        this.administrarCategoriasUseCase = administrarCategoriasUseCase;
    }

    @PostMapping
    @Operation(summary = "Crear categoria", description = "Crea una nueva categoria (admin).")
    public ResponseEntity<CategoriaResponse> crear(@Valid @RequestBody CategoriaRequest request) {
        Long idAdmin = SecurityUtils.getCurrentUsuarioId();
        Categoria categoria = administrarCategoriasUseCase.crear(idAdmin, request.getNombreCategoria());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(categoria));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoria", description = "Actualiza el nombre de una categoria (admin).")
    public ResponseEntity<CategoriaResponse> actualizar(@PathVariable Long id,
                                                            @Valid @RequestBody CategoriaRequest request) {
        Long idAdmin = SecurityUtils.getCurrentUsuarioId();
        Categoria categoria = administrarCategoriasUseCase.actualizar(idAdmin, id, request.getNombreCategoria());
        return ResponseEntity.ok(toResponse(categoria));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoria", description = "Elimina una categoria si no esta en uso (admin).")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Long idAdmin = SecurityUtils.getCurrentUsuarioId();
        administrarCategoriasUseCase.eliminar(idAdmin, id);
        return ResponseEntity.noContent().build();
    }

    private CategoriaResponse toResponse(Categoria categoria) {
        return new CategoriaResponse(categoria.getIdCategoria(), categoria.getNombreCategoria());
    }
}
