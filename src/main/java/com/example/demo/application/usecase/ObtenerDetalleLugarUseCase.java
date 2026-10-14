package com.example.demo.application.usecase;

import com.example.demo.domain.exception.LugarNoEncontradoException;
import com.example.demo.domain.model.Calificacion;
import com.example.demo.domain.model.FotoLugar;
import com.example.demo.domain.model.Lugar;
import com.example.demo.domain.port.CalificacionRepositoryPort;
import com.example.demo.domain.port.FotoLugarRepositoryPort;
import com.example.demo.domain.port.LugarRepositoryPort;

import java.util.List;

/**
 * RF-06: Obtener detalle de un lugar con fotos, promedio y comentarios visibles.
 * Solo lugares VERIFICADOS son visibles publicamente.
 */
public class ObtenerDetalleLugarUseCase {

    private final LugarRepositoryPort lugarRepositoryPort;
    private final FotoLugarRepositoryPort fotoLugarRepositoryPort;
    private final CalificacionRepositoryPort calificacionRepositoryPort;

    public ObtenerDetalleLugarUseCase(LugarRepositoryPort lugarRepositoryPort,
                                       FotoLugarRepositoryPort fotoLugarRepositoryPort,
                                       CalificacionRepositoryPort calificacionRepositoryPort) {
        this.lugarRepositoryPort = lugarRepositoryPort;
        this.fotoLugarRepositoryPort = fotoLugarRepositoryPort;
        this.calificacionRepositoryPort = calificacionRepositoryPort;
    }

    public DetalleLugar ejecutar(Long idLugar) {
        Lugar lugar = lugarRepositoryPort.findById(idLugar)
                .orElseThrow(() -> new LugarNoEncontradoException("Lugar no encontrado con id: " + idLugar));

        List<FotoLugar> fotos = fotoLugarRepositoryPort.findByLugarId(idLugar);
        List<Calificacion> calificaciones = calificacionRepositoryPort.findByLugarIdAndVisibleTrue(idLugar);
        double promedio = calificacionRepositoryPort.calcularPromedioByLugarId(idLugar);

        return new DetalleLugar(lugar, fotos, calificaciones, promedio);
    }

    public record DetalleLugar(Lugar lugar, List<FotoLugar> fotos,
                                List<Calificacion> calificaciones, double promedio) {
    }
}
