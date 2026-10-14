package com.example.demo.infrastructure.adapter.out.geo;

import com.example.demo.domain.model.Coordenadas;
import com.example.demo.domain.port.GeocodingPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Adaptador stub para geocodificacion.
 * Si la ubicacion viene en formato "latitud,longitud" se parsea directamente.
 * De lo contrario devuelve coordenadas por defecto (Bogota).
 * Incluye CircuitBreaker y Retry.
 */
@Component
public class StubGeocodingAdapter implements GeocodingPort {

    private static final Logger log = LoggerFactory.getLogger(StubGeocodingAdapter.class);

    private static final BigDecimal DEFAULT_LAT = new BigDecimal("4.7110");
    private static final BigDecimal DEFAULT_LON = new BigDecimal("-74.0721");
    private static final BigDecimal DEFAULT_RADIO = new BigDecimal("5");

    @Override
    @CircuitBreaker(name = "geoService", fallbackMethod = "fallbackGeocode")
    @Retry(name = "geoService")
    public Coordenadas geocode(String ubicacion) {
        if (ubicacion == null || ubicacion.isBlank()) {
            return new Coordenadas(DEFAULT_LAT, DEFAULT_LON, DEFAULT_RADIO);
        }

        // Intentar parsear "latitud,longitud"
        String trimmed = ubicacion.trim();
        String[] partes = trimmed.split(",");
        if (partes.length == 2) {
            try {
                BigDecimal lat = new BigDecimal(partes[0].trim());
                BigDecimal lon = new BigDecimal(partes[1].trim());
                return new Coordenadas(lat, lon, DEFAULT_RADIO);
            } catch (NumberFormatException e) {
                log.debug("No se pudo parsear coordenadas de: {}", ubicacion);
            }
        }

        // En una implementacion real se consultaria la API de mapas aqui.
        // Por defecto devolvemos Bogota.
        return new Coordenadas(DEFAULT_LAT, DEFAULT_LON, DEFAULT_RADIO);
    }

    @SuppressWarnings("unused")
    public Coordenadas fallbackGeocode(String ubicacion, Exception ex) {
        log.warn("Fallo servicio de geocodificacion, usando coordenadas por defecto: {}", ex.getMessage());
        return new Coordenadas(DEFAULT_LAT, DEFAULT_LON, DEFAULT_RADIO);
    }
}
