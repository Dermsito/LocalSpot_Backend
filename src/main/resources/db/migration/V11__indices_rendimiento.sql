-- ============================================================
-- V11__indices_rendimiento.sql
-- Optimización de consultas frecuentes para despliegue de alto rendimiento.
-- ============================================================

-- Índice para acelerar búsquedas de texto con ILIKE/LOWER en lugares
CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX IF NOT EXISTS idx_lugar_nombre_trgm ON lugar USING gin (nombre gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_lugar_descripcion_trgm ON lugar USING gin (descripcion gin_trgm_ops);

-- Índice compuesto para ordenamiento por fecha y verificación
CREATE INDEX IF NOT EXISTS idx_lugar_estado_fecha ON lugar (estado_verificacion, fecha_registro DESC);

-- Índice para cálculo rápido de promedios de calificación por lugar
CREATE INDEX IF NOT EXISTS idx_calificacion_lugar_puntuacion ON calificacion (id_lugar, puntuacion);
