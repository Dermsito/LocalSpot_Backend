package com.example.demo.domain.port;

import com.example.demo.domain.model.Rol;

import java.util.List;
import java.util.Optional;

public interface RolRepositoryPort {
    Optional<Rol> findById(Short id);
    List<Rol> findAll();
}
