-- ============================================================
-- V7__lugar_horario.sql
-- Agrega el campo horario a la tabla lugar (RF-13: horario de atencion)
-- ============================================================

ALTER TABLE lugar
    ADD COLUMN horario VARCHAR(255);

COMMENT ON COLUMN lugar.horario IS 'Horario de atencion del lugar (texto libre, ej: Lun-Vie 8:00-18:00)';
