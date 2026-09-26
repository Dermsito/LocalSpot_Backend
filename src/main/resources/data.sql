INSERT INTO categorias (id_categoria, nombre_categoria) VALUES
    (1, 'Comida'),
    (2, 'Entretenimiento'),
    (3, 'Cultura'),
    (4, 'Aire libre'),
    (5, 'Compras')
ON CONFLICT (id_categoria) DO NOTHING;
