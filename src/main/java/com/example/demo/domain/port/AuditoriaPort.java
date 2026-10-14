package com.example.demo.domain.port;

/**
 * Puerto de servicio para registrar acciones en la tabla de auditoria.
 * Simplifica la auditoria desde los casos de uso.
 */
public interface AuditoriaPort {

    /**
     * Registra una accion de auditoria.
     *
     * @param idUsuario       id del usuario que realiza la accion (puede ser null)
     * @param entidad         nombre de la entidad afectada (ej: "CALIFICACION")
     * @param idRegistro      id del registro afectado
     * @param accion          tipo de accion: CREATE, UPDATE o DELETE
     * @param datosAnteriores JSON con el estado anterior (puede ser null)
     * @param datosNuevos     JSON con el estado nuevo (puede ser null)
     */
    void registrar(Long idUsuario, String entidad, Long idRegistro,
                   String accion, String datosAnteriores, String datosNuevos);
}
