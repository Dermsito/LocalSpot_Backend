-- ============================================================
-- V2__rol_y_usuario.sql
-- Tabla de roles (ADMINISTRADOR, FINAL, PROPIETARIO) y migracion
-- de usuarios para referenciar a rol mediante id_rol.
-- ============================================================

-- Tabla: rol (catálogo de roles del sistema)
CREATE TABLE IF NOT EXISTS rol (
    id          SMALLINT      NOT NULL,
    nombre      VARCHAR(20)   NOT NULL,
    descripcion VARCHAR(100),
    CONSTRAINT pk_rol PRIMARY KEY (id),
    CONSTRAINT uk_rol_nombre UNIQUE (nombre)
);

COMMENT ON TABLE rol IS 'Catalogo de roles de usuario del sistema';
COMMENT ON COLUMN rol.id IS 'Identificador del rol (1=ADMINISTRADOR, 2=FINAL, 3=PROPIETARIO)';
COMMENT ON COLUMN rol.nombre IS 'Nombre del rol';
COMMENT ON COLUMN rol.descripcion IS 'Descripcion del rol';

-- Seed de roles (idempotente)
INSERT INTO rol (id, nombre, descripcion) VALUES
    (1, 'ADMINISTRADOR', 'Administrador del sistema con acceso total'),
    (2, 'FINAL',         'Usuario final consumidor de recomendaciones'),
    (3, 'PROPIETARIO',   'Propietario de establecimientos')
ON CONFLICT (id) DO NOTHING;

-- Migracion de usuarios: agregar columna id_rol y FK
ALTER TABLE usuarios
    ADD COLUMN IF NOT EXISTS id_rol SMALLINT NOT NULL DEFAULT 2;

COMMENT ON COLUMN usuarios.id_rol IS 'Rol del usuario (FK a rol.id)';

-- Poblar id_rol segun el valor actual de tipo_usuario (para compatibilidad)
UPDATE usuarios
SET id_rol = CASE
    WHEN tipo_usuario = 'ADMINISTRADOR' THEN 1
    WHEN tipo_usuario = 'PROPIETARIO'   THEN 3
    ELSE 2
END
WHERE id_rol = 2;

ALTER TABLE usuarios
    DROP CONSTRAINT IF EXISTS fk_usuarios_rol;

ALTER TABLE usuarios
    ADD CONSTRAINT fk_usuarios_rol
    FOREIGN KEY (id_rol) REFERENCES rol (id);

CREATE INDEX IF NOT EXISTS idx_usuarios_rol ON usuarios (id_rol);
