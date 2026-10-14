-- ============================================================
-- V3__lugar_y_foto_lugar.sql
-- Tablas de lugares y sus fotos. Un lugar pertenece a una categoria
-- y opcionalmente a un propietario (usuario).
-- ============================================================

-- Tabla: lugar
CREATE TABLE IF NOT EXISTS lugar (
    id_lugar            BIGSERIAL       NOT NULL,
    id_categoria        BIGINT          NOT NULL,
    id_propietario      BIGINT,
    nombre              VARCHAR(120)    NOT NULL,
    descripcion         TEXT,
    direccion           VARCHAR(255),
    latitud             NUMERIC(10,7),
    longitud            NUMERIC(10,7),
    precio_min          NUMERIC(12,2)   NOT NULL DEFAULT 0,
    precio_max          NUMERIC(12,2)   NOT NULL DEFAULT 0,
    estado_verificacion VARCHAR(20)     NOT NULL DEFAULT 'PENDIENTE',
    fecha_registro      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP,
    CONSTRAINT pk_lugar PRIMARY KEY (id_lugar),
    CONSTRAINT fk_lugar_categoria FOREIGN KEY (id_categoria)
        REFERENCES categorias (id_categoria) ON DELETE RESTRICT,
    CONSTRAINT fk_lugar_propietario FOREIGN KEY (id_propietario)
        REFERENCES usuarios (id_usuario) ON DELETE SET NULL,
    CONSTRAINT ck_lugar_precio_min       CHECK (precio_min >= 0),
    CONSTRAINT ck_lugar_precio_max       CHECK (precio_max >= precio_min),
    CONSTRAINT ck_lugar_verificacion     CHECK (estado_verificacion IN ('PENDIENTE', 'VERIFICADO', 'RECHAZADO'))
);

COMMENT ON TABLE lugar IS 'Lugares/establecimientos disponibles en LocalSpot';
COMMENT ON COLUMN lugar.id_lugar IS 'Identificador unico del lugar';
COMMENT ON COLUMN lugar.id_categoria IS 'Categoria a la que pertenece el lugar';
COMMENT ON COLUMN lugar.id_propietario IS 'Usuario propietario del establecimiento (NULL si creado por admin)';
COMMENT ON COLUMN lugar.nombre IS 'Nombre del lugar';
COMMENT ON COLUMN lugar.descripcion IS 'Descripcion detallada del lugar';
COMMENT ON COLUMN lugar.direccion IS 'Direccion fisica del lugar';
COMMENT ON COLUMN lugar.latitud IS 'Latitud geografica (WGS84)';
COMMENT ON COLUMN lugar.longitud IS 'Longitud geografica (WGS84)';
COMMENT ON COLUMN lugar.precio_min IS 'Precio minimo estimado en COP';
COMMENT ON COLUMN lugar.precio_max IS 'Precio maximo estimado en COP';
COMMENT ON COLUMN lugar.estado_verificacion IS 'Estado de verificacion: PENDIENTE, VERIFICADO o RECHAZADO';
COMMENT ON COLUMN lugar.fecha_registro IS 'Fecha de creacion del lugar';
COMMENT ON COLUMN lugar.fecha_actualizacion IS 'Fecha de ultima actualizacion del lugar';

CREATE INDEX IF NOT EXISTS idx_lugares_verificacion_categoria ON lugar (estado_verificacion, id_categoria);
CREATE INDEX IF NOT EXISTS idx_lugares_precios ON lugar (precio_min, precio_max);
CREATE INDEX IF NOT EXISTS idx_lugares_propietario ON lugar (id_propietario);

-- Tabla: foto_lugar
CREATE TABLE IF NOT EXISTS foto_lugar (
    id_foto_lugar BIGSERIAL     NOT NULL,
    id_lugar      BIGINT        NOT NULL,
    url_foto      VARCHAR(500)  NOT NULL,
    fecha_subida  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_foto_lugar PRIMARY KEY (id_foto_lugar),
    CONSTRAINT fk_foto_lugar_lugar FOREIGN KEY (id_lugar)
        REFERENCES lugar (id_lugar) ON DELETE CASCADE
);

COMMENT ON TABLE foto_lugar IS 'Fotos asociadas a un lugar';
COMMENT ON COLUMN foto_lugar.id_foto_lugar IS 'Identificador unico de la foto';
COMMENT ON COLUMN foto_lugar.id_lugar IS 'Lugar al que pertenece la foto';
COMMENT ON COLUMN foto_lugar.url_foto IS 'URL de acceso a la foto';
COMMENT ON COLUMN foto_lugar.fecha_subida IS 'Fecha de subida de la foto';

CREATE INDEX IF NOT EXISTS idx_foto_lugar_lugar ON foto_lugar (id_lugar);
