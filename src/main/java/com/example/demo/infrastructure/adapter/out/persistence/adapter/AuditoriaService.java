package com.example.demo.infrastructure.adapter.out.persistence.adapter;

import com.example.demo.domain.model.Auditoria;
import com.example.demo.domain.port.AuditoriaPort;
import com.example.demo.domain.port.AuditoriaRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Implementacion del puerto de auditoria. Registra acciones en la tabla auditoria.
 */
@Component
public class AuditoriaService implements AuditoriaPort {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaService.class);

    private final AuditoriaRepositoryPort auditoriaRepositoryPort;

    public AuditoriaService(AuditoriaRepositoryPort auditoriaRepositoryPort) {
        this.auditoriaRepositoryPort = auditoriaRepositoryPort;
    }

    @Override
    public void registrar(Long idUsuario, String entidad, Long idRegistro,
                          String accion, String datosAnteriores, String datosNuevos) {
        try {
            Auditoria auditoria = new Auditoria();
            auditoria.setIdUsuario(idUsuario);
            auditoria.setEntidad(entidad);
            auditoria.setIdRegistro(idRegistro);
            auditoria.setAccion(accion);
            auditoria.setDatosAnteriores(datosAnteriores);
            auditoria.setDatosNuevos(datosNuevos);
            auditoria.setFechaAccion(LocalDateTime.now());
            auditoriaRepositoryPort.save(auditoria);
        } catch (Exception e) {
            // La auditoria no debe romper la operacion principal
            log.warn("No se pudo registrar auditoria para {} {}: {}", entidad, accion, e.getMessage());
        }
    }
}
