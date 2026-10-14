-- ============================================================
-- V6__seed_pruebas.sql
-- Datos de prueba para desarrollo:
--   - 15 lugares VERIFICADOS + 2 PENDIENTES
--   - 30 fotos (2 por lugar verificado)
--   - 2 usuarios de prueba (FINAL y PROPIETARIO) - password: 'password'
--   - 15 calificaciones de prueba
-- ADMIN se crea via ApplicationRunner (ADMIN_EMAIL / ADMIN_PASSWORD).
-- ============================================================

-- Usuarios de prueba (password BCrypt de 'password')
INSERT INTO usuarios (id_usuario, nombre, correo, contrasena_hash, tipo_usuario, id_rol, fecha_registro) VALUES
    (100, 'Usuario Final Prueba',     'final@localspot.test',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'FINAL',       2, CURRENT_TIMESTAMP),
    (101, 'Usuario Propietario Test', 'propietario@localspot.test','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'PROPIETARIO', 3, CURRENT_TIMESTAMP)
ON CONFLICT (id_usuario) DO NOTHING;

-- 15 lugares VERIFICADOS (coordenadas aproximadas de Bogota)
INSERT INTO lugar (id_lugar, id_categoria, id_propietario, nombre, descripcion, direccion, latitud, longitud, precio_min, precio_max, estado_verificacion, fecha_registro) VALUES
    (1,  1, 101, 'Pizzeria El Rincon Secreto',    'Pizzeria artesanal con ingredientes locales y masa madre fermentada 48h.', 'Cra 7 #45-12',      4.7110, -74.0721, 15000, 45000, 'VERIFICADO', CURRENT_TIMESTAMP),
    (2,  1, 101, 'Sabor Escondido',               'Comida casera colombiana con recetas de la abuela, ambiente familiar.',    'Calle 80 #10-20',    4.7120, -74.0731, 8000,  25000, 'VERIFICADO', CURRENT_TIMESTAMP),
    (3,  1, 101, 'Antojitos del Centro',          'Antojitos tipicos: empanadas, patacones y jugos naturales.',                'Cra 13 #30-45',      4.7100, -74.0711, 5000,  18000, 'VERIFICADO', CURRENT_TIMESTAMP),
    (4,  2, 101, 'Microcine La Guarida',          'Cine independiente con peliculas de autor y cine foro los jueves.',         'Calle 22 #5-30',     4.7090, -74.0701, 10000, 20000, 'VERIFICADO', CURRENT_TIMESTAMP),
    (5,  2, 101, 'Sala de Escape Bajo Tierra',    'Escape room con tematicas colombianas y enigmas originales.',               'Cra 10 #20-15',      4.7130, -74.0741, 30000, 60000, 'VERIFICADO', CURRENT_TIMESTAMP),
    (6,  2, 101, 'Karaoke Retro',                 'Karaoke con musica de los 80s y 90s, ambiente retro y cocktails.',         'Calle 45 #12-33',    4.7140, -74.0751, 15000, 40000, 'VERIFICADO', CURRENT_TIMESTAMP),
    (7,  3, 101, 'Galeria Arte Urbano',           'Galeria de arte urbano con exposiciones rotativas de artistas locales.',    'Cra 5 #60-10',       4.7150, -74.0761, 0,     15000, 'VERIFICADO', CURRENT_TIMESTAMP),
    (8,  3, 101, 'Museo Casa de la Memoria',      'Museo comunitario sobre la historia y memoria del barrio.',                 'Calle 18 #8-22',     4.7080, -74.0691, 5000,  10000, 'VERIFICADO', CURRENT_TIMESTAMP),
    (9,  3, 101, 'Centro Cultural La Esquina',    'Centro cultural con talleres, exposiciones y eventos comunitarios.',        'Cra 15 #25-08',      4.7070, -74.0681, 0,     20000, 'VERIFICADO', CURRENT_TIMESTAMP),
    (10, 4, 101, 'Mirador del Occidente',         'Mirador natural con vista panoramica de la ciudad desde la falda.',         'Vereda El Rosal',     4.7160, -74.0771, 0,     8000,  'VERIFICADO', CURRENT_TIMESTAMP),
    (11, 4, 101, 'Parque Natural La Esperanza',   'Parque natural con senderos ecologicos y avistamiento de aves.',            'Cra 90 #50-00',      4.7170, -74.0781, 5000,  12000, 'VERIFICADO', CURRENT_TIMESTAMP),
    (12, 4, 101, 'Glamping El Bosque',            'Glamping en medio del bosque con tiendas equipadas y fogata.',              'Vereda La Palma',     4.7180, -74.0791, 80000, 150000,'VERIFICADO', CURRENT_TIMESTAMP),
    (13, 5, 101, 'Mercado de Las Pulgas',         'Mercado de pulgas con antiguedades, artesanías y comida callejera.',        'Calle 25 #3-10',     4.7060, -74.0671, 5000,  50000, 'VERIFICADO', CURRENT_TIMESTAMP),
    (14, 5, 101, 'Bazar Artesanal Local',         'Bazar de artesanias hechas a mano por artesanos de la region.',             'Cra 20 #40-15',      4.7050, -74.0661, 10000, 60000, 'VERIFICADO', CURRENT_TIMESTAMP),
    (15, 5, 101, 'Tienda de Diseno Independiente','Tienda de diseno independiente con prendas y accesorios unicos.',           'Calle 70 #5-44',     4.7040, -74.0651, 30000, 120000,'VERIFICADO', CURRENT_TIMESTAMP),
    -- 2 lugares PENDIENTES (para probar filtros)
    (16, 1, NULL, 'Nuevo Restaurante Prueba',     'Restaurante en proceso de verificacion por el equipo admin.',               'Calle 100 #20-30',   4.7200, -74.0801, 20000, 70000, 'PENDIENTE',  CURRENT_TIMESTAMP),
    (17, 3, NULL, 'Espacio Cultural Nuevo',       'Espacio cultural pendiente de aprobacion.',                                 'Cra 30 #15-40',      4.7210, -74.0811, 0,     25000, 'PENDIENTE',  CURRENT_TIMESTAMP)
ON CONFLICT (id_lugar) DO NOTHING;

-- 30 fotos (2 por cada lugar verificado del 1 al 15)
INSERT INTO foto_lugar (id_foto_lugar, id_lugar, url_foto, fecha_subida) VALUES
    (1,  1,  'https://picsum.photos/seed/lugar1a/800/600', CURRENT_TIMESTAMP),
    (2,  1,  'https://picsum.photos/seed/lugar1b/800/600', CURRENT_TIMESTAMP),
    (3,  2,  'https://picsum.photos/seed/lugar2a/800/600', CURRENT_TIMESTAMP),
    (4,  2,  'https://picsum.photos/seed/lugar2b/800/600', CURRENT_TIMESTAMP),
    (5,  3,  'https://picsum.photos/seed/lugar3a/800/600', CURRENT_TIMESTAMP),
    (6,  3,  'https://picsum.photos/seed/lugar3b/800/600', CURRENT_TIMESTAMP),
    (7,  4,  'https://picsum.photos/seed/lugar4a/800/600', CURRENT_TIMESTAMP),
    (8,  4,  'https://picsum.photos/seed/lugar4b/800/600', CURRENT_TIMESTAMP),
    (9,  5,  'https://picsum.photos/seed/lugar5a/800/600', CURRENT_TIMESTAMP),
    (10, 5,  'https://picsum.photos/seed/lugar5b/800/600', CURRENT_TIMESTAMP),
    (11, 6,  'https://picsum.photos/seed/lugar6a/800/600', CURRENT_TIMESTAMP),
    (12, 6,  'https://picsum.photos/seed/lugar6b/800/600', CURRENT_TIMESTAMP),
    (13, 7,  'https://picsum.photos/seed/lugar7a/800/600', CURRENT_TIMESTAMP),
    (14, 7,  'https://picsum.photos/seed/lugar7b/800/600', CURRENT_TIMESTAMP),
    (15, 8,  'https://picsum.photos/seed/lugar8a/800/600', CURRENT_TIMESTAMP),
    (16, 8,  'https://picsum.photos/seed/lugar8b/800/600', CURRENT_TIMESTAMP),
    (17, 9,  'https://picsum.photos/seed/lugar9a/800/600', CURRENT_TIMESTAMP),
    (18, 9,  'https://picsum.photos/seed/lugar9b/800/600', CURRENT_TIMESTAMP),
    (19, 10, 'https://picsum.photos/seed/lugar10a/800/600',CURRENT_TIMESTAMP),
    (20, 10, 'https://picsum.photos/seed/lugar10b/800/600',CURRENT_TIMESTAMP),
    (21, 11, 'https://picsum.photos/seed/lugar11a/800/600',CURRENT_TIMESTAMP),
    (22, 11, 'https://picsum.photos/seed/lugar11b/800/600',CURRENT_TIMESTAMP),
    (23, 12, 'https://picsum.photos/seed/lugar12a/800/600',CURRENT_TIMESTAMP),
    (24, 12, 'https://picsum.photos/seed/lugar12b/800/600',CURRENT_TIMESTAMP),
    (25, 13, 'https://picsum.photos/seed/lugar13a/800/600',CURRENT_TIMESTAMP),
    (26, 13, 'https://picsum.photos/seed/lugar13b/800/600',CURRENT_TIMESTAMP),
    (27, 14, 'https://picsum.photos/seed/lugar14a/800/600',CURRENT_TIMESTAMP),
    (28, 14, 'https://picsum.photos/seed/lugar14b/800/600',CURRENT_TIMESTAMP),
    (29, 15, 'https://picsum.photos/seed/lugar15a/800/600',CURRENT_TIMESTAMP),
    (30, 15, 'https://picsum.photos/seed/lugar15b/800/600',CURRENT_TIMESTAMP)
ON CONFLICT (id_foto_lugar) DO NOTHING;

-- 15 calificaciones de prueba (usuario final 100 califica 15 lugares)
INSERT INTO calificacion (id_calificacion, id_usuario, id_lugar, puntuacion, comentario, visible, fecha_registro) VALUES
    (1,  100, 1,  5, 'Excelente pizza artesanal, la mejor de la zona.',   TRUE, CURRENT_TIMESTAMP),
    (2,  100, 2,  4, 'Comida casera muy rica y precios accesibles.',      TRUE, CURRENT_TIMESTAMP),
    (3,  100, 3,  4, 'Antojitos tipicos deliciosos, muy recomendado.',    TRUE, CURRENT_TIMESTAMP),
    (4,  100, 4,  5, 'Cine independiente de calidad, gran experiencia.', TRUE, CURRENT_TIMESTAMP),
    (5,  100, 5,  3, 'Escape room divertido aunque corto.',               TRUE, CURRENT_TIMESTAMP),
    (6,  100, 6,  4, 'Karaoke retro genial, cocktails ricos.',            TRUE, CURRENT_TIMESTAMP),
    (7,  100, 7,  5, 'Galeria de arte urbano impresionante.',             TRUE, CURRENT_TIMESTAMP),
    (8,  100, 8,  4, 'Museo comunitario muy interesante.',                TRUE, CURRENT_TIMESTAMP),
    (9,  100, 9,  5, 'Centro cultural con talleres excelentes.',          TRUE, CURRENT_TIMESTAMP),
    (10, 100, 10, 5, 'Vista panoramica increible desde el mirador.',      TRUE, CURRENT_TIMESTAMP),
    (11, 100, 11, 4, 'Senderos ecologicos bien mantenidos.',              TRUE, CURRENT_TIMESTAMP),
    (12, 100, 12, 5, 'Glamping en el bosque, experiencia unica.',         TRUE, CURRENT_TIMESTAMP),
    (13, 100, 13, 3, 'Mercado de pulgas con variedad interesante.',       TRUE, CURRENT_TIMESTAMP),
    (14, 100, 14, 4, 'Artesanias hechas a mano de alta calidad.',         TRUE, CURRENT_TIMESTAMP),
    (15, 100, 15, 4, 'Diseno independiente unico y original.',            TRUE, CURRENT_TIMESTAMP)
ON CONFLICT (id_calificacion) DO NOTHING;

-- Ajustar secuencias de serials
SELECT setval(pg_get_serial_sequence('usuarios',     'id_usuario'),      GREATEST(COALESCE((SELECT MAX(id_usuario)      FROM usuarios),     1), 1));
SELECT setval(pg_get_serial_sequence('lugar',        'id_lugar'),        GREATEST(COALESCE((SELECT MAX(id_lugar)        FROM lugar),        1), 1));
SELECT setval(pg_get_serial_sequence('foto_lugar',   'id_foto_lugar'),   GREATEST(COALESCE((SELECT MAX(id_foto_lugar)   FROM foto_lugar),   1), 1));
SELECT setval(pg_get_serial_sequence('calificacion', 'id_calificacion'), GREATEST(COALESCE((SELECT MAX(id_calificacion) FROM calificacion), 1), 1));
