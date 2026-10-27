package com.example.demo.infrastructure.adapter.out.ai;

import com.example.demo.domain.model.Lugar;
import com.example.demo.domain.model.Preferencia;
import com.example.demo.domain.port.AiRecommendationPort;
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
import java.util.List;

/**
 * Adaptador real de recomendaciones con IA usando Google Gemini
 * (endpoint generativelanguage.googleapis.com, API generateContent).
 *
 * Solo se activa cuando AI_API_KEY tiene valor; en su ausencia se usa
 * {@link StubAiRecommendationAdapter}. Incluye CircuitBreaker + Retry con
 * motivo de respaldo si el servicio falla o no responde a tiempo.
 */
@Component
@ConditionalOnExpression("'${localspot.integrations.ai.api-key:}' != ''")
public class GeminiAiRecommendationAdapter implements AiRecommendationPort {

    private static final Logger log = LoggerFactory.getLogger(GeminiAiRecommendationAdapter.class);

    private static final int MAX_CARACTERES_MOTIVO = 200;
    private static final int MAX_TOKENS_RESPUESTA = 256;

    private final RestClient restClient;
    private final String apiKey;
    private final String apiUrl;
    private final String model;

    public GeminiAiRecommendationAdapter(
            @Value("${localspot.integrations.ai.api-key:}") String apiKey,
            @Value("${localspot.integrations.ai.api-url:https://generativelanguage.googleapis.com}") String apiUrl,
            @Value("${localspot.integrations.ai.model:gemini-1.5-flash}") String model) {
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.model = model;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(10));
        this.restClient = RestClient.builder().requestFactory(requestFactory).build();
    }

    @Override
    @CircuitBreaker(name = "aiService", fallbackMethod = "fallbackMotivo")
    @Retry(name = "aiService")
    public String generateMotivo(Lugar lugar, Preferencia preferencia) {
        URI uri = UriComponentsBuilder.fromUriString(apiUrl)
                .path("/v1beta/models/{model}:generateContent")
                .queryParam("key", apiKey)
                .buildAndExpand(model)
                .encode()
                .toUri();

        GeminiRequest request = new GeminiRequest(
                List.of(new Content(List.of(new Part(construirPrompt(lugar, preferencia))))),
                new GenerationConfig(MAX_TOKENS_RESPUESTA, 0.7));

        GeminiResponse response = restClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(GeminiResponse.class);

        String texto = extraerTexto(response);
        if (texto == null || texto.isBlank()) {
            throw new IllegalStateException("Gemini devolvio una respuesta vacia");
        }

        return recortar(texto.trim());
    }

    private String construirPrompt(Lugar lugar, Preferencia preferencia) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Eres un asistente de una app para descubrir lugares poco conocidos. ");
        prompt.append("Redacta en espanol una sola frase de maximo 200 caracteres que explique por que ")
                .append("este lugar es una buena recomendacion para el usuario. No uses comillas ni emojis.\n")
                .append("Lugar: ").append(lugar.getNombre()).append("\n");
        if (lugar.getDescripcion() != null && !lugar.getDescripcion().isBlank()) {
            prompt.append("Descripcion: ").append(lugar.getDescripcion()).append("\n");
        }
        if (lugar.getPrecioMin() != null && lugar.getPrecioMax() != null) {
            prompt.append("Rango de precios: ").append(lugar.getPrecioMin())
                    .append(" - ").append(lugar.getPrecioMax()).append("\n");
        }
        if (preferencia != null) {
            prompt.append("Categoria preferida por el usuario: ").append(preferencia.getIdCategoria()).append("\n");
            if (preferencia.getPresupuestoMax() != null) {
                prompt.append("Presupuesto maximo del usuario: ").append(preferencia.getPresupuestoMax());
            }
        }
        return prompt.toString();
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

    private String recortar(String motivo) {
        return motivo.length() > MAX_CARACTERES_MOTIVO ? motivo.substring(0, MAX_CARACTERES_MOTIVO) : motivo;
    }

    @SuppressWarnings("unused")
    public String fallbackMotivo(Lugar lugar, Preferencia preferencia, Throwable ex) {
        log.warn("Fallo el servicio de IA ({}), usando motivo por defecto", ex.getMessage());
        return "Lugar poco conocido que coincide con tu preferencia dentro de tu presupuesto.";
    }

    // --- Estructuras de la API generateContent de Gemini ---

    record GeminiRequest(List<Content> contents, GenerationConfig generationConfig) {
    }

    record Content(List<Part> parts) {
    }

    record Part(String text) {
    }

    record GenerationConfig(Integer maxOutputTokens, Double temperature) {
    }

    record GeminiResponse(List<Candidate> candidates) {
    }

    record Candidate(Content content) {
    }
}
