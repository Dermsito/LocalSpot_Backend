package com.example.demo.domain.port;

import com.example.demo.domain.model.Calificacion;

import java.util.List;
import java.util.Optional;

public interface CalificacionRepositoryPort {
    Calificacion save(Calificacion calificacion);
    Optional<Calificacion> findByUsuarioIdAndLugarId(Long idUsuario, Long idLugar);
    List<Calificacion> findByLugarIdAndVisibleTrue(Long idLugar);
    List<Calificacion> findByLugarId(Long idLugar);
    Optional<Calificacion> findById(Long idCalificacion);
    boolean existsByUsuarioIdAndLugarId(Long idUsuario, Long idLugar);
    void updateVisible(Long idCalificacion, boolean visible);
    double calcularPromedioByLugarId(Long idLugar);
}
