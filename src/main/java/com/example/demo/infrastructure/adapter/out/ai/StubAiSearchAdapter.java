package com.example.demo.infrastructure.adapter.out.ai;

import com.example.demo.domain.model.Lugar;
import com.example.demo.domain.port.AiSearchPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@ConditionalOnExpression("'${localspot.integrations.ai.api-key:}' == ''")
public class StubAiSearchAdapter implements AiSearchPort {

    @Override
    public List<RecomendacionIaItem> recomendarLugares(String consulta, List<Lugar> lugaresDisponibles) {
        return fallbackBusqueda(consulta, lugaresDisponibles);
    }

    public static List<RecomendacionIaItem> fallbackBusqueda(String consulta, List<Lugar> lugaresDisponibles) {
        if (consulta == null || consulta.isBlank() || lugaresDisponibles == null || lugaresDisponibles.isEmpty()) {
            return Collections.emptyList();
        }

        String[] palabras = consulta.toLowerCase()
                .replaceAll("[^a-záéíóúñ0-9\\s]", " ")
                .split("\\s+");

        Set<String> palabrasClave = new HashSet<>();
        for (String p : palabras) {
            if (p.length() > 2) {
                palabrasClave.add(p);
            }
        }

        record ScoredLugar(Lugar lugar, int score) {}
        List<ScoredLugar> puntuados = new ArrayList<>();

        for (Lugar l : lugaresDisponibles) {
            int score = 0;
            String nombre = l.getNombre() != null ? l.getNombre().toLowerCase() : "";
            String desc = l.getDescripcion() != null ? l.getDescripcion().toLowerCase() : "";
            String dir = l.getDireccion() != null ? l.getDireccion().toLowerCase() : "";

            for (String palabra : palabrasClave) {
                if (nombre.contains(palabra)) score += 5;
                if (desc.contains(palabra)) score += 3;
                if (dir.contains(palabra)) score += 1;
            }

            if (score > 0) {
                puntuados.add(new ScoredLugar(l, score));
            }
        }

        puntuados.sort(Comparator.comparingInt(ScoredLugar::score).reversed());

        List<RecomendacionIaItem> items = new ArrayList<>();
        int limite = Math.min(5, puntuados.size());
        for (int i = 0; i < limite; i++) {
            Lugar l = puntuados.get(i).lugar();
            String descResumida = l.getDescripcion() != null && l.getDescripcion().length() > 70
                    ? l.getDescripcion().substring(0, 70) + "..."
                    : (l.getDescripcion() != null ? l.getDescripcion() : "Ideal para conocer");
            items.add(new RecomendacionIaItem(
                    l.getIdLugar(),
                    "Coincide con tu búsqueda por su ambiente: " + descResumida
            ));
        }

        if (items.isEmpty()) {
            for (int i = 0; i < Math.min(3, lugaresDisponibles.size()); i++) {
                Lugar l = lugaresDisponibles.get(i);
                items.add(new RecomendacionIaItem(
                        l.getIdLugar(),
                        "Lugar destacado de la comunidad sugerido para tu búsqueda"
                ));
            }
        }

        return items;
    }
}
