package com.example.demo.domain.port;

import com.example.demo.domain.model.Lugar;
import com.example.demo.domain.model.Preferencia;

/**
 * Puerto para generar motivos de recomendacion mediante un servicio de IA.
 * Debe ser tolerante a fallos (circuit breaker + retry en el adaptador).
 */
public interface AiRecommendationPort {

    /**
     * Genera un motivo de recomendacion para un lugar dado una preferencia.
     *
     * @param lugar       lugar recomendado
     * @param preferencia preferencia del usuario
     * @return motivo de maximo 200 caracteres
     */
    String generateMotivo(Lugar lugar, Preferencia preferencia);
}
