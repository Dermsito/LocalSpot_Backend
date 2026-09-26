package com.example.demo.application.usecase;

import com.example.demo.domain.exception.OperacionNoAutorizadaException;
import com.example.demo.domain.exception.PreferenciaNoEncontradaException;
import com.example.demo.domain.model.Preferencia;
import com.example.demo.domain.port.PreferenciaRepositoryPort;

import java.math.BigDecimal;

public class ActualizarPreferenciaUseCase {

    private final PreferenciaRepositoryPort preferenciaRepositoryPort;

    public ActualizarPreferenciaUseCase(PreferenciaRepositoryPort preferenciaRepositoryPort) {
        this.preferenciaRepositoryPort = preferenciaRepositoryPort;
    }

    public Preferencia ejecutar(Long idPreferencia, Long idUsuarioAutenticado,
                                Long idCategoria, BigDecimal presupuestoMax) {
        Preferencia existente = preferenciaRepositoryPort.findById(idPreferencia)
                .orElseThrow(() -> new PreferenciaNoEncontradaException("Preferencia no encontrada"));

        if (!existente.getIdUsuario().equals(idUsuarioAutenticado)) {
            throw new OperacionNoAutorizadaException("No tiene permiso para modificar esta preferencia");
        }

        if (idCategoria != null) {
            existente.setIdCategoria(idCategoria);
        }
        if (presupuestoMax != null) {
            existente.setPresupuestoMax(presupuestoMax);
        }

        return preferenciaRepositoryPort.save(existente);
    }
}
