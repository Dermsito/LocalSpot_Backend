-- ============================================================
-- verificacion.sql
-- Script de verificacion de la base de datos LocalSpot.
-- Ejecutar contra PostgreSQL 16.
-- ============================================================

-- 1. Verificar que las tablas existen
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY table_name;

-- 2. Verificar usuarios de prueba
SELECT id_usuario, nombre, correo, tipo_usuario
FROM usuarios
ORDER BY id_usuario;

-- 3. Verificar categorias base
SELECT id_categoria, nombre_categoria FROM categorias ORDER BY id_categoria;

-- 4. Verificar lugares
SELECT id_lugar, nombre, estado_verificacion, precio_min, precio_max
FROM lugar
ORDER BY id_lugar;

-- 5. Verificar promedio de calificaciones por lugar
SELECT l.id_lugar,
       l.nombre,
       COUNT(c.id_calificacion) AS total_calificaciones,
       ROUND(AVG(c.puntuacion), 2) AS promedio
FROM lugar l
LEFT JOIN calificacion c ON c.id_lugar = l.id_lugar
GROUP BY l.id_lugar, l.nombre
ORDER BY l.id_lugar;

-- 6. Verificar favoritos por usuario
SELECT u.correo,
       COUNT(f.id_lugar) AS total_favoritos
FROM usuarios u
LEFT JOIN favorito f ON f.id_usuario = u.id_usuario
GROUP BY u.id_usuario, u.correo
ORDER BY u.id_usuario;

-- 7. Verificar preferencias con categoria
SELECT u.correo, c.nombre_categoria, p.presupuesto_max
FROM preferencias p
JOIN usuarios u ON u.id_usuario = p.id_usuario
JOIN categorias c ON c.id_categoria = p.id_categoria
ORDER BY u.correo, c.nombre_categoria;

-- 8. Verificar reportes pendientes
SELECT r.id_reporte, r.motivo, r.estado, r.fecha_reporte
FROM reporte r
WHERE r.estado = 'PENDIENTE'
ORDER BY r.fecha_reporte;

-- 9. Verificar integridad: lugares sin categoria
SELECT id_lugar, nombre FROM lugar WHERE id_categoria IS NULL;

-- 10. Verificar integridad: preferencias duplicadas (no deben existir)
SELECT id_usuario, id_categoria, COUNT(*)
FROM preferencias
GROUP BY id_usuario, id_categoria
HAVING COUNT(*) > 1;

-- 11. Verificar hashes BCrypt validos (deben empezar con $2)
SELECT id_usuario, correo,
       CASE WHEN contrasena_hash LIKE '$2%' THEN 'OK' ELSE 'INVALIDO' END AS estado_hash
FROM usuarios
ORDER BY id_usuario;
