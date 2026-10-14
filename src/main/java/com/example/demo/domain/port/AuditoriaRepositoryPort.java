package com.example.demo.domain.port;

import com.example.demo.domain.model.Auditoria;

public interface AuditoriaRepositoryPort {
    Auditoria save(Auditoria auditoria);
}
