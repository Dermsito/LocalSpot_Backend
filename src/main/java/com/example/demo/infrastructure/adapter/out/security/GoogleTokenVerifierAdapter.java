package com.example.demo.infrastructure.adapter.out.security;

import com.example.demo.domain.port.GoogleTokenVerifierPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Component
public class GoogleTokenVerifierAdapter implements GoogleTokenVerifierPort {

    private static final Logger log = LoggerFactory.getLogger(GoogleTokenVerifierAdapter.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String googleClientId;

    public GoogleTokenVerifierAdapter(
            ObjectMapper objectMapper,
            @Value("${localspot.auth.google.client-id:420642326248-r2a1tunes509kl0rfjo3sj3as33s2jcq.apps.googleusercontent.com}") String googleClientId) {
        this.objectMapper = objectMapper;
        this.googleClientId = googleClientId;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(4));
        requestFactory.setReadTimeout(Duration.ofSeconds(6));
        this.restClient = RestClient.builder().requestFactory(requestFactory).build();
    }

    @Override
    public PerfilGoogle verify(String idToken) {
        if (idToken == null || idToken.isBlank()) {
            throw new IllegalArgumentException("El token de Google es obligatorio");
        }

        try {
            String rawJson = restClient.get()
                    .uri("https://oauth2.googleapis.com/tokeninfo?id_token={idToken}", idToken)
                    .retrieve()
                    .body(String.class);

            if (rawJson == null || rawJson.isBlank()) {
                throw new IllegalStateException("Respuesta vacía al validar token con Google");
            }

            JsonNode node = objectMapper.readTree(rawJson);

            if (node.hasNonNull("error") || node.hasNonNull("error_description")) {
                String error = node.hasNonNull("error_description")
                        ? node.get("error_description").asText()
                        : node.get("error").asText();
                throw new IllegalArgumentException("Token de Google inválido: " + error);
            }

            String aud = node.hasNonNull("aud") ? node.get("aud").asText() : "";
            if (!googleClientId.isBlank() && !googleClientId.equals(aud)) {
                log.warn("El token aud [{}] no coincide con el cliente configurado [{}]", aud, googleClientId);
                throw new IllegalArgumentException("El token de Google no pertenece a esta aplicación");
            }

            String email = node.hasNonNull("email") ? node.get("email").asText() : null;
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("El token de Google no contiene correo electrónico");
            }

            String name = node.hasNonNull("name") ? node.get("name").asText() : email.split("@")[0];
            boolean emailVerified = node.hasNonNull("email_verified") && Boolean.parseBoolean(node.get("email_verified").asText());

            return new PerfilGoogle(email.toLowerCase().trim(), name, emailVerified);
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Fallo la comunicacion con Google OAuth: {}", ex.getMessage());
            throw new IllegalStateException("Error al autenticar con los servidores de Google: " + ex.getMessage(), ex);
        }
    }
}
