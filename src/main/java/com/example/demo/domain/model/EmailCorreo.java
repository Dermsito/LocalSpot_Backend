package com.example.demo.domain.model;

/**
 * Mensaje de correo electronico a enviar por un proveedor externo (SMTP).
 *
 * @param destinatario correo del destinatario
 * @param asunto       asunto del mensaje
 * @param cuerpo       contenido en texto plano
 */
public record EmailCorreo(String destinatario, String asunto, String cuerpo) {
}
