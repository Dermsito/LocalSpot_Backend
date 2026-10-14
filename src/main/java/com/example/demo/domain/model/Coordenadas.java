package com.example.demo.domain.model;

import java.math.BigDecimal;

/**
 * Coordenadas geograficas resultantes de geocodificar una ubicacion.
 *
 * @param latitud   latitud WGS84
 * @param longitud  longitud WGS84
 * @param radioKm   radio de busqueda en kilometros
 */
public record Coordenadas(BigDecimal latitud, BigDecimal longitud, BigDecimal radioKm) {
}
