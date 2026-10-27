package com.example.demo.application.usecase;

import com.example.demo.domain.exception.CategoriaEnUsoException;
import com.example.demo.domain.exception.CategoriaNoEncontradaException;
import com.example.demo.domain.model.Categoria;
import com.example.demo.domain.port.AuditoriaPort;
import com.example.demo.domain.port.CategoriaRepositoryPort;
import com.example.demo.domain.port.LugarRepositoryPort;

import java.util.List;

/**
 * RF-11: CRUD de categorias por administrador.
 * Nombre unico; no eliminar si esta en uso por algun lugar.
 */
public class AdministrarCategoriasUseCase {

    private final CategoriaRepositoryPort categoriaRepositoryPort;
    private final LugarRepositoryPort lugarRepositoryPort;
    private final AuditoriaPort auditoriaPort;

    public AdministrarCategoriasUseCase(CategoriaRepositoryPort categoriaRepositoryPort,
                                         LugarRepositoryPort lugarRepositoryPort,
                                         AuditoriaPort auditoriaPort) {
        this.categoriaRepositoryPort = categoriaRepositoryPort;
        this.lugarRepositoryPort = lugarRepositoryPort;
        this.auditoriaPort = auditoriaPort;
    }

    public List<Categoria> listar() {
        return categoriaRepositoryPort.findAll();
    }

    public Categoria crear(Long idAdmin, String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoria es obligatorio");
        }
        if (categoriaRepositoryPort.existsByNombreCategoria(nombre)) {
            throw new CategoriaEnUsoException("Ya existe una categoria con el nombre: " + nombre);
        }
        Categoria categoria = new Categoria(null, nombre);
        Categoria guardada = categoriaRepositoryPort.save(categoria);
        auditoriaPort.registrar(idAdmin, "CATEGORIA", guardada.getIdCategoria(), "CREATE", null,
                "nombre=" + nombre);
        return guardada;
    }

    public Categoria actualizar(Long idAdmin, Long idCategoria, String nombre) {
        Categoria categoria = categoriaRepositoryPort.findById(idCategoria)
                .orElseThrow(() -> new CategoriaNoEncontradaException("Categoria no encontrada con id: " + idCategoria));
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoria es obligatorio");
        }
        if (categoriaRepositoryPort.existsByNombreCategoriaAndIdNot(nombre, idCategoria)) {
            throw new CategoriaEnUsoException("Ya existe una categoria con el nombre: " + nombre);
        }
        categoria.setNombreCategoria(nombre);
        Categoria guardada = categoriaRepositoryPort.save(categoria);
        auditoriaPort.registrar(idAdmin, "CATEGORIA", idCategoria, "UPDATE", null, "nombre=" + nombre);
        return guardada;
    }

    public void eliminar(Long idAdmin, Long idCategoria) {
        if (!categoriaRepositoryPort.existsById(idCategoria)) {
            throw new CategoriaNoEncontradaException("Categoria no encontrada con id: " + idCategoria);
        }
        if (lugarRepositoryPort.existsByCategoriaId(idCategoria)) {
            throw new CategoriaEnUsoException("No se puede eliminar la categoria porque esta en uso por lugares");
        }
        categoriaRepositoryPort.deleteById(idCategoria);
        auditoriaPort.registrar(idAdmin, "CATEGORIA", idCategoria, "DELETE", null, null);
    }
}
