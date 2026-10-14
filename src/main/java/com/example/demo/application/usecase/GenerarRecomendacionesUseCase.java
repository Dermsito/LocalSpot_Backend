package com.example.demo.application.usecase;

import com.example.demo.domain.exception.RecomendacionSinPreferenciasException;
import com.example.demo.domain.model.Lugar;
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
import java.util.List;
import java.util.Optional;

/**
 * RF-09: Generar recomendaciones de lugares poco conocidos.
 * Requiere preferencias; filtra VERIFICADOS dentro del presupuesto;
 * calcula puntaje 0..100 priorizando baja popularidad y buena calificacion;
 * AiRecommendationPort genera el motivo (fallback por defecto).
 */
public class GenerarRecomendacionesUseCase {

    private final LugarRepositoryPort lugarRepositoryPort;
    private final PreferenciaRepositoryPort preferenciaRepositoryPort;
    private final RecomendacionRepositoryPort recomendacionRepositoryPort;
    private final CalificacionRepositoryPort calificacionRepositoryPort;
    private final AiRecommendationPort aiRecommendationPort;

    public GenerarRecomendacionesUseCase(LugarRepositoryPort lugarRepositoryPort,
                                          PreferenciaRepositoryPort preferenciaRepositoryPort,
                                          RecomendacionRepositoryPort recomendacionRepositoryPort,
                                          CalificacionRepositoryPort calificacionRepositoryPort,
                                          AiRecommendationPort aiRecommendationPort) {
        this.lugarRepositoryPort = lugarRepositoryPort;
        this.preferenciaRepositoryPort = preferenciaRepositoryPort;
        this.recomendacionRepositoryPort = recomendacionRepositoryPort;
        this.calificacionRepositoryPort = calificacionRepositoryPort;
        this.aiRecommendationPort = aiRecommendationPort;
    }

    public List<Recomendacion> ejecutar(Long idUsuario) {
        List<Preferencia> preferencias = preferenciaRepositoryPort.findByIdUsuario(idUsuario);
        if (preferencias == null || preferencias.isEmpty()) {
            throw new RecomendacionSinPreferenciasException(
                    "Necesitas configurar tus preferencias antes de generar recomendaciones");
        }

        List<Recomendacion> recomendaciones = new ArrayList<>();
        LocalDateTime ahora = LocalDateTime.now();

        for (Preferencia pref : preferencias) {
            // Buscar lugares VERIFICADOS de la categoria y dentro del presupuesto
            var pagina = lugarRepositoryPort.buscarConFiltros(
                    null, pref.getIdCategoria(), null, null, null,
                    null, pref.getPresupuestoMax(),
                    com.example.demo.domain.model.EstadoVerificacion.VERIFICADO,
                    new com.example.demo.domain.model.Paginacion(0, 100));

            for (Lugar lugar : pagina.content()) {
                double promedio = calificacionRepositoryPort.calcularPromedioByLugarId(lugar.getIdLugar());
                long cantidadCalificaciones = calificacionRepositoryPort.findByLugarIdAndVisibleTrue(lugar.getIdLugar()).size();

                // Puntaje de afinidad: prioriza buena calificacion (60%) y baja popularidad (40%)
                BigDecimal puntaje = calcularPuntaje(promedio, cantidadCalificaciones);

                // Buscar preferencia asociada (en este caso la de esta categoria)
                Preferencia preferenciaAsociada = preferencias.stream()
                        .filter(p -> p.getIdCategoria().equals(lugar.getIdCategoria()))
                        .findFirst()
                        .orElse(pref);

                String motivo;
                try {
                    motivo = aiRecommendationPort.generateMotivo(lugar, preferenciaAsociada);
                } catch (Exception e) {
                    motivo = "Lugar poco conocido con buena relacion calidad-precio, recomendado para ti.";
                }
                if (motivo != null && motivo.length() > 200) {
                    motivo = motivo.substring(0, 200);
                }

                // No repetir lugar por usuario: si ya existe, actualizar puntaje
                Optional<Recomendacion> existente =
                        recomendacionRepositoryPort.findByUsuarioIdAndLugarId(idUsuario, lugar.getIdLugar());

                Recomendacion recomendacion;
                if (existente.isPresent()) {
                    recomendacion = existente.get();
                    recomendacion.setPuntajeAfinidad(puntaje);
                    recomendacion.setMotivo(motivo);
                    recomendacion.setFechaGeneracion(ahora);
                } else {
                    recomendacion = new Recomendacion();
                    recomendacion.setIdUsuario(idUsuario);
                    recomendacion.setIdLugar(lugar.getIdLugar());
                    recomendacion.setPuntajeAfinidad(puntaje);
                    recomendacion.setMotivo(motivo);
                    recomendacion.setFechaGeneracion(ahora);
                }
                recomendaciones.add(recomendacionRepositoryPort.save(recomendacion));
            }
        }

        // Ordenar por puntaje descendente
        recomendaciones.sort(Comparator.comparing(Recomendacion::getPuntajeAfinidad).reversed());
        return recomendaciones;
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
}
