package com.example.demo.domain.port;

import com.example.demo.domain.model.Categoria;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepositoryPort {
    Optional<Categoria> findById(Long idCategoria);
    List<Categoria> findAll();
    Categoria save(Categoria categoria);
    boolean existsById(Long idCategoria);
}
