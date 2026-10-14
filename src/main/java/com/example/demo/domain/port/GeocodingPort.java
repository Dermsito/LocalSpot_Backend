package com.example.demo.domain.port;

import com.example.demo.domain.model.Coordenadas;

/**
 * Puerto para geocodificar una ubicacion (texto o coordenadas) a Coordenadas.
 */
public interface GeocodingPort {

    /**
     * Convierte una cadena de ubicacion en coordenadas geograficas.
     * Si la cadena tiene formato "lat,long" se parsea directamente;
     * de lo contrario se consulta un servicio de mapas.
     *
     * @param ubicacion texto de ubicacion o "latitud,longitud"
     * @return coordenadas con radio de busqueda
     */
    Coordenadas geocode(String ubicacion);
}
