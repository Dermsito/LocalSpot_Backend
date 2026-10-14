package com.example.demo.application.usecase;

import com.example.demo.domain.exception.CalificacionDuplicadaException;
import com.example.demo.domain.exception.CalificacionNoEncontradaException;
import com.example.demo.domain.exception.LugarNoEncontradoException;
import com.example.demo.domain.model.Calificacion;
import com.example.demo.domain.port.AuditoriaPort;
import com.example.demo.domain.port.CalificacionRepositoryPort;
import com.example.demo.domain.port.LugarRepositoryPort;

import java.time.LocalDateTime;

/**
 * RF-08: Calificar un lugar (1..5) y comentar.
 * Una por lugar, editable. La edicion queda en AUDITORIA.
 */
public class CalificarLugarUseCase {

    private final CalificacionRepositoryPort calificacionRepositoryPort;
    private final LugarRepositoryPort lugarRepositoryPort;
    private final AuditoriaPort auditoriaPort;

    public CalificarLugarUseCase(CalificacionRepositoryPort calificacionRepositoryPort,
                                  LugarRepositoryPort lugarRepositoryPort,
                                  AuditoriaPort auditoriaPort) {
        this.calificacionRepositoryPort = calificacionRepositoryPort;
        this.lugarRepositoryPort = lugarRepositoryPort;
        this.auditoriaPort = auditoriaPort;
    }

    public Calificacion crear(Long idUsuario, Long idLugar, Short puntuacion, String comentario) {
        validarPuntuacion(puntuacion);
        if (!lugarRepositoryPort.findById(idLugar).isPresent()) {
            throw new LugarNoEncontradoException("Lugar no encontrado con id: " + idLugar);
        }
        if (calificacionRepositoryPort.existsByUsuarioIdAndLugarId(idUsuario, idLugar)) {
            throw new CalificacionDuplicadaException("Ya calificaste este lugar. Usa PUT para editar.");
        }

        Calificacion calificacion = new Calificacion();
        calificacion.setIdUsuario(idUsuario);
        calificacion.setIdLugar(idLugar);
        calificacion.setPuntuacion(puntuacion);
        calificacion.setComentario(comentario);
        calificacion.setVisible(true);
        calificacion.setFechaRegistro(LocalDateTime.now());
        calificacion.setFechaActualizacion(LocalDateTime.now());

        Calificacion guardada = calificacionRepositoryPort.save(calificacion);
        auditoriaPort.registrar(idUsuario, "CALIFICACION", guardada.getIdCalificacion(), "CREATE", null,
                "puntuacion=" + puntuacion);
        return guardada;
    }

    public Calificacion editar(Long idUsuario, Long idLugar, Short puntuacion, String comentario) {
        validarPuntuacion(puntuacion);
        Calificacion existente = calificacionRepositoryPort.findByUsuarioIdAndLugarId(idUsuario, idLugar)
                .orElseThrow(() -> new CalificacionNoEncontradaException("No tienes una calificacion para este lugar"));

        String datosAnteriores = "puntuacion=" + existente.getPuntuacion() + ",comentario=" + existente.getComentario();

        existente.setPuntuacion(puntuacion);
        existente.setComentario(comentario);
        existente.setFechaActualizacion(LocalDateTime.now());

        Calificacion guardada = calificacionRepositoryPort.save(existente);
        auditoriaPort.registrar(idUsuario, "CALIFICACION", guardada.getIdCalificacion(), "UPDATE",
                datosAnteriores, "puntuacion=" + puntuacion + ",comentario=" + comentario);
        return guardada;
    }

    private void validarPuntuacion(Short puntuacion) {
        if (puntuacion == null || puntuacion < 1 || puntuacion > 5) {
            throw new IllegalArgumentException("La puntuacion debe estar entre 1 y 5");
        }
    }
}
