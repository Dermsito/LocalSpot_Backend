package com.example.demo.infrastructure.adapter.out.persistence.adapter;

import com.example.demo.domain.model.Auditoria;
import com.example.demo.domain.port.AuditoriaRepositoryPort;
import com.example.demo.infrastructure.adapter.out.persistence.entity.AuditoriaJpaEntity;
import com.example.demo.infrastructure.adapter.out.persistence.repository.AuditoriaJpaRepository;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaRepositoryAdapter implements AuditoriaRepositoryPort {

    private final AuditoriaJpaRepository auditoriaJpaRepository;

    public AuditoriaRepositoryAdapter(AuditoriaJpaRepository auditoriaJpaRepository) {
        this.auditoriaJpaRepository = auditoriaJpaRepository;
    }

    @Override
    public Auditoria save(Auditoria auditoria) {
        AuditoriaJpaEntity entity = toEntity(auditoria);
        AuditoriaJpaEntity saved = auditoriaJpaRepository.save(entity);
        return toDomain(saved);
    }

    private Auditoria toDomain(AuditoriaJpaEntity entity) {
        Auditoria a = new Auditoria();
        a.setIdAuditoria(entity.getIdAuditoria());
        a.setIdUsuario(entity.getIdUsuario());
        a.setEntidad(entity.getEntidad());
        a.setIdRegistro(entity.getIdRegistro());
        a.setAccion(entity.getAccion());
        a.setDatosAnteriores(entity.getDatosAnteriores());
        a.setDatosNuevos(entity.getDatosNuevos());
        a.setFechaAccion(entity.getFechaAccion());
        return a;
    }

    private AuditoriaJpaEntity toEntity(Auditoria a) {
        AuditoriaJpaEntity entity = new AuditoriaJpaEntity();
        entity.setIdAuditoria(a.getIdAuditoria());
        entity.setIdUsuario(a.getIdUsuario());
        entity.setEntidad(a.getEntidad());
        entity.setIdRegistro(a.getIdRegistro());
        entity.setAccion(a.getAccion());
        entity.setDatosAnteriores(a.getDatosAnteriores());
        entity.setDatosNuevos(a.getDatosNuevos());
        entity.setFechaAccion(a.getFechaAccion());
        return entity;
    }
}
