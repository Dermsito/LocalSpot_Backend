CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(150) NOT NULL,
    contrasena_hash VARCHAR(255) NOT NULL,
    tipo_usuario VARCHAR(20) NOT NULL DEFAULT 'FINAL',
    fecha_registro DATE NOT NULL DEFAULT CURRENT_DATE,
    CONSTRAINT uk_usuarios_correo UNIQUE (correo)
);

CREATE TABLE IF NOT EXISTS categorias (
    id_categoria BIGSERIAL PRIMARY KEY,
    nombre_categoria VARCHAR(50) NOT NULL,
    CONSTRAINT uk_categorias_nombre UNIQUE (nombre_categoria)
);

CREATE TABLE IF NOT EXISTS preferencias (
    id_preferencia BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    id_categoria BIGINT NOT NULL,
    presupuesto_max NUMERIC(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_preferencias_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_preferencias_categoria FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria) ON DELETE RESTRICT,
    CONSTRAINT uk_preferencia_usuario_categoria UNIQUE (id_usuario, id_categoria),
    CONSTRAINT chk_presupuesto_no_negativo CHECK (presupuesto_max >= 0)
);

INSERT INTO categorias (id_categoria, nombre_categoria) VALUES
    (1, 'Comida'),
    (2, 'Entretenimiento'),
    (3, 'Cultura'),
    (4, 'Aire libre'),
    (5, 'Compras')
ON CONFLICT (id_categoria) DO NOTHING;

SELECT setval(pg_get_serial_sequence('categorias', 'id_categoria'), GREATEST(COALESCE((SELECT MAX(id_categoria) FROM categorias), 1), 1));
