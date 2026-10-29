package com.example.demo.domain.port;

import com.example.demo.domain.model.Lugar;
import java.util.List;

/**
 * Puerto para buscar y recomendar lugares usando IA a partir de una consulta en lenguaje natural.
 */
public interface AiSearchPort {

    record RecomendacionIaItem(Long idLugar, String motivo) {}

    List<RecomendacionIaItem> recomendarLugares(String consulta, List<Lugar> lugaresDisponibles);
}
