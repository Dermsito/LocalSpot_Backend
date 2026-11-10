package com.example.demo.infrastructure.adapter.out.ai;

import com.example.demo.domain.model.Lugar;
import com.example.demo.domain.port.AiSearchPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
@ConditionalOnExpression("'${localspot.integrations.ai.api-key:}' != ''")
public class GeminiAiSearchAdapter implements AiSearchPort {

    private static final Logger log = LoggerFactory.getLogger(GeminiAiSearchAdapter.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String apiUrl;
    private final String model;

    public GeminiAiSearchAdapter(
            @Value("${localspot.integrations.ai.api-key:}") String apiKey,
            @Value("${localspot.integrations.ai.api-url:https://generativelanguage.googleapis.com}") String apiUrl,
            @Value("${localspot.integrations.ai.model:gemini-1.5-flash}") String model,
            ObjectMapper objectMapper) {
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.model = model;
        this.objectMapper = objectMapper;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(4));
        requestFactory.setReadTimeout(Duration.ofSeconds(12));
        this.restClient = RestClient.builder().requestFactory(requestFactory).build();
    }

    @Override
    @CircuitBreaker(name = "aiService", fallbackMethod = "fallbackRecomendar")
    @Retry(name = "aiService")
    public List<RecomendacionIaItem> recomendarLugares(String consulta, List<Lugar> lugaresDisponibles) {
        try {
            URI uri = UriComponentsBuilder.fromUriString(apiUrl)
                    .path("/v1beta/models/{model}:generateContent")
                    .queryParam("key", apiKey)
                    .buildAndExpand(model)
                    .encode()
                    .toUri();

            String prompt = construirPrompt(consulta, lugaresDisponibles);

            GeminiRequest request = new GeminiRequest(
                    List.of(new Content(List.of(new Part(prompt)))),
                    new GenerationConfig(512, 0.4));

            GeminiResponse response = restClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(GeminiResponse.class);

            String texto = extraerTexto(response);
            if (texto == null || texto.isBlank()) {
                return StubAiSearchAdapter.fallbackBusqueda(consulta, lugaresDisponibles);
            }

            return parsearRespuesta(texto, lugaresDisponibles);
        } catch (Exception ex) {
            log.warn("Error consultando Gemini AI para busqueda inteligente: {}. Usando fallback local.", ex.getMessage());
            return StubAiSearchAdapter.fallbackBusqueda(consulta, lugaresDisponibles);
        }
    }

    @SuppressWarnings("unused")
    public List<RecomendacionIaItem> fallbackRecomendar(String consulta, List<Lugar> lugaresDisponibles, Throwable ex) {
        log.warn("Circuit breaker activado en Gemini AI para busqueda ({}), usando fallback local", ex.getMessage());
        return StubAiSearchAdapter.fallbackBusqueda(consulta, lugaresDisponibles);
    }

    private String construirPrompt(String consulta, List<Lugar> lugares) {
        StringBuilder sb = new StringBuilder();
        sb.append("Eres un asistente inteligente de la app LocalSpot. El usuario busca: \"").append(consulta).append("\".\n");
        sb.append("A continuación tienes los lugares verificados disponibles en la plataforma:\n");

        for (Lugar l : lugares) {
            sb.append("- ID: ").append(l.getIdLugar())
                    .append(" | Nombre: ").append(l.getNombre());
            if (l.getDescripcion() != null && !l.getDescripcion().isBlank()) {
                sb.append(" | Descripcion: ").append(l.getDescripcion());
            }
            if (l.getPrecioMin() != null && l.getPrecioMax() != null) {
                sb.append(" | Precios: $").append(l.getPrecioMin()).append(" - $").append(l.getPrecioMax());
            }
            sb.append("\n");
        }

        sb.append("\nInstrucciones:\n");
        sb.append("1. Selecciona hasta 5 lugares que mejor respondan a lo que busca el usuario.\n");
        sb.append("2. Para cada lugar explica en una frase corta y convincente (máximo 120 caracteres, en español) por qué se lo recomiendas para su búsqueda.\n");
        sb.append("3. Responde ÚNICAMENTE un arreglo JSON con formato: [{\"idLugar\": <numero>, \"motivo\": \"<texto>\"}].\n");
        sb.append("No agregues texto explicativo ni bloques markdown fuera del JSON.");
        return sb.toString();
    }

    private List<RecomendacionIaItem> parsearRespuesta(String texto, List<Lugar> lugaresDisponibles) {
        try {
            String jsonLimpio = texto.trim();
            if (jsonLimpio.startsWith("```json")) {
                jsonLimpio = jsonLimpio.substring(7);
            } else if (jsonLimpio.startsWith("```")) {
                jsonLimpio = jsonLimpio.substring(3);
            }
            if (jsonLimpio.endsWith("```")) {
                jsonLimpio = jsonLimpio.substring(0, jsonLimpio.length() - 3);
            }
            jsonLimpio = jsonLimpio.trim();

            int startIndex = jsonLimpio.indexOf('[');
            int endIndex = jsonLimpio.lastIndexOf(']');
            if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                jsonLimpio = jsonLimpio.substring(startIndex, endIndex + 1);
            }

            JsonNode arrayNode = objectMapper.readTree(jsonLimpio);
            if (!arrayNode.isArray()) {
                return StubAiSearchAdapter.fallbackBusqueda(texto, lugaresDisponibles);
            }

            List<RecomendacionIaItem> resultado = new ArrayList<>();
            for (JsonNode itemNode : arrayNode) {
                if (itemNode.has("idLugar") && itemNode.has("motivo")) {
                    Long idLugar = itemNode.get("idLugar").asLong();
                    String motivo = itemNode.get("motivo").asText();
                    resultado.add(new RecomendacionIaItem(idLugar, motivo));
                }
            }
            return resultado.isEmpty() ? StubAiSearchAdapter.fallbackBusqueda(texto, lugaresDisponibles) : resultado;
        } catch (Exception e) {
            log.warn("No se pudo parsear respuesta JSON de Gemini AI: {}", e.getMessage());
            return StubAiSearchAdapter.fallbackBusqueda(texto, lugaresDisponibles);
        }
    }

    private String extraerTexto(GeminiResponse response) {
        if (response == null || response.candidates() == null || response.candidates().isEmpty()) {
            return null;
        }
        Content content = response.candidates().get(0).content();
        if (content == null || content.parts() == null || content.parts().isEmpty()) {
            return null;
        }
        return content.parts().get(0).text();
    }

    record GeminiRequest(List<Content> contents, GenerationConfig generationConfig) {}
    record Content(List<Part> parts) {}
    record Part(String text) {}
    record GenerationConfig(Integer maxOutputTokens, Double temperature) {}
    record GeminiResponse(List<Candidate> candidates) {}
    record Candidate(Content content) {}
}
