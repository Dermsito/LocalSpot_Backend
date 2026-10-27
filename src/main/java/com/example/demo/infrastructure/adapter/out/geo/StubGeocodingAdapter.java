package com.example.demo.infrastructure.adapter.out.geo;

import com.example.demo.domain.model.Coordenadas;
import com.example.demo.domain.port.GeocodingPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Adaptador stub para geocodificacion, activo solo cuando MAPS_API_KEY esta vacio.
 * Si la ubicacion viene en formato "latitud,longitud" se parsea directamente.
 * De lo contrario devuelve el centro por defecto configurado
 * (por defecto, Fusagasuga, Colombia).
 */
@Component
@ConditionalOnExpression("'${localspot.integrations.maps.api-key:}' == ''")
public class StubGeocodingAdapter implements GeocodingPort {

    private static final Logger log = LoggerFactory.getLogger(StubGeocodingAdapter.class);

    private final BigDecimal latitudPorDefecto;
    private final BigDecimal longitudPorDefecto;
    private final BigDecimal radioPorDefecto;

    public StubGeocodingAdapter(
            @Value("${localspot.integrations.maps.default-lat:4.3372}") BigDecimal latitudPorDefecto,
            @Value("${localspot.integrations.maps.default-lon:-74.3640}") BigDecimal longitudPorDefecto,
            @Value("${localspot.integrations.maps.default-radio-km:5}") BigDecimal radioPorDefecto) {
        this.latitudPorDefecto = latitudPorDefecto;
        this.longitudPorDefecto = longitudPorDefecto;
        this.radioPorDefecto = radioPorDefecto;
    }

    @Override
    @CircuitBreaker(name = "geoService", fallbackMethod = "fallbackGeocode")
    @Retry(name = "geoService")
    public Coordenadas geocode(String ubicacion) {
        if (ubicacion == null || ubicacion.isBlank()) {
            return coordsPorDefecto();
        }

        // Intentar parsear "latitud,longitud"
        String[] partes = ubicacion.trim().split(",");
        if (partes.length == 2) {
            try {
                BigDecimal lat = new BigDecimal(partes[0].trim());
                BigDecimal lon = new BigDecimal(partes[1].trim());
                return new Coordenadas(lat, lon, radioPorDefecto);
            } catch (NumberFormatException e) {
                log.debug("No se pudo parsear coordenadas de: {}", ubicacion);
            }
        }

        // Sin MAPS_API_KEY no hay geocodificacion real: se usa el centro por defecto.
        log.debug("Geocodificacion stub (sin MAPS_API_KEY) para '{}'", ubicacion);
        return coordsPorDefecto();
    }

    private Coordenadas coordsPorDefecto() {
        return new Coordenadas(latitudPorDefecto, longitudPorDefecto, radioPorDefecto);
    }

    @SuppressWarnings("unused")
    public Coordenadas fallbackGeocode(String ubicacion, Throwable ex) {
        log.warn("Fallo servicio de geocodificacion, usando coordenadas por defecto: {}", ex.getMessage());
        return coordsPorDefecto();
    }
}
