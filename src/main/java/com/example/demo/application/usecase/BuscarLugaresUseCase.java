package com.example.demo.application.usecase;

import com.example.demo.domain.model.EstadoVerificacion;
import com.example.demo.domain.model.Lugar;
import com.example.demo.domain.model.Pagina;
import com.example.demo.domain.model.Paginacion;
import com.example.demo.domain.port.LugarRepositoryPort;

import java.math.BigDecimal;

/**
 * RF-04 y RF-05: Buscar lugares con filtros combinables.
 * Solo devuelve lugares VERIFICADOS.
 */
public class BuscarLugaresUseCase {

    private final LugarRepositoryPort lugarRepositoryPort;

    public BuscarLugaresUseCase(LugarRepositoryPort lugarRepositoryPort) {
        this.lugarRepositoryPort = lugarRepositoryPort;
    }

    public Pagina<Lugar> ejecutar(String q, Long categoriaId, BigDecimal latitud,
                                   BigDecimal longitud, BigDecimal radioKm,
                                   BigDecimal precioMin, BigDecimal precioMax,
                                   Paginacion paginacion) {
        if (precioMin != null && precioMax != null && precioMin.compareTo(precioMax) > 0) {
            throw new IllegalArgumentException("El precio minimo no puede ser mayor al precio maximo");
        }
        // Solo lugares VERIFICADOS aparecen en busqueda publica
        return lugarRepositoryPort.buscarConFiltros(q, categoriaId, latitud, longitud, radioKm,
                precioMin, precioMax, EstadoVerificacion.VERIFICADO, paginacion);
    }
}
