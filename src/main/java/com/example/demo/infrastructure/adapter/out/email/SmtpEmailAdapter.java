package com.example.demo.infrastructure.adapter.out.email;

import com.example.demo.domain.model.EmailCorreo;
import com.example.demo.domain.port.EmailPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Adaptador de correo via SMTP (spring-boot-starter-mail).
 * En desarrollo apunta a Mailpit (MAIL_HOST / MAIL_PORT) y en produccion
 * a cualquier servidor SMTP configurado por variables de entorno.
 *
 * Un fallo de envio no interrumpe el flujo de negocio (el usuario puede
 * solicitar el reenvio), pero se registra como error para diagnostico.
 */
@Component
public class SmtpEmailAdapter implements EmailPort {

    private static final Logger log = LoggerFactory.getLogger(SmtpEmailAdapter.class);

    private final JavaMailSender javaMailSender;
    private final String remitente;

    public SmtpEmailAdapter(JavaMailSender javaMailSender,
                            @Value("${localspot.verificacion.remitente:no-reply@localspot.test}") String remitente) {
        this.javaMailSender = javaMailSender;
        this.remitente = remitente;
    }

    @Override
    public void enviar(EmailCorreo correo) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitente);
        mensaje.setTo(correo.destinatario());
        mensaje.setSubject(correo.asunto());
        mensaje.setText(correo.cuerpo());
        try {
            javaMailSender.send(mensaje);
            log.debug("Correo enviado a {}", correo.destinatario());
        } catch (Exception ex) {
            log.error("No se pudo enviar el correo a {}: {}", correo.destinatario(), ex.getMessage());
        }
    }
}
