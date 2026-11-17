package com.example.demo.application.usecase;

import com.example.demo.domain.exception.RecomendacionSinPreferenciasException;
import com.example.demo.domain.model.EstadoVerificacion;
import com.example.demo.domain.model.Lugar;
import com.example.demo.domain.model.Paginacion;
import com.example.demo.domain.model.Preferencia;
import com.example.demo.domain.model.Recomendacion;
import com.example.demo.domain.port.AiRecommendationPort;
import com.example.demo.domain.port.CalificacionRepositoryPort;
import com.example.demo.domain.port.LugarRepositoryPort;
import com.example.demo.domain.port.PreferenciaRepositoryPort;
import com.example.demo.domain.port.RecomendacionRepositoryPort;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * RF-09: Generar recomendaciones de lugares poco conocidos.
 * Requiere preferencias; filtra VERIFICADOS dentro del presupuesto;
 * calcula puntaje 0..100 priorizando baja popularidad y buena calificacion;
 * AiRecommendationPort genera el motivo (fallback por defecto).
 *
 * <p>El puntaje se calcula primero para todos los candidatos y solo despues
 * se pide a la IA el motivo de los mejores ({@code limiteMotivosIa}); el resto
 * reutiliza un motivo por defecto. Asi se evita una llamada a la IA por cada
 * lugar, que era la causa principal de la latencia.</p>
 */
public class GenerarRecomendacionesUseCase {

    /** Limite por defecto de motivos redactados por IA en cada ejecucion. */
    static final int LIMITE_MOTIVOS_IA_POR_DEFECTO = 12;

    private static final String MOTIVO_POR_DEFECTO =
            "Lugar poco conocido con buena relacion calidad-precio, recomendado para ti.";

    private static final int MAX_CARACTERES_MOTIVO = 200;

    private final LugarRepositoryPort lugarRepositoryPort;
    private final PreferenciaRepositoryPort preferenciaRepositoryPort;
    private final RecomendacionRepositoryPort recomendacionRepositoryPort;
    private final CalificacionRepositoryPort calificacionRepositoryPort;
    private final AiRecommendationPort aiRecommendationPort;
    private final int limiteMotivosIa;

    public GenerarRecomendacionesUseCase(LugarRepositoryPort lugarRepositoryPort,
                                          PreferenciaRepositoryPort preferenciaRepositoryPort,
                                          RecomendacionRepositoryPort recomendacionRepositoryPort,
                                          CalificacionRepositoryPort calificacionRepositoryPort,
                                          AiRecommendationPort aiRecommendationPort,
                                          Integer limiteMotivosIa) {
        this.lugarRepositoryPort = lugarRepositoryPort;
        this.preferenciaRepositoryPort = preferenciaRepositoryPort;
        this.recomendacionRepositoryPort = recomendacionRepositoryPort;
        this.calificacionRepositoryPort = calificacionRepositoryPort;
        this.aiRecommendationPort = aiRecommendationPort;
        this.limiteMotivosIa = (limiteMotivosIa == null || limiteMotivosIa < 0)
                ? LIMITE_MOTIVOS_IA_POR_DEFECTO
                : limiteMotivosIa;
    }

    public List<Recomendacion> ejecutar(Long idUsuario) {
        List<Preferencia> preferencias = preferenciaRepositoryPort.findByIdUsuario(idUsuario);
        if (preferencias == null || preferencias.isEmpty()) {
            throw new RecomendacionSinPreferenciasException(
                    "Necesitas configurar tus preferencias antes de generar recomendaciones");
        }

        Map<Long, Preferencia> preferenciasPorCategoria = preferencias.stream()
                .filter(p -> p.getIdCategoria() != null)
                .collect(Collectors.toMap(Preferencia::getIdCategoria, Function.identity(), (a, b) -> a));

        // Fase 1: calcular candidatos y puntaje sin llamar todavia a la IA.
        Map<Long, Candidato> mejoresPorLugar = new LinkedHashMap<>();
        for (Preferencia pref : preferencias) {
            var pagina = lugarRepositoryPort.buscarConFiltros(
                    null, pref.getIdCategoria(), null, null, null,
                    null, pref.getPresupuestoMax(),
                    EstadoVerificacion.VERIFICADO, new Paginacion(0, 100));

            for (Lugar lugar : pagina.content()) {
                double promedio = calificacionRepositoryPort.calcularPromedioByLugarId(lugar.getIdLugar());
                long cantidadCalificaciones =
                        calificacionRepositoryPort.findByLugarIdAndVisibleTrue(lugar.getIdLugar()).size();
                BigDecimal puntaje = calcularPuntaje(promedio, cantidadCalificaciones);

                Preferencia preferenciaAsociada =
                        preferenciasPorCategoria.getOrDefault(lugar.getIdCategoria(), pref);

                // Si un lugar encaja con varias preferencias, se conserva el mejor puntaje.
                Candidato actual = mejoresPorLugar.get(lugar.getIdLugar());
                if (actual == null || puntaje.compareTo(actual.puntaje()) > 0) {
                    mejoresPorLugar.put(lugar.getIdLugar(),
                            new Candidato(lugar, preferenciaAsociada, puntaje));
                }
            }
        }

        // Fase 2: ordenar y pedir el motivo a la IA solo para los mejores.
        List<Candidato> candidatos = new ArrayList<>(mejoresPorLugar.values());
        candidatos.sort(Comparator.comparing(Candidato::puntaje).reversed());

        LocalDateTime ahora = LocalDateTime.now();
        List<Recomendacion> recomendaciones = new ArrayList<>();

        for (int i = 0; i < candidatos.size(); i++) {
            Candidato candidato = candidatos.get(i);
            String motivo = i < limiteMotivosIa
                    ? generarMotivoIa(candidato.lugar(), candidato.preferencia())
                    : MOTIVO_POR_DEFECTO;

            Recomendacion recomendacion = recomendacionRepositoryPort
                    .findByUsuarioIdAndLugarId(idUsuario, candidato.lugar().getIdLugar())
                    .orElseGet(Recomendacion::new);
            recomendacion.setIdUsuario(idUsuario);
            recomendacion.setIdLugar(candidato.lugar().getIdLugar());
            recomendacion.setPuntajeAfinidad(candidato.puntaje());
            recomendacion.setMotivo(motivo);
            recomendacion.setFechaGeneracion(ahora);

            recomendaciones.add(recomendacionRepositoryPort.save(recomendacion));
        }

        recomendaciones.sort(Comparator.comparing(Recomendacion::getPuntajeAfinidad).reversed());
        return recomendaciones;
    }

    private String generarMotivoIa(Lugar lugar, Preferencia preferencia) {
        try {
            String motivo = aiRecommendationPort.generateMotivo(lugar, preferencia);
            if (motivo == null || motivo.isBlank()) {
                return MOTIVO_POR_DEFECTO;
            }
            return motivo.length() > MAX_CARACTERES_MOTIVO
                    ? motivo.substring(0, MAX_CARACTERES_MOTIVO)
                    : motivo;
        } catch (Exception e) {
            return MOTIVO_POR_DEFECTO;
        }
    }

    private BigDecimal calcularPuntaje(double promedio, long cantidadCalificaciones) {
        // Componente de calificacion: 0-60 puntos (promedio de 1-5 -> 0-60)
        double componenteCalificacion = (promedio / 5.0) * 60.0;

        // Componente de popularidad: 0-40 puntos (menos calificaciones = mas puntos)
        // Usamos una escala logaritmica: 0 calificaciones = 40, muchas = cercano a 0
        double componentePopularidad = 40.0 / (1.0 + Math.log1p(cantidadCalificaciones));

        double total = componenteCalificacion + componentePopularidad;
        return BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP);
    }

    private record Candidato(Lugar lugar, Preferencia preferencia, BigDecimal puntaje) {
    }
}
