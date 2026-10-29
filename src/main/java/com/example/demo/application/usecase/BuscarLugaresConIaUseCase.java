package com.example.demo.application.usecase;

import com.example.demo.domain.model.EstadoVerificacion;
import com.example.demo.domain.model.Lugar;
import com.example.demo.domain.model.Pagina;
import com.example.demo.domain.model.Paginacion;
import com.example.demo.domain.port.AiSearchPort;
import com.example.demo.domain.port.LugarRepositoryPort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Recomienda lugares registrados a traves de Gemini AI basandose en la intencion
 * de busqueda expresada por el usuario en lenguaje natural.
 */
public class BuscarLugaresConIaUseCase {

    private final LugarRepositoryPort lugarRepositoryPort;
    private final AiSearchPort aiSearchPort;

    public record LugarRecomendado(Lugar lugar, String motivo) {}

    public BuscarLugaresConIaUseCase(LugarRepositoryPort lugarRepositoryPort, AiSearchPort aiSearchPort) {
        this.lugarRepositoryPort = lugarRepositoryPort;
        this.aiSearchPort = aiSearchPort;
    }

    public List<LugarRecomendado> ejecutar(String consulta) {
        if (consulta == null || consulta.isBlank()) {
            return Collections.emptyList();
        }

        // Obtener todos los lugares verificados disponibles en el sistema
        Pagina<Lugar> pagina = lugarRepositoryPort.buscarConFiltros(
                null, null, null, null, null, null, null,
                EstadoVerificacion.VERIFICADO, new Paginacion(0, 200));

        List<Lugar> lugares = pagina.content();
        if (lugares.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Lugar> lugaresPorId = lugares.stream()
                .collect(Collectors.toMap(Lugar::getIdLugar, Function.identity(), (a, b) -> a));

        List<AiSearchPort.RecomendacionIaItem> recomendaciones = aiSearchPort.recomendarLugares(consulta.trim(), lugares);
        List<LugarRecomendado> resultados = new ArrayList<>();

        for (AiSearchPort.RecomendacionIaItem item : recomendaciones) {
            Lugar lugar = lugaresPorId.get(item.idLugar());
            if (lugar != null) {
                resultados.add(new LugarRecomendado(lugar, item.motivo()));
            }
        }

        return resultados;
    }
}
