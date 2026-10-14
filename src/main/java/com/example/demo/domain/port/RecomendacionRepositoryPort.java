package com.example.demo.domain.port;

import com.example.demo.domain.model.Recomendacion;

import java.util.List;
import java.util.Optional;

public interface RecomendacionRepositoryPort {
    Recomendacion save(Recomendacion recomendacion);
    List<Recomendacion> findByUsuarioIdOrderByPuntajeDesc(Long idUsuario);
    boolean existsByUsuarioIdAndLugarId(Long idUsuario, Long idLugar);
    Optional<Recomendacion> findByUsuarioIdAndLugarId(Long idUsuario, Long idLugar);
}
