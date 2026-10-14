-- ============================================================
-- V5__reporte_auditoria.sql
-- Tablas de reportes de calificaciones (moderacion) y auditoria
-- de acciones del sistema.
-- ============================================================

-- Tabla: reporte (un usuario reporta una calificacion)
CREATE TABLE IF NOT EXISTS reporte (
    id_reporte       BIGSERIAL     NOT NULL,
    id_calificacion  BIGINT        NOT NULL,
    id_usuario       BIGINT        NOT NULL,
    motivo           VARCHAR(200)  NOT NULL,
    estado           VARCHAR(20)   NOT NULL DEFAULT 'PENDIENTE',
    fecha_creacion   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_resolucion TIMESTAMP,
    CONSTRAINT pk_reporte PRIMARY KEY (id_reporte),
    CONSTRAINT uk_reporte_calif_usuario UNIQUE (id_calificacion, id_usuario),
    CONSTRAINT fk_reporte_calificacion FOREIGN KEY (id_calificacion)
        REFERENCES calificacion (id_calificacion) ON DELETE CASCADE,
    CONSTRAINT fk_reporte_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuarios (id_usuario) ON DELETE CASCADE,
    CONSTRAINT ck_reporte_estado CHECK (estado IN ('PENDIENTE', 'ACEPTADO', 'DESCARTADO'))
);

COMMENT ON TABLE reporte IS 'Reportes de calificaciones realizados por usuarios para moderacion';
COMMENT ON COLUMN reporte.id_reporte IS 'Identificador unico del reporte';
COMMENT ON COLUMN reporte.id_calificacion IS 'Calificacion reportada';
COMMENT ON COLUMN reporte.id_usuario IS 'Usuario que realizo el reporte';
COMMENT ON COLUMN reporte.motivo IS 'Motivo del reporte (max 200 caracteres)';
COMMENT ON COLUMN reporte.estado IS 'Estado del reporte: PENDIENTE, ACEPTADO o DESCARTADO';
COMMENT ON COLUMN reporte.fecha_creacion IS 'Fecha de creacion del reporte';
COMMENT ON COLUMN reporte.fecha_resolucion IS 'Fecha en que el admin resolvio el reporte';

CREATE INDEX IF NOT EXISTS idx_reporte_estado ON reporte (estado);
CREATE INDEX IF NOT EXISTS idx_reporte_calificacion ON reporte (id_calificacion);

-- Tabla: auditoria (registro de acciones CREATE/UPDATE/DELETE)
CREATE TABLE IF NOT EXISTS auditoria (
    id_auditoria     BIGSERIAL    NOT NULL,
    id_usuario       BIGINT,
    entidad          VARCHAR(50)  NOT NULL,
    id_registro      BIGINT,
    accion           VARCHAR(20)  NOT NULL,
    datos_anteriores TEXT,
    datos_nuevos     TEXT,
    fecha_accion     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_auditoria PRIMARY KEY (id_auditoria),
    CONSTRAINT fk_auditoria_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuarios (id_usuario) ON DELETE SET NULL,
    CONSTRAINT ck_auditoria_accion CHECK (accion IN ('CREATE', 'UPDATE', 'DELETE'))
);

COMMENT ON TABLE auditoria IS 'Registro de auditoria de acciones del sistema';
COMMENT ON COLUMN auditoria.id_auditoria IS 'Identificador unico del registro de auditoria';
COMMENT ON COLUMN auditoria.id_usuario IS 'Usuario que realizo la accion (NULL si no autenticado)';
COMMENT ON COLUMN auditoria.entidad IS 'Nombre de la entidad afectada (ej: LUGAR, CALIFICACION)';
COMMENT ON COLUMN auditoria.id_registro IS 'Identificador del registro afectado';
COMMENT ON COLUMN auditoria.accion IS 'Tipo de accion: CREATE, UPDATE o DELETE';
COMMENT ON COLUMN auditoria.datos_anteriores IS 'JSON con los datos antes de la accion';
COMMENT ON COLUMN auditoria.datos_nuevos IS 'JSON con los datos despues de la accion';
COMMENT ON COLUMN auditoria.fecha_accion IS 'Fecha y hora de la accion';

CREATE INDEX IF NOT EXISTS idx_auditoria_entidad ON auditoria (entidad);
CREATE INDEX IF NOT EXISTS idx_auditoria_usuario ON auditoria (id_usuario);
