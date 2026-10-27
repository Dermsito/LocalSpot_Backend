package com.example.demo.domain.port;

import com.example.demo.domain.model.EmailCorreo;

/**
 * Puerto de salida para el envio de correos electronicos.
 * La implementacion concreta (SMTP, API de terceros, etc.) vive en infraestructura.
 */
public interface EmailPort {

    /**
     * Envia un correo electronico.
     *
     * @param correo mensaje a enviar
     */
    void enviar(EmailCorreo correo);
}
