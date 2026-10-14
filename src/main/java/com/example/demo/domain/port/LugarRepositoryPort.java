package com.example.demo.domain.port;

import com.example.demo.domain.model.EstadoVerificacion;
import com.example.demo.domain.model.Lugar;
import com.example.demo.domain.model.Pagina;
import com.example.demo.domain.model.Paginacion;

import java.math.BigDecimal;
import java.util.Optional;

public interface LugarRepositoryPort {
    Optional<Lugar> findById(Long idLugar);
    Lugar save(Lugar lugar);
    void deleteById(Long idLugar);
    boolean existsByCategoriaId(Long idCategoria);

    Pagina<Lugar> buscarConFiltros(String q, Long categoriaId, BigDecimal latitud,
                                   BigDecimal longitud, BigDecimal radioKm,
                                   BigDecimal precioMin, BigDecimal precioMax,
                                   EstadoVerificacion estadoVerificacion, Paginacion paginacion);
}
