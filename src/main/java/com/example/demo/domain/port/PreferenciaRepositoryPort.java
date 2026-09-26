package com.example.demo.domain.port;

import com.example.demo.domain.model.Preferencia;

import java.util.List;
import java.util.Optional;

public interface PreferenciaRepositoryPort {
    Optional<Preferencia> findById(Long idPreferencia);
    List<Preferencia> findByIdUsuario(Long idUsuario);
    Optional<Preferencia> findByIdUsuarioAndIdCategoria(Long idUsuario, Long idCategoria);
    Preferencia save(Preferencia preferencia);
    void deleteById(Long idPreferencia);
    boolean existsById(Long idPreferencia);
}
