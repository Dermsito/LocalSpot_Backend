package com.example.demo.infrastructure.adapter.out.persistence.adapter;

import com.example.demo.domain.model.Categoria;
import com.example.demo.domain.port.CategoriaRepositoryPort;
import com.example.demo.infrastructure.adapter.out.persistence.entity.CategoriaJpaEntity;
import com.example.demo.infrastructure.adapter.out.persistence.repository.CategoriaJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CategoriaRepositoryAdapter implements CategoriaRepositoryPort {

    private final CategoriaJpaRepository categoriaJpaRepository;

    public CategoriaRepositoryAdapter(CategoriaJpaRepository categoriaJpaRepository) {
        this.categoriaJpaRepository = categoriaJpaRepository;
    }

    @Override
    public Optional<Categoria> findById(Long idCategoria) {
        return categoriaJpaRepository.findById(idCategoria)
                .map(this::toDomain);
    }

    @Override
    public List<Categoria> findAll() {
        return categoriaJpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Categoria save(Categoria categoria) {
        CategoriaJpaEntity entity = toEntity(categoria);
        CategoriaJpaEntity saved = categoriaJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public boolean existsById(Long idCategoria) {
        return categoriaJpaRepository.existsById(idCategoria);
    }

    private Categoria toDomain(CategoriaJpaEntity entity) {
        return new Categoria(entity.getIdCategoria(), entity.getNombreCategoria());
    }

    private CategoriaJpaEntity toEntity(Categoria categoria) {
        CategoriaJpaEntity entity = new CategoriaJpaEntity();
        entity.setIdCategoria(categoria.getIdCategoria());
        entity.setNombreCategoria(categoria.getNombreCategoria());
        return entity;
    }
}
