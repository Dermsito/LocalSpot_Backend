package com.example.demo.infrastructure.adapter.out.email;

import com.example.demo.domain.model.EmailCorreo;
import com.example.demo.domain.port.EmailPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
public class SmtpEmailAdapter implements EmailPort {

    private static final Logger log = LoggerFactory.getLogger(SmtpEmailAdapter.class);
    private static final String BREVO_ENDPOINT = "https://api.brevo.com/v3/smtp/email";

    private final JavaMailSender javaMailSender;
    private final String remitente;
    private final String brevoApiKey;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public SmtpEmailAdapter(JavaMailSender javaMailSender,
                            @Value("${localspot.verificacion.remitente:localspot.soporte@gmail.com}") String remitente,
                            @Value("${BREVO_API_KEY:}") String brevoApiKey,
                            ObjectMapper objectMapper) {
        this.javaMailSender = javaMailSender;
        this.remitente = remitente;
        this.brevoApiKey = brevoApiKey;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public void enviar(EmailCorreo correo) {
        if (brevoApiKey != null && !brevoApiKey.isBlank()) {
            enviarConBrevo(correo);
            return;
        }
        enviarConSmtp(correo);
    }

    private void enviarConBrevo(EmailCorreo correo) {
        try {
            Map<String, Object> payload = Map.of(
                    "sender", Map.of("name", "LocalSpot", "email", remitente),
                    "to", List.of(Map.of("email", correo.destinatario())),
                    "subject", correo.asunto(),
                    "textContent", correo.cuerpo()
            );
            String json = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BREVO_ENDPOINT))
                    .timeout(Duration.ofSeconds(15))
                    .header("accept", "application/json")
                    .header("content-type", "application/json")
                    .header("api-key", brevoApiKey.trim())
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("Correo enviado via Brevo a {}", correo.destinatario());
            } else {
                log.error("Brevo rechazo el envio a {} (HTTP {}): {}", correo.destinatario(), response.statusCode(), response.body());
            }
        } catch (Exception ex) {
            log.error("Error enviando correo via Brevo a {}: {}", correo.destinatario(), ex.getMessage());
        }
    }

    private void enviarConSmtp(EmailCorreo correo) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitente);
        mensaje.setTo(correo.destinatario());
        mensaje.setSubject(correo.asunto());
        mensaje.setText(correo.cuerpo());
        try {
            javaMailSender.send(mensaje);
            log.debug("Correo SMTP enviado a {}", correo.destinatario());
        } catch (Exception ex) {
            log.error("No se pudo enviar el correo SMTP a {}: {}", correo.destinatario(), ex.getMessage());
        }
    }
}