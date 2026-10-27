-- ============================================================
-- V9__verificacion_correo.sql
-- Verificacion de correo de usuarios:
--   - usuarios.verificado / usuarios.fecha_verificacion
--   - tabla verificacion_correo con tokens de un solo uso
-- Los usuarios ya existentes (admin y seeds de prueba) quedan
-- marcados como verificados para no bloquear su login.
-- ============================================================

ALTER TABLE usuarios
    ADD COLUMN IF NOT EXISTS verificado BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE usuarios
    ADD COLUMN IF NOT EXISTS fecha_verificacion TIMESTAMP;

COMMENT ON COLUMN usuarios.verificado IS 'Indica si el correo del usuario fue verificado';
COMMENT ON COLUMN usuarios.fecha_verificacion IS 'Fecha en que se verifico el correo del usuario';

-- Los usuarios preexistentes se consideran verificados (admin y datos de prueba)
UPDATE usuarios
SET verificado = TRUE,
    fecha_verificacion = CURRENT_TIMESTAMP
WHERE verificado = FALSE;

CREATE TABLE IF NOT EXISTS verificacion_correo (
    id_token         BIGSERIAL    NOT NULL,
    id_usuario       BIGINT       NOT NULL,
    token            VARCHAR(64)  NOT NULL,
    fecha_creacion   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_expiracion TIMESTAMP    NOT NULL,
    usado            BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_verificacion_correo PRIMARY KEY (id_token),
    CONSTRAINT uk_verificacion_correo_token UNIQUE (token),
    CONSTRAINT fk_verificacion_correo_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuarios (id_usuario) ON DELETE CASCADE
);

COMMENT ON TABLE verificacion_correo IS 'Tokens de verificacion de correo de usuarios';
COMMENT ON COLUMN verificacion_correo.id_token IS 'Identificador del token';
COMMENT ON COLUMN verificacion_correo.id_usuario IS 'Usuario al que pertenece el token (FK a usuarios.id_usuario)';
COMMENT ON COLUMN verificacion_correo.token IS 'Token unico enviado por correo';
COMMENT ON COLUMN verificacion_correo.fecha_creacion IS 'Fecha de creacion del token';
COMMENT ON COLUMN verificacion_correo.fecha_expiracion IS 'Fecha de expiracion del token';
COMMENT ON COLUMN verificacion_correo.usado IS 'Indica si el token ya fue consumido o invalidado';

CREATE INDEX IF NOT EXISTS idx_verificacion_correo_usuario
    ON verificacion_correo (id_usuario);
