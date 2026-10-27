package com.example.demo.infrastructure.adapter.out.ai;

import com.example.demo.domain.model.Lugar;
import com.example.demo.domain.model.Preferencia;
import com.example.demo.domain.port.AiRecommendationPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * Adaptador stub para recomendaciones con IA, activo solo cuando AI_API_KEY
 * esta vacio.
 * Devuelve un motivo generado localmente para poder trabajar sin clave de API.
 * Incluye CircuitBreaker y Retry para resiliencia.
 */
@Component
@ConditionalOnExpression("'${localspot.integrations.ai.api-key:}' == ''")
public class StubAiRecommendationAdapter implements AiRecommendationPort {

    private static final Logger log = LoggerFactory.getLogger(StubAiRecommendationAdapter.class);

    @Override
    @CircuitBreaker(name = "aiService", fallbackMethod = "fallbackMotivo")
    @Retry(name = "aiService")
    public String generateMotivo(Lugar lugar, Preferencia preferencia) {
        // Simulacion de generacion de motivo por IA
        String motivo = String.format(
                "Recomendado por su buena calificacion y baja popularidad. Categoria acorde a tus preferencias de %s.",
                preferencia != null ? "categoria " + preferencia.getIdCategoria() : "tu perfil");
        if (motivo.length() > 200) {
            motivo = motivo.substring(0, 200);
        }
        return motivo;
    }

    @SuppressWarnings("unused")
    public String fallbackMotivo(Lugar lugar, Preferencia preferencia, Throwable ex) {
        log.warn("Fallo servicio de IA, usando motivo por defecto: {}", ex.getMessage());
        return "Lugar poco conocido que coincide con tu preferencia dentro de tu presupuesto.";
    }
}
