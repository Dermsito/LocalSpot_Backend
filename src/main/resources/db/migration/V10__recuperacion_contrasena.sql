-- ============================================================
-- V10__recuperacion_contrasena.sql
-- Tabla para tokens de recuperacion de contrasena
-- ============================================================

CREATE TABLE IF NOT EXISTS recuperacion_contrasena (
    id_token         BIGSERIAL    NOT NULL,
    id_usuario       BIGINT       NOT NULL,
    token            VARCHAR(64)  NOT NULL,
    fecha_creacion   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_expiracion TIMESTAMP    NOT NULL,
    usado            BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_recuperacion_contrasena PRIMARY KEY (id_token),
    CONSTRAINT uk_recuperacion_contrasena_token UNIQUE (token),
    CONSTRAINT fk_recuperacion_contrasena_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuarios (id_usuario) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_recuperacion_contrasena_usuario
    ON recuperacion_contrasena (id_usuario);
