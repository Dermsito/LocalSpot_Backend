package com.example.demo.application.usecase;

import com.example.demo.domain.exception.CategoriaNoEncontradaException;
import com.example.demo.domain.exception.PreferenciaDuplicadaException;
import com.example.demo.domain.exception.UsuarioNoEncontradoException;
import com.example.demo.domain.model.Preferencia;
import com.example.demo.domain.port.CategoriaRepositoryPort;
import com.example.demo.domain.port.PreferenciaRepositoryPort;
import com.example.demo.domain.port.UsuarioRepositoryPort;

import java.math.BigDecimal;

public class CrearPreferenciaUseCase {

    private final PreferenciaRepositoryPort preferenciaRepositoryPort;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final CategoriaRepositoryPort categoriaRepositoryPort;

    public CrearPreferenciaUseCase(PreferenciaRepositoryPort preferenciaRepositoryPort,
                                   UsuarioRepositoryPort usuarioRepositoryPort,
                                   CategoriaRepositoryPort categoriaRepositoryPort) {
        this.preferenciaRepositoryPort = preferenciaRepositoryPort;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.categoriaRepositoryPort = categoriaRepositoryPort;
    }

    public Preferencia ejecutar(Long idUsuario, Long idCategoria, BigDecimal presupuestoMax) {
        if (usuarioRepositoryPort.findById(idUsuario).isEmpty()) {
            throw new UsuarioNoEncontradoException("Usuario no encontrado");
        }

        if (!categoriaRepositoryPort.existsById(idCategoria)) {
            throw new CategoriaNoEncontradaException("Categoria no encontrada");
        }

        if (preferenciaRepositoryPort.findByIdUsuarioAndIdCategoria(idUsuario, idCategoria).isPresent()) {
            throw new PreferenciaDuplicadaException(
                    "Ya existe una preferencia para la categoria con id: " + idCategoria);
        }

        Preferencia nueva = new Preferencia();
        nueva.setIdUsuario(idUsuario);
        nueva.setIdCategoria(idCategoria);
        nueva.setPresupuestoMax(presupuestoMax);
        return preferenciaRepositoryPort.save(nueva);
    }
}
