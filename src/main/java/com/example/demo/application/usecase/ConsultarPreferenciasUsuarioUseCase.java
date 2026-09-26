package com.example.demo.application.usecase;

import com.example.demo.domain.exception.UsuarioNoEncontradoException;
import com.example.demo.domain.model.Preferencia;
import com.example.demo.domain.port.PreferenciaRepositoryPort;
import com.example.demo.domain.port.UsuarioRepositoryPort;

import java.util.List;

public class ConsultarPreferenciasUsuarioUseCase {

    private final PreferenciaRepositoryPort preferenciaRepositoryPort;
    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public ConsultarPreferenciasUsuarioUseCase(PreferenciaRepositoryPort preferenciaRepositoryPort,
                                               UsuarioRepositoryPort usuarioRepositoryPort) {
        this.preferenciaRepositoryPort = preferenciaRepositoryPort;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    public List<Preferencia> ejecutar(Long idUsuario) {
        if (usuarioRepositoryPort.findById(idUsuario).isEmpty()) {
            throw new UsuarioNoEncontradoException("Usuario no encontrado");
        }
        return preferenciaRepositoryPort.findByIdUsuario(idUsuario);
    }
}
