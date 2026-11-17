-- ============================================================
-- V12__sistema_logging_bd.sql
-- Tablas para almacenamiento de logs HTTP y eventos del sistema
-- ============================================================

-- 1. Tabla: log_http (trazabilidad y accesos HTTP)
CREATE TABLE IF NOT EXISTS log_http (
    id_log_http      BIGSERIAL     NOT NULL,
    correlation_id   VARCHAR(64),
    metodo           VARCHAR(10)   NOT NULL,
    uri              VARCHAR(500)  NOT NULL,
    query_params     VARCHAR(1000),
    status_code      INT           NOT NULL,
    tiempo_ms        BIGINT        NOT NULL,
    ip_origen        VARCHAR(64),
    usuario_correo   VARCHAR(150),
    fecha_creacion   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_log_http PRIMARY KEY (id_log_http)
);

CREATE INDEX IF NOT EXISTS idx_log_http_correlation ON log_http (correlation_id);
CREATE INDEX IF NOT EXISTS idx_log_http_fecha ON log_http (fecha_creacion);
CREATE INDEX IF NOT EXISTS idx_log_http_status ON log_http (status_code);

COMMENT ON TABLE log_http IS 'Registro de peticiones HTTP para métricas, auditoría de acceso y análisis de rendimiento';

-- 2. Tabla: log_evento (errores, excepciones e incidentes de aplicación)
CREATE TABLE IF NOT EXISTS log_evento (
    id_log_evento    BIGSERIAL     NOT NULL,
    correlation_id   VARCHAR(64),
    nivel            VARCHAR(10)   NOT NULL,
    origen           VARCHAR(255)  NOT NULL,
    mensaje          TEXT          NOT NULL,
    detalles         TEXT,
    usuario_correo   VARCHAR(150),
    fecha_creacion   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_log_evento PRIMARY KEY (id_log_evento),
    CONSTRAINT ck_log_evento_nivel CHECK (nivel IN ('TRACE', 'DEBUG', 'INFO', 'WARN', 'ERROR'))
);

CREATE INDEX IF NOT EXISTS idx_log_evento_nivel ON log_evento (nivel);
CREATE INDEX IF NOT EXISTS idx_log_evento_fecha ON log_evento (fecha_creacion);
CREATE INDEX IF NOT EXISTS idx_log_evento_correlation ON log_evento (correlation_id);

COMMENT ON TABLE log_evento IS 'Registro persistente de errores y eventos clave de la aplicación';
