-- ============================================================
-- V1__init_schema.sql
-- Esquema inicial de LocalSpot: usuarios, categorias, preferencias.
-- Constraints con nombres explicitos: pk_, uk_, fk_, ck_, idx_.
-- ============================================================

-- Tabla: usuarios (usuario final, administrador o propietario)
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario      BIGSERIAL      NOT NULL,
    nombre          VARCHAR(100)   NOT NULL,
    correo          VARCHAR(150)   NOT NULL,
    contrasena_hash VARCHAR(255)   NOT NULL,
    tipo_usuario    VARCHAR(20)    NOT NULL DEFAULT 'FINAL',
    fecha_registro  TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_usuarios PRIMARY KEY (id_usuario),
    CONSTRAINT uk_usuarios_correo UNIQUE (correo),
    CONSTRAINT ck_usuarios_tipo CHECK (tipo_usuario IN ('ADMINISTRADOR', 'FINAL', 'PROPIETARIO'))
);

COMMENT ON TABLE usuarios IS 'Usuarios de la plataforma LocalSpot';
COMMENT ON COLUMN usuarios.id_usuario IS 'Identificador unico del usuario';
COMMENT ON COLUMN usuarios.nombre IS 'Nombre completo del usuario';
COMMENT ON COLUMN usuarios.correo IS 'Correo electronico unico (login)';
COMMENT ON COLUMN usuarios.contrasena_hash IS 'Hash BCrypt de la contrasena';
COMMENT ON COLUMN usuarios.tipo_usuario IS 'Rol: ADMINISTRADOR, FINAL o PROPIETARIO (migrar a tabla rol en V2)';
COMMENT ON COLUMN usuarios.fecha_registro IS 'Fecha y hora de registro del usuario';

CREATE INDEX IF NOT EXISTS idx_usuarios_correo ON usuarios (correo);

-- Tabla: categorias (categoria de lugares)
CREATE TABLE IF NOT EXISTS categorias (
    id_categoria     BIGSERIAL     NOT NULL,
    nombre_categoria VARCHAR(80)   NOT NULL,
    CONSTRAINT pk_categorias PRIMARY KEY (id_categoria),
    CONSTRAINT uk_categorias_nombre UNIQUE (nombre_categoria)
);

COMMENT ON TABLE categorias IS 'Categorias de lugares disponibles en LocalSpot';
COMMENT ON COLUMN categorias.id_categoria IS 'Identificador unico de la categoria';
COMMENT ON COLUMN categorias.nombre_categoria IS 'Nombre de la categoria (max 80 caracteres)';

-- Tabla: preferencias (preferencias de presupuesto por categoria del usuario)
CREATE TABLE IF NOT EXISTS preferencias (
    id_preferencia  BIGSERIAL       NOT NULL,
    id_usuario      BIGINT          NOT NULL,
    id_categoria    BIGINT          NOT NULL,
    presupuesto_max NUMERIC(12,2)   NOT NULL,
    CONSTRAINT pk_preferencias PRIMARY KEY (id_preferencia),
    CONSTRAINT uk_preferencia_usuario_categoria UNIQUE (id_usuario, id_categoria),
    CONSTRAINT fk_preferencias_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuarios (id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_preferencias_categoria FOREIGN KEY (id_categoria)
        REFERENCES categorias (id_categoria) ON DELETE RESTRICT,
    CONSTRAINT ck_preferencias_presupuesto CHECK (presupuesto_max >= 0)
);

COMMENT ON TABLE preferencias IS 'Preferencias de presupuesto del usuario por categoria';
COMMENT ON COLUMN preferencias.id_preferencia IS 'Identificador unico de la preferencia';
COMMENT ON COLUMN preferencias.id_usuario IS 'Usuario dueno de la preferencia';
COMMENT ON COLUMN preferencias.id_categoria IS 'Categoria asociada a la preferencia';
COMMENT ON COLUMN preferencias.presupuesto_max IS 'Presupuesto maximo en COP para la categoria';

CREATE INDEX IF NOT EXISTS idx_preferencias_usuario ON preferencias (id_usuario);

-- Seed: 5 categorias base
INSERT INTO categorias (id_categoria, nombre_categoria) VALUES
    (1, 'Comida'),
    (2, 'Entretenimiento'),
    (3, 'Cultura'),
    (4, 'Aire libre'),
    (5, 'Compras')
ON CONFLICT (id_categoria) DO NOTHING;

SELECT setval(pg_get_serial_sequence('categorias', 'id_categoria'),
              GREATEST(COALESCE((SELECT MAX(id_categoria) FROM categorias), 1), 1));
