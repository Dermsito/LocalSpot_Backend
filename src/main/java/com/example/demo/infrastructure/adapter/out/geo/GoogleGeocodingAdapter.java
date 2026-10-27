package com.example.demo.infrastructure.adapter.out.geo;

import com.example.demo.domain.exception.GeocodificacionFallidaException;
import com.example.demo.domain.model.Coordenadas;
import com.example.demo.domain.port.GeocodingPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Duration;
import java.util.List;

/**
 * Adaptador real de geocodificacion usando la Google Geocoding API.
 * Solo se activa cuando MAPS_API_KEY tiene valor; en su ausencia se usa
 * {@link StubGeocodingAdapter}.
 *
 * Si la ubicacion llega como "latitud,longitud" se parsea directamente y no se
 * consume la API. Incluye CircuitBreaker + Retry con fallback a las coordenadas
 * por defecto configuradas (por defecto, Fusagasuga, Colombia).
 */
@Component
@ConditionalOnExpression("'${localspot.integrations.maps.api-key:}' != ''")
public class GoogleGeocodingAdapter implements GeocodingPort {

    private static final Logger log = LoggerFactory.getLogger(GoogleGeocodingAdapter.class);

    private final RestClient restClient;
    private final String apiKey;
    private final String apiUrl;
    private final BigDecimal latitudPorDefecto;
    private final BigDecimal longitudPorDefecto;
    private final BigDecimal radioPorDefecto;

    public GoogleGeocodingAdapter(
            @Value("${localspot.integrations.maps.api-key:}") String apiKey,
            @Value("${localspot.integrations.maps.api-url:https://maps.googleapis.com/maps/api/geocode/json}") String apiUrl,
            @Value("${localspot.integrations.maps.default-lat:4.3372}") BigDecimal latitudPorDefecto,
            @Value("${localspot.integrations.maps.default-lon:-74.3640}") BigDecimal longitudPorDefecto,
            @Value("${localspot.integrations.maps.default-radio-km:5}") BigDecimal radioPorDefecto) {
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.latitudPorDefecto = latitudPorDefecto;
        this.longitudPorDefecto = longitudPorDefecto;
        this.radioPorDefecto = radioPorDefecto;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));
        this.restClient = RestClient.builder().requestFactory(requestFactory).build();
    }

    @Override
    @CircuitBreaker(name = "geoService", fallbackMethod = "fallbackGeocode")
    @Retry(name = "geoService")
    public Coordenadas geocode(String ubicacion) {
        if (ubicacion == null || ubicacion.isBlank()) {
            throw new GeocodificacionFallidaException("La ubicacion no puede estar vacia");
        }

        Coordenadas directas = parsearCoordenadas(ubicacion);
        if (directas != null) {
            return directas;
        }

        URI uri = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("address", ubicacion)
                .queryParam("key", apiKey)
                .build()
                .encode()
                .toUri();

        GeoCodeApiResponse response = restClient.get()
                .uri(uri)
                .retrieve()
                .body(GeoCodeApiResponse.class);

        if (response == null || !"OK".equalsIgnoreCase(response.status())
                || response.results() == null || response.results().isEmpty()) {
            String status = response != null ? response.status() : "SIN_RESPUESTA";
            throw new GeocodificacionFallidaException(
                    "Google Geocoding no pudo resolver la ubicacion (status=" + status + ")");
        }

        Location location = response.results().get(0).geometry().location();
        if (location == null || location.lat() == null || location.lng() == null) {
            throw new GeocodificacionFallidaException("Google Geocoding devolvio coordenadas vacias");
        }

        log.debug("Geocodificacion exitosa para '{}'", ubicacion);
        return new Coordenadas(location.lat(), location.lng(), radioPorDefecto);
    }

    private Coordenadas parsearCoordenadas(String ubicacion) {
        String[] partes = ubicacion.trim().split(",");
        if (partes.length != 2) {
            return null;
        }
        try {
            BigDecimal lat = new BigDecimal(partes[0].trim());
            BigDecimal lon = new BigDecimal(partes[1].trim());
            return new Coordenadas(lat, lon, radioPorDefecto);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public Coordenadas fallbackGeocode(String ubicacion, Throwable ex) {
        log.warn("Fallo el servicio de geocodificacion ({}), usando coordenadas por defecto", ex.getMessage());
        return new Coordenadas(latitudPorDefecto, longitudPorDefecto, radioPorDefecto);
    }

    // --- Estructuras de la respuesta de Google Geocoding API ---

    record GeoCodeApiResponse(String status, List<Result> results) {
    }

    record Result(Geometry geometry) {
    }

    record Geometry(Location location) {
    }

    record Location(BigDecimal lat, BigDecimal lng) {
    }
}
