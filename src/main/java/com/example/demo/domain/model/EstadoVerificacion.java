package com.example.demo.domain.model;

/**
 * Estados de verificacion de un lugar.
 * PENDIENTE: creado por propietario, sin aprobar por admin
 * VERIFICADO: aprobado por admin, visible publicamente
 * RECHAZADO: denegado por admin
 */
public enum EstadoVerificacion {
    PENDIENTE,
    VERIFICADO,
    RECHAZADO
}
