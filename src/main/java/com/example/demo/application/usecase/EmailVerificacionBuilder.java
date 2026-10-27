package com.example.demo.application.usecase;

import com.example.demo.domain.model.EmailCorreo;

/**
 * Construye el correo de verificacion enviado al registrarse o al reenviar la
 * verificacion.
 */
final class EmailVerificacionBuilder {

    private static final String ASUNTO = "Verifica tu correo en LocalSpot";

    private EmailVerificacionBuilder() {
    }

    static EmailCorreo construir(String nombre, String destinatario, String token, String baseUrl) {
        String enlace = baseUrl + "/verificar-correo?token=" + token;
        String cuerpo = "Hola " + nombre + ",\n\n"
                + "Gracias por registrarte en LocalSpot. Para activar tu cuenta confirma tu correo:\n\n"
                + enlace + "\n\n"
                + "Si no creaste esta cuenta, puedes ignorar este mensaje.\n\n"
                + "Equipo LocalSpot";
        return new EmailCorreo(destinatario, ASUNTO, cuerpo);
    }
}
