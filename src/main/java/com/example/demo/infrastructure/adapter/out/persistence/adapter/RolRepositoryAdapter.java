package com.example.demo.infrastructure.adapter.out.persistence.adapter;

import com.example.demo.domain.model.Rol;
import com.example.demo.domain.port.RolRepositoryPort;
import com.example.demo.infrastructure.adapter.out.persistence.entity.RolJpaEntity;
import com.example.demo.infrastructure.adapter.out.persistence.repository.RolJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RolRepositoryAdapter implements RolRepositoryPort {

    private final RolJpaRepository rolJpaRepository;

    public RolRepositoryAdapter(RolJpaRepository rolJpaRepository) {
        this.rolJpaRepository = rolJpaRepository;
    }

    @Override
    public Optional<Rol> findById(Short id) {
        return rolJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Rol> findAll() {
        return rolJpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private Rol toDomain(RolJpaEntity entity) {
        return new Rol(entity.getId(), entity.getNombre(), entity.getDescripcion());
    }
}
