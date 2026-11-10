package com.example.demo.application.usecase;

import com.example.demo.domain.model.EmailCorreo;

final class EmailRecuperacionBuilder {

    private static final String ASUNTO = "Recuperación de contraseña en LocalSpot";

    private EmailRecuperacionBuilder() {
    }

    static EmailCorreo construir(String nombre, String destinatario, String token, String baseUrl) {
        String enlace = baseUrl + "/restablecer-contrasena?token=" + token;
        String cuerpo = "Hola " + nombre + ",\n\n"
                + "Hemos recibido una solicitud para restablecer la contraseña de tu cuenta en LocalSpot.\n\n"
                + "Haz clic en el siguiente enlace o cópialo en tu navegador para elegir una nueva contraseña:\n\n"
                + enlace + "\n\n"
                + "Este enlace tiene una validez de 2 horas. Si tú no solicitaste este cambio, puedes ignorar este correo de manera segura.\n\n"
                + "Equipo LocalSpot";
        return new EmailCorreo(destinatario, ASUNTO, cuerpo);
    }
}
