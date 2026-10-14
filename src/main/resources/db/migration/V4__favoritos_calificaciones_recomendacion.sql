-- ============================================================
-- V4__favoritos_calificaciones_recomendacion.sql
-- Tablas de favoritos (usuario <-> lugar), calificaciones de
-- lugares y recomendaciones generadas para el usuario.
-- ============================================================

-- Tabla: favorito (PK compuesta)
CREATE TABLE IF NOT EXISTS favorito (
    id_usuario     BIGINT    NOT NULL,
    id_lugar       BIGINT    NOT NULL,
    fecha_agregado TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_favorito PRIMARY KEY (id_usuario, id_lugar),
    CONSTRAINT fk_favorito_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuarios (id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_favorito_lugar FOREIGN KEY (id_lugar)
        REFERENCES lugar (id_lugar) ON DELETE CASCADE
);

COMMENT ON TABLE favorito IS 'Lugares marcados como favoritos por el usuario';
COMMENT ON COLUMN favorito.id_usuario IS 'Usuario que marco el favorito';
COMMENT ON COLUMN favorito.id_lugar IS 'Lugar marcado como favorito';
COMMENT ON COLUMN favorito.fecha_agregado IS 'Fecha en que se agrego el favorito';

CREATE INDEX IF NOT EXISTS idx_favorito_usuario ON favorito (id_usuario);

-- Tabla: calificacion (una por usuario y lugar)
CREATE TABLE IF NOT EXISTS calificacion (
    id_calificacion     BIGSERIAL   NOT NULL,
    id_usuario          BIGINT      NOT NULL,
    id_lugar            BIGINT      NOT NULL,
    puntuacion          SMALLINT    NOT NULL,
    comentario          TEXT,
    visible             BOOLEAN     NOT NULL DEFAULT TRUE,
    fecha_registro      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP,
    CONSTRAINT pk_calificacion PRIMARY KEY (id_calificacion),
    CONSTRAINT uk_calificacion_usuario_lugar UNIQUE (id_usuario, id_lugar),
    CONSTRAINT fk_calificacion_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuarios (id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_calificacion_lugar FOREIGN KEY (id_lugar)
        REFERENCES lugar (id_lugar) ON DELETE CASCADE,
    CONSTRAINT ck_calificacion_puntuacion CHECK (puntuacion BETWEEN 1 AND 5)
);

COMMENT ON TABLE calificacion IS 'Calificaciones y comentarios de los usuarios sobre los lugares';
COMMENT ON COLUMN calificacion.id_calificacion IS 'Identificador unico de la calificacion';
COMMENT ON COLUMN calificacion.id_usuario IS 'Usuario que califico el lugar';
COMMENT ON COLUMN calificacion.id_lugar IS 'Lugar calificado';
COMMENT ON COLUMN calificacion.puntuacion IS 'Puntuacion de 1 a 5';
COMMENT ON COLUMN calificacion.comentario IS 'Comentario opcional del usuario';
COMMENT ON COLUMN calificacion.visible IS 'Indica si la calificacion es visible publicamente (moderacion)';
COMMENT ON COLUMN calificacion.fecha_registro IS 'Fecha de creacion de la calificacion';
COMMENT ON COLUMN calificacion.fecha_actualizacion IS 'Fecha de ultima edicion de la calificacion';

CREATE INDEX IF NOT EXISTS idx_calificaciones_lugar ON calificacion (id_lugar);
CREATE INDEX IF NOT EXISTS idx_calificaciones_usuario ON calificacion (id_usuario);

-- Tabla: recomendacion (una por usuario y lugar)
CREATE TABLE IF NOT EXISTS recomendacion (
    id_recomendacion   BIGSERIAL      NOT NULL,
    id_usuario         BIGINT         NOT NULL,
    id_lugar           BIGINT         NOT NULL,
    puntaje_afinidad   NUMERIC(5,2)   NOT NULL,
    motivo             VARCHAR(200)   NOT NULL,
    fecha_generacion   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_recomendacion PRIMARY KEY (id_recomendacion),
    CONSTRAINT uk_recomendacion_usuario_lugar UNIQUE (id_usuario, id_lugar),
    CONSTRAINT fk_recomendacion_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuarios (id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_recomendacion_lugar FOREIGN KEY (id_lugar)
        REFERENCES lugar (id_lugar) ON DELETE CASCADE,
    CONSTRAINT ck_recomendacion_puntaje CHECK (puntaje_afinidad BETWEEN 0 AND 100)
);

COMMENT ON TABLE recomendacion IS 'Recomendaciones de lugares generadas para el usuario';
COMMENT ON COLUMN recomendacion.id_recomendacion IS 'Identificador unico de la recomendacion';
COMMENT ON COLUMN recomendacion.id_usuario IS 'Usuario destinatario de la recomendacion';
COMMENT ON COLUMN recomendacion.id_lugar IS 'Lugar recomendado';
COMMENT ON COLUMN recomendacion.puntaje_afinidad IS 'Puntaje de afinidad de 0 a 100';
COMMENT ON COLUMN recomendacion.motivo IS 'Motivo de la recomendacion (max 200 caracteres)';
COMMENT ON COLUMN recomendacion.fecha_generacion IS 'Fecha de generacion de la recomendacion';

CREATE INDEX IF NOT EXISTS idx_recomendacion_usuario ON recomendacion (id_usuario);
