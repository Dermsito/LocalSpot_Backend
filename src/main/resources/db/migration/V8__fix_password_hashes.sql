-- ============================================================
-- V8__fix_password_hashes.sql
-- Corrige los hashes BCrypt de los usuarios de prueba FINAL y PROPIETARIO.
-- El hash original en V6 no coincidia con la contrasena 'password'.
-- Password: 'password' (BCrypt cost 10)
-- ============================================================

UPDATE usuarios
SET contrasena_hash = '$2a$10$bKu36zZxK623it5egyzKMOn8KKuFcCeXubQPa7UI6PQsbmK0gl8oO'
WHERE id_usuario IN (100, 101)
  AND tipo_usuario IN ('FINAL', 'PROPIETARIO');
