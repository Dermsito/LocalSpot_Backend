package com.example.demo.application.usecase;

import com.example.demo.domain.exception.OperacionNoAutorizadaException;
import com.example.demo.domain.exception.PreferenciaNoEncontradaException;
import com.example.demo.domain.port.PreferenciaRepositoryPort;

public class EliminarPreferenciaUseCase {

    private final PreferenciaRepositoryPort preferenciaRepositoryPort;

    public EliminarPreferenciaUseCase(PreferenciaRepositoryPort preferenciaRepositoryPort) {
        this.preferenciaRepositoryPort = preferenciaRepositoryPort;
    }

    public void ejecutar(Long idPreferencia, Long idUsuarioAutenticado) {
        var preferencia = preferenciaRepositoryPort.findById(idPreferencia)
                .orElseThrow(() -> new PreferenciaNoEncontradaException("Preferencia no encontrada: " + idPreferencia));

        if (!preferencia.getIdUsuario().equals(idUsuarioAutenticado)) {
            throw new OperacionNoAutorizadaException("No puedes eliminar preferencias de otros usuarios");
        }

        preferenciaRepositoryPort.deleteById(idPreferencia);
    }
}
