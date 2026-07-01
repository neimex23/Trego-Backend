-- ============================================================================
-- Trego - Datos de prueba (seed) completo y autocontenido
-- ----------------------------------------------------------------------------
-- Genera un dataset realista para demo: administrador, restaurantes, clientes,
-- subcategorias, ingredientes, productos (platos / articulos / combos),
-- ofertas, comentarios (resenias), pedidos con lineas, pagos y reclamos.
--
-- Disenio:
--   * Idempotente: cada insert esta guardado con NOT EXISTS sobre una clave
--     natural, por lo que el script se puede correr varias veces sin duplicar.
--   * Herencia JOINED: Usuario -> {Administrador, Cliente, Restaurante} y
--     Producto -> {Plato, Articulo, Combo}. Se inserta primero la tabla padre
--     y luego la hija reutilizando el mismo id.
--   * Marcadores: los usuarios de prueba usan emails '@trego.seed' para poder
--     identificarlos y limpiarlos facilmente.
--
-- Ejecutar (Windows / PowerShell):
--   Get-Content scripts/seed-demo-completo.sql | mysql -u root -p1234
-- Ejecutar (bash):
--   mysql -u root -p1234 < scripts/seed-demo-completo.sql
-- ============================================================================

USE tregodb;

SET NAMES utf8mb4;

-- ============================================================================
-- 1. ADMINISTRADOR
-- ============================================================================
INSERT INTO usuario (nombre, email, foto_perfil, rol)
SELECT 'Admin Trego', 'admin@trego.seed', NULL, 'Administrador'
FROM (SELECT 1) AS d
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'admin@trego.seed');

INSERT INTO administrador (id_usuario, password)
SELECT u.id_usuario, '$2a$10$demoDemoDemoDemoDemoDe.HashFicticioParaPruebas123456'
FROM usuario u
WHERE u.email = 'admin@trego.seed'
  AND NOT EXISTS (SELECT 1 FROM administrador a WHERE a.id_usuario = u.id_usuario);

-- ============================================================================
-- 2. SUBCATEGORIAS (catalogo global, una por categoria del menu)
-- ============================================================================
INSERT INTO sub_categoria (nombre, categoria, url_imagen)
SELECT 'Platos principales', 'Principal', NULL
FROM (SELECT 1) AS d
WHERE NOT EXISTS (SELECT 1 FROM sub_categoria WHERE nombre = 'Platos principales');
INSERT INTO sub_categoria (nombre, categoria, url_imagen)
SELECT 'Entradas', 'Entrada', NULL
FROM (SELECT 1) AS d
WHERE NOT EXISTS (SELECT 1 FROM sub_categoria WHERE nombre = 'Entradas');
INSERT INTO sub_categoria (nombre, categoria, url_imagen)
SELECT 'Guarniciones', 'Guarnicion', NULL
FROM (SELECT 1) AS d
WHERE NOT EXISTS (SELECT 1 FROM sub_categoria WHERE nombre = 'Guarniciones');
INSERT INTO sub_categoria (nombre, categoria, url_imagen)
SELECT 'Ensaladas', 'Ensalada', NULL
FROM (SELECT 1) AS d
WHERE NOT EXISTS (SELECT 1 FROM sub_categoria WHERE nombre = 'Ensaladas');
INSERT INTO sub_categoria (nombre, categoria, url_imagen)
SELECT 'Bebidas', 'Bebida', NULL
FROM (SELECT 1) AS d
WHERE NOT EXISTS (SELECT 1 FROM sub_categoria WHERE nombre = 'Bebidas');
INSERT INTO sub_categoria (nombre, categoria, url_imagen)
SELECT 'Postres', 'Postre', NULL
FROM (SELECT 1) AS d
WHERE NOT EXISTS (SELECT 1 FROM sub_categoria WHERE nombre = 'Postres');

SET @sub_principal  = (SELECT id_sub_categoria FROM sub_categoria WHERE nombre = 'Platos principales' LIMIT 1);
SET @sub_entrada    = (SELECT id_sub_categoria FROM sub_categoria WHERE nombre = 'Entradas' LIMIT 1);
SET @sub_guarnicion = (SELECT id_sub_categoria FROM sub_categoria WHERE nombre = 'Guarniciones' LIMIT 1);
SET @sub_ensalada   = (SELECT id_sub_categoria FROM sub_categoria WHERE nombre = 'Ensaladas' LIMIT 1);
SET @sub_bebida     = (SELECT id_sub_categoria FROM sub_categoria WHERE nombre = 'Bebidas' LIMIT 1);
SET @sub_postre     = (SELECT id_sub_categoria FROM sub_categoria WHERE nombre = 'Postres' LIMIT 1);

-- ============================================================================
-- 3. OFERTAS (vigentes: desde ayer hasta dentro de 30 dias)
-- ============================================================================
INSERT INTO oferta (descripcion, descuento, url_imagen, fecha_inicio, fecha_fin)
SELECT '2x1 en hamburguesas - solo esta semana', 25.0, NULL,
       DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY)
FROM (SELECT 1) AS d
WHERE NOT EXISTS (SELECT 1 FROM oferta WHERE descripcion = '2x1 en hamburguesas - solo esta semana');
INSERT INTO oferta (descripcion, descuento, url_imagen, fecha_inicio, fecha_fin)
SELECT 'Pizza grande 20% off', 20.0, NULL,
       DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY)
FROM (SELECT 1) AS d
WHERE NOT EXISTS (SELECT 1 FROM oferta WHERE descripcion = 'Pizza grande 20% off');
INSERT INTO oferta (descripcion, descuento, url_imagen, fecha_inicio, fecha_fin)
SELECT 'Postres a mitad de precio', 50.0, NULL,
       DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY)
FROM (SELECT 1) AS d
WHERE NOT EXISTS (SELECT 1 FROM oferta WHERE descripcion = 'Postres a mitad de precio');

SET @of_burger = (SELECT id_oferta FROM oferta WHERE descripcion = '2x1 en hamburguesas - solo esta semana' LIMIT 1);
SET @of_pizza  = (SELECT id_oferta FROM oferta WHERE descripcion = 'Pizza grande 20% off' LIMIT 1);
SET @of_postre = (SELECT id_oferta FROM oferta WHERE descripcion = 'Postres a mitad de precio' LIMIT 1);

-- ============================================================================
-- 4. RESTAURANTES  (usuario + restaurante)
--    habilitado=1 (aprobado por admin), abierto=1, en Montevideo.
-- ============================================================================

-- --- 4.1 La Parrilla del Centro (Parrillada) ---
INSERT INTO usuario (nombre, email, foto_perfil, rol)
SELECT 'La Parrilla del Centro', 'parrilla@trego.seed', NULL, 'Restaurante'
FROM (SELECT 1) AS d
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'parrilla@trego.seed');
SET @rest_parrilla = (SELECT id_usuario FROM usuario WHERE email = 'parrilla@trego.seed' LIMIT 1);
INSERT INTO restaurante (id_usuario, password, rut, telefono, descripcion, calificacion_prom,
       habilitado, cuentahabilitada, abierto, foto_portada, categoria, hora_apertura, hora_cierre,
       cierre_programado, radio_entrega, tag, calle, numero, apartamento, esquina, latitud, longitud)
SELECT @rest_parrilla, '$2a$10$demoHashParrillaFicticioParaPruebas1234567890abc', '210000010011',
       '099111222', 'Asado uruguayo a la lenia, achuras y pasta casera.', 4.6,
       1, 1, 1, NULL, 'Parrillada', '11:30:00', '23:30:00', NULL, 12,
       'Local', 'Av. 18 de Julio', '1450', NULL, 'Yi', -34.90642, -56.18915
FROM (SELECT 1) AS d
WHERE @rest_parrilla IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM restaurante WHERE id_usuario = @rest_parrilla);

-- --- 4.2 Pizzeria Napoli (Pizza) ---
INSERT INTO usuario (nombre, email, foto_perfil, rol)
SELECT 'Pizzeria Napoli', 'napoli@trego.seed', NULL, 'Restaurante'
FROM (SELECT 1) AS d
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'napoli@trego.seed');
SET @rest_napoli = (SELECT id_usuario FROM usuario WHERE email = 'napoli@trego.seed' LIMIT 1);
INSERT INTO restaurante (id_usuario, password, rut, telefono, descripcion, calificacion_prom,
       habilitado, cuentahabilitada, abierto, foto_portada, categoria, hora_apertura, hora_cierre,
       cierre_programado, radio_entrega, tag, calle, numero, apartamento, esquina, latitud, longitud)
SELECT @rest_napoli, '$2a$10$demoHashNapoliFicticioParaPruebas1234567890abcde', '210000020022',
       '099333444', 'Pizza a la piedra y fainia. Receta napolitana tradicional.', 4.8,
       1, 1, 1, NULL, 'Pizza', '18:00:00', '00:30:00', NULL, 10,
       'Local', 'Ejido', '1234', NULL, 'San Jose', -34.90495, -56.19102
FROM (SELECT 1) AS d
WHERE @rest_napoli IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM restaurante WHERE id_usuario = @rest_napoli);

-- --- 4.3 Burger House (ComidaRapida) ---
INSERT INTO usuario (nombre, email, foto_perfil, rol)
SELECT 'Burger House', 'burger@trego.seed', NULL, 'Restaurante'
FROM (SELECT 1) AS d
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'burger@trego.seed');
SET @rest_burger = (SELECT id_usuario FROM usuario WHERE email = 'burger@trego.seed' LIMIT 1);
INSERT INTO restaurante (id_usuario, password, rut, telefono, descripcion, calificacion_prom,
       habilitado, cuentahabilitada, abierto, foto_portada, categoria, hora_apertura, hora_cierre,
       cierre_programado, radio_entrega, tag, calle, numero, apartamento, esquina, latitud, longitud)
SELECT @rest_burger, '$2a$10$demoHashBurgerFicticioParaPruebas1234567890abcde', '210000030033',
       '099555666', 'Hamburguesas smash, papas rusticas y milkshakes.', 4.3,
       1, 1, 1, NULL, 'ComidaRapida', '12:00:00', '23:59:00', NULL, 15,
       'Local', 'Av. Brasil', '2710', 'PB', 'Cavia', -34.91357, -56.15498
FROM (SELECT 1) AS d
WHERE @rest_burger IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM restaurante WHERE id_usuario = @rest_burger);

-- --- 4.4 Verde Vegano (Vegano) ---
INSERT INTO usuario (nombre, email, foto_perfil, rol)
SELECT 'Verde Vegano', 'vegano@trego.seed', NULL, 'Restaurante'
FROM (SELECT 1) AS d
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'vegano@trego.seed');
SET @rest_vegano = (SELECT id_usuario FROM usuario WHERE email = 'vegano@trego.seed' LIMIT 1);
INSERT INTO restaurante (id_usuario, password, rut, telefono, descripcion, calificacion_prom,
       habilitado, cuentahabilitada, abierto, foto_portada, categoria, hora_apertura, hora_cierre,
       cierre_programado, radio_entrega, tag, calle, numero, apartamento, esquina, latitud, longitud)
SELECT @rest_vegano, '$2a$10$demoHashVeganoFicticioParaPruebas1234567890abcde', '210000040044',
       '099777888', 'Cocina plant-based, bowls y jugos naturales.', 4.5,
       1, 1, 1, NULL, 'Vegano', '10:00:00', '22:00:00', NULL, 8,
       'Local', 'Bvar. Espania', '2300', NULL, 'Br. Artigas', -34.91011, -56.15721
FROM (SELECT 1) AS d
WHERE @rest_vegano IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM restaurante WHERE id_usuario = @rest_vegano);

-- --- 4.5 Heladeria Crema (Heladeria) ---
INSERT INTO usuario (nombre, email, foto_perfil, rol)
SELECT 'Heladeria Crema', 'helados@trego.seed', NULL, 'Restaurante'
FROM (SELECT 1) AS d
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'helados@trego.seed');
SET @rest_helados = (SELECT id_usuario FROM usuario WHERE email = 'helados@trego.seed' LIMIT 1);
INSERT INTO restaurante (id_usuario, password, rut, telefono, descripcion, calificacion_prom,
       habilitado, cuentahabilitada, abierto, foto_portada, categoria, hora_apertura, hora_cierre,
       cierre_programado, radio_entrega, tag, calle, numero, apartamento, esquina, latitud, longitud)
SELECT @rest_helados, '$2a$10$demoHashHeladosFicticioParaPruebas1234567890abc', '210000050055',
       '099999000', 'Helados artesanales y postres helados.', 4.7,
       1, 1, 1, NULL, 'Heladeria', '13:00:00', '23:00:00', NULL, 10,
       'Local', '26 de Marzo', '1180', NULL, 'Coronel Mora', -34.91548, -56.15203
FROM (SELECT 1) AS d
WHERE @rest_helados IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM restaurante WHERE id_usuario = @rest_helados);

-- ============================================================================
-- 5. INGREDIENTES por restaurante
-- ============================================================================
-- Cada INSERT de ingrediente esta guardado por nombre + restaurante_id.

-- Parrilla
INSERT INTO ingrediente (nombre, restaurante_id) SELECT 'Asado de tira', @rest_parrilla FROM (SELECT 1) AS d
WHERE @rest_parrilla IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre='Asado de tira' AND restaurante_id=@rest_parrilla);
INSERT INTO ingrediente (nombre, restaurante_id) SELECT 'Chorizo', @rest_parrilla FROM (SELECT 1) AS d
WHERE @rest_parrilla IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre='Chorizo' AND restaurante_id=@rest_parrilla);
INSERT INTO ingrediente (nombre, restaurante_id) SELECT 'Morcilla', @rest_parrilla FROM (SELECT 1) AS d
WHERE @rest_parrilla IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre='Morcilla' AND restaurante_id=@rest_parrilla);
INSERT INTO ingrediente (nombre, restaurante_id) SELECT 'Provolone', @rest_parrilla FROM (SELECT 1) AS d
WHERE @rest_parrilla IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre='Provolone' AND restaurante_id=@rest_parrilla);
INSERT INTO ingrediente (nombre, restaurante_id) SELECT 'Ensalada criolla', @rest_parrilla FROM (SELECT 1) AS d
WHERE @rest_parrilla IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre='Ensalada criolla' AND restaurante_id=@rest_parrilla);

-- Napoli
INSERT INTO ingrediente (nombre, restaurante_id) SELECT 'Muzzarella', @rest_napoli FROM (SELECT 1) AS d
WHERE @rest_napoli IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre='Muzzarella' AND restaurante_id=@rest_napoli);
INSERT INTO ingrediente (nombre, restaurante_id) SELECT 'Salsa de tomate', @rest_napoli FROM (SELECT 1) AS d
WHERE @rest_napoli IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre='Salsa de tomate' AND restaurante_id=@rest_napoli);
INSERT INTO ingrediente (nombre, restaurante_id) SELECT 'Jamon', @rest_napoli FROM (SELECT 1) AS d
WHERE @rest_napoli IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre='Jamon' AND restaurante_id=@rest_napoli);
INSERT INTO ingrediente (nombre, restaurante_id) SELECT 'Aceitunas', @rest_napoli FROM (SELECT 1) AS d
WHERE @rest_napoli IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre='Aceitunas' AND restaurante_id=@rest_napoli);
INSERT INTO ingrediente (nombre, restaurante_id) SELECT 'Albahaca', @rest_napoli FROM (SELECT 1) AS d
WHERE @rest_napoli IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre='Albahaca' AND restaurante_id=@rest_napoli);

-- Burger
INSERT INTO ingrediente (nombre, restaurante_id) SELECT 'Medallon de carne', @rest_burger FROM (SELECT 1) AS d
WHERE @rest_burger IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre='Medallon de carne' AND restaurante_id=@rest_burger);
INSERT INTO ingrediente (nombre, restaurante_id) SELECT 'Queso cheddar', @rest_burger FROM (SELECT 1) AS d
WHERE @rest_burger IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre='Queso cheddar' AND restaurante_id=@rest_burger);
INSERT INTO ingrediente (nombre, restaurante_id) SELECT 'Bacon', @rest_burger FROM (SELECT 1) AS d
WHERE @rest_burger IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre='Bacon' AND restaurante_id=@rest_burger);
INSERT INTO ingrediente (nombre, restaurante_id) SELECT 'Lechuga', @rest_burger FROM (SELECT 1) AS d
WHERE @rest_burger IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre='Lechuga' AND restaurante_id=@rest_burger);
INSERT INTO ingrediente (nombre, restaurante_id) SELECT 'Tomate', @rest_burger FROM (SELECT 1) AS d
WHERE @rest_burger IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre='Tomate' AND restaurante_id=@rest_burger);
INSERT INTO ingrediente (nombre, restaurante_id) SELECT 'Cebolla caramelizada', @rest_burger FROM (SELECT 1) AS d
WHERE @rest_burger IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre='Cebolla caramelizada' AND restaurante_id=@rest_burger);

-- Vegano
INSERT INTO ingrediente (nombre, restaurante_id) SELECT 'Garbanzos', @rest_vegano FROM (SELECT 1) AS d
WHERE @rest_vegano IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre='Garbanzos' AND restaurante_id=@rest_vegano);
INSERT INTO ingrediente (nombre, restaurante_id) SELECT 'Palta', @rest_vegano FROM (SELECT 1) AS d
WHERE @rest_vegano IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre='Palta' AND restaurante_id=@rest_vegano);
INSERT INTO ingrediente (nombre, restaurante_id) SELECT 'Quinoa', @rest_vegano FROM (SELECT 1) AS d
WHERE @rest_vegano IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre='Quinoa' AND restaurante_id=@rest_vegano);
INSERT INTO ingrediente (nombre, restaurante_id) SELECT 'Tofu', @rest_vegano FROM (SELECT 1) AS d
WHERE @rest_vegano IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre='Tofu' AND restaurante_id=@rest_vegano);

-- Helados
INSERT INTO ingrediente (nombre, restaurante_id) SELECT 'Dulce de leche', @rest_helados FROM (SELECT 1) AS d
WHERE @rest_helados IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre='Dulce de leche' AND restaurante_id=@rest_helados);
INSERT INTO ingrediente (nombre, restaurante_id) SELECT 'Chocolate', @rest_helados FROM (SELECT 1) AS d
WHERE @rest_helados IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre='Chocolate' AND restaurante_id=@rest_helados);
INSERT INTO ingrediente (nombre, restaurante_id) SELECT 'Frutilla', @rest_helados FROM (SELECT 1) AS d
WHERE @rest_helados IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre='Frutilla' AND restaurante_id=@rest_helados);

-- ============================================================================
-- 6. PRODUCTOS
--    Patron por producto:
--      a) INSERT en producto (guardado por restaurante_id + nombre)
--      b) SET @p = id resuelto por (restaurante_id, nombre)
--      c) INSERT en la tabla hija (plato/articulo/combo) guardado por NOT EXISTS
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 6.1 La Parrilla del Centro
-- ----------------------------------------------------------------------------
INSERT INTO producto (nombre, precio, descripcion, url_imagen, oferta_activa, disponible, oferta_id, subcategoria_id, restaurante_id)
SELECT 'Parrillada para dos', 1290, 'Asado, chorizo, morcilla y achuras. Incluye guarnicion.', NULL, 0, 1, NULL, @sub_principal, @rest_parrilla
FROM (SELECT 1) AS d
WHERE @rest_parrilla IS NOT NULL AND NOT EXISTS (SELECT 1 FROM producto WHERE restaurante_id=@rest_parrilla AND nombre='Parrillada para dos');
SET @p = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_parrilla AND nombre='Parrillada para dos' LIMIT 1);
INSERT INTO plato (id_producto, tiempo_preparacion_minutos) SELECT @p, 35 FROM (SELECT 1) AS d
WHERE @p IS NOT NULL AND NOT EXISTS (SELECT 1 FROM plato WHERE id_producto=@p);

INSERT INTO producto (nombre, precio, descripcion, url_imagen, oferta_activa, disponible, oferta_id, subcategoria_id, restaurante_id)
SELECT 'Provolone a la parrilla', 380, 'Queso provolone fundido con oregano.', NULL, 0, 1, NULL, @sub_entrada, @rest_parrilla
FROM (SELECT 1) AS d
WHERE @rest_parrilla IS NOT NULL AND NOT EXISTS (SELECT 1 FROM producto WHERE restaurante_id=@rest_parrilla AND nombre='Provolone a la parrilla');
SET @p = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_parrilla AND nombre='Provolone a la parrilla' LIMIT 1);
INSERT INTO plato (id_producto, tiempo_preparacion_minutos) SELECT @p, 12 FROM (SELECT 1) AS d
WHERE @p IS NOT NULL AND NOT EXISTS (SELECT 1 FROM plato WHERE id_producto=@p);

INSERT INTO producto (nombre, precio, descripcion, url_imagen, oferta_activa, disponible, oferta_id, subcategoria_id, restaurante_id)
SELECT 'Agua mineral 600ml', 90, 'Agua sin gas.', NULL, 0, 1, NULL, @sub_bebida, @rest_parrilla
FROM (SELECT 1) AS d
WHERE @rest_parrilla IS NOT NULL AND NOT EXISTS (SELECT 1 FROM producto WHERE restaurante_id=@rest_parrilla AND nombre='Agua mineral 600ml');
SET @p = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_parrilla AND nombre='Agua mineral 600ml' LIMIT 1);
INSERT INTO articulo (id_producto) SELECT @p FROM (SELECT 1) AS d
WHERE @p IS NOT NULL AND NOT EXISTS (SELECT 1 FROM articulo WHERE id_producto=@p);

-- ----------------------------------------------------------------------------
-- 6.2 Pizzeria Napoli
-- ----------------------------------------------------------------------------
INSERT INTO producto (nombre, precio, descripcion, url_imagen, oferta_activa, disponible, oferta_id, subcategoria_id, restaurante_id)
SELECT 'Pizza muzzarella', 540, 'Masa a la piedra, salsa y muzzarella.', NULL, 1, 1, @of_pizza, @sub_principal, @rest_napoli
FROM (SELECT 1) AS d
WHERE @rest_napoli IS NOT NULL AND NOT EXISTS (SELECT 1 FROM producto WHERE restaurante_id=@rest_napoli AND nombre='Pizza muzzarella');
SET @p = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_napoli AND nombre='Pizza muzzarella' LIMIT 1);
INSERT INTO plato (id_producto, tiempo_preparacion_minutos) SELECT @p, 22 FROM (SELECT 1) AS d
WHERE @p IS NOT NULL AND NOT EXISTS (SELECT 1 FROM plato WHERE id_producto=@p);

INSERT INTO producto (nombre, precio, descripcion, url_imagen, oferta_activa, disponible, oferta_id, subcategoria_id, restaurante_id)
SELECT 'Pizza napolitana', 620, 'Tomate, ajo, albahaca y muzzarella.', NULL, 1, 1, @of_pizza, @sub_principal, @rest_napoli
FROM (SELECT 1) AS d
WHERE @rest_napoli IS NOT NULL AND NOT EXISTS (SELECT 1 FROM producto WHERE restaurante_id=@rest_napoli AND nombre='Pizza napolitana');
SET @p = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_napoli AND nombre='Pizza napolitana' LIMIT 1);
INSERT INTO plato (id_producto, tiempo_preparacion_minutos) SELECT @p, 24 FROM (SELECT 1) AS d
WHERE @p IS NOT NULL AND NOT EXISTS (SELECT 1 FROM plato WHERE id_producto=@p);

INSERT INTO producto (nombre, precio, descripcion, url_imagen, oferta_activa, disponible, oferta_id, subcategoria_id, restaurante_id)
SELECT 'Faina', 160, 'Porcion de faina de garbanzo.', NULL, 0, 1, NULL, @sub_guarnicion, @rest_napoli
FROM (SELECT 1) AS d
WHERE @rest_napoli IS NOT NULL AND NOT EXISTS (SELECT 1 FROM producto WHERE restaurante_id=@rest_napoli AND nombre='Faina');
SET @p = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_napoli AND nombre='Faina' LIMIT 1);
INSERT INTO plato (id_producto, tiempo_preparacion_minutos) SELECT @p, 10 FROM (SELECT 1) AS d
WHERE @p IS NOT NULL AND NOT EXISTS (SELECT 1 FROM plato WHERE id_producto=@p);

INSERT INTO producto (nombre, precio, descripcion, url_imagen, oferta_activa, disponible, oferta_id, subcategoria_id, restaurante_id)
SELECT 'Coca-Cola 1.5L', 180, 'Bebida cola retornable.', NULL, 0, 1, NULL, @sub_bebida, @rest_napoli
FROM (SELECT 1) AS d
WHERE @rest_napoli IS NOT NULL AND NOT EXISTS (SELECT 1 FROM producto WHERE restaurante_id=@rest_napoli AND nombre='Coca-Cola 1.5L');
SET @p = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_napoli AND nombre='Coca-Cola 1.5L' LIMIT 1);
INSERT INTO articulo (id_producto) SELECT @p FROM (SELECT 1) AS d
WHERE @p IS NOT NULL AND NOT EXISTS (SELECT 1 FROM articulo WHERE id_producto=@p);

-- ----------------------------------------------------------------------------
-- 6.3 Burger House
-- ----------------------------------------------------------------------------
INSERT INTO producto (nombre, precio, descripcion, url_imagen, oferta_activa, disponible, oferta_id, subcategoria_id, restaurante_id)
SELECT 'Hamburguesa clasica', 420, 'Medallon 150g, cheddar, lechuga y tomate.', NULL, 1, 1, @of_burger, @sub_principal, @rest_burger
FROM (SELECT 1) AS d
WHERE @rest_burger IS NOT NULL AND NOT EXISTS (SELECT 1 FROM producto WHERE restaurante_id=@rest_burger AND nombre='Hamburguesa clasica');
SET @p = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_burger AND nombre='Hamburguesa clasica' LIMIT 1);
INSERT INTO plato (id_producto, tiempo_preparacion_minutos) SELECT @p, 15 FROM (SELECT 1) AS d
WHERE @p IS NOT NULL AND NOT EXISTS (SELECT 1 FROM plato WHERE id_producto=@p);

INSERT INTO producto (nombre, precio, descripcion, url_imagen, oferta_activa, disponible, oferta_id, subcategoria_id, restaurante_id)
SELECT 'Hamburguesa bacon', 520, 'Doble medallon, bacon y cebolla caramelizada.', NULL, 1, 1, @of_burger, @sub_principal, @rest_burger
FROM (SELECT 1) AS d
WHERE @rest_burger IS NOT NULL AND NOT EXISTS (SELECT 1 FROM producto WHERE restaurante_id=@rest_burger AND nombre='Hamburguesa bacon');
SET @p = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_burger AND nombre='Hamburguesa bacon' LIMIT 1);
INSERT INTO plato (id_producto, tiempo_preparacion_minutos) SELECT @p, 18 FROM (SELECT 1) AS d
WHERE @p IS NOT NULL AND NOT EXISTS (SELECT 1 FROM plato WHERE id_producto=@p);

INSERT INTO producto (nombre, precio, descripcion, url_imagen, oferta_activa, disponible, oferta_id, subcategoria_id, restaurante_id)
SELECT 'Papas rusticas', 230, 'Porcion grande con cascara.', NULL, 0, 1, NULL, @sub_guarnicion, @rest_burger
FROM (SELECT 1) AS d
WHERE @rest_burger IS NOT NULL AND NOT EXISTS (SELECT 1 FROM producto WHERE restaurante_id=@rest_burger AND nombre='Papas rusticas');
SET @p = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_burger AND nombre='Papas rusticas' LIMIT 1);
INSERT INTO plato (id_producto, tiempo_preparacion_minutos) SELECT @p, 12 FROM (SELECT 1) AS d
WHERE @p IS NOT NULL AND NOT EXISTS (SELECT 1 FROM plato WHERE id_producto=@p);

INSERT INTO producto (nombre, precio, descripcion, url_imagen, oferta_activa, disponible, oferta_id, subcategoria_id, restaurante_id)
SELECT 'Sprite 500ml', 110, 'Bebida lima-limon.', NULL, 0, 1, NULL, @sub_bebida, @rest_burger
FROM (SELECT 1) AS d
WHERE @rest_burger IS NOT NULL AND NOT EXISTS (SELECT 1 FROM producto WHERE restaurante_id=@rest_burger AND nombre='Sprite 500ml');
SET @p = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_burger AND nombre='Sprite 500ml' LIMIT 1);
INSERT INTO articulo (id_producto) SELECT @p FROM (SELECT 1) AS d
WHERE @p IS NOT NULL AND NOT EXISTS (SELECT 1 FROM articulo WHERE id_producto=@p);

-- Combo Burger (Plato + Guarnicion + Bebida)
INSERT INTO producto (nombre, precio, descripcion, url_imagen, oferta_activa, disponible, oferta_id, subcategoria_id, restaurante_id)
SELECT 'Combo clasico', 650, 'Hamburguesa clasica + papas rusticas + Sprite.', NULL, 0, 1, NULL, @sub_principal, @rest_burger
FROM (SELECT 1) AS d
WHERE @rest_burger IS NOT NULL AND NOT EXISTS (SELECT 1 FROM producto WHERE restaurante_id=@rest_burger AND nombre='Combo clasico');
SET @p_combo = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_burger AND nombre='Combo clasico' LIMIT 1);
INSERT INTO combo (id_producto) SELECT @p_combo FROM (SELECT 1) AS d
WHERE @p_combo IS NOT NULL AND NOT EXISTS (SELECT 1 FROM combo WHERE id_producto=@p_combo);
-- Productos incluidos en el combo (ManyToMany combo -> producto)
SET @p_burg = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_burger AND nombre='Hamburguesa clasica' LIMIT 1);
SET @p_papas = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_burger AND nombre='Papas rusticas' LIMIT 1);
SET @p_sprite = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_burger AND nombre='Sprite 500ml' LIMIT 1);
INSERT INTO combo_productos_incluidos (combo_id_producto, productos_incluidos_id_producto)
SELECT @p_combo, @p_burg FROM (SELECT 1) AS d
WHERE @p_combo IS NOT NULL AND @p_burg IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM combo_productos_incluidos WHERE combo_id_producto=@p_combo AND productos_incluidos_id_producto=@p_burg);
INSERT INTO combo_productos_incluidos (combo_id_producto, productos_incluidos_id_producto)
SELECT @p_combo, @p_papas FROM (SELECT 1) AS d
WHERE @p_combo IS NOT NULL AND @p_papas IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM combo_productos_incluidos WHERE combo_id_producto=@p_combo AND productos_incluidos_id_producto=@p_papas);
INSERT INTO combo_productos_incluidos (combo_id_producto, productos_incluidos_id_producto)
SELECT @p_combo, @p_sprite FROM (SELECT 1) AS d
WHERE @p_combo IS NOT NULL AND @p_sprite IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM combo_productos_incluidos WHERE combo_id_producto=@p_combo AND productos_incluidos_id_producto=@p_sprite);

-- ----------------------------------------------------------------------------
-- 6.4 Verde Vegano
-- ----------------------------------------------------------------------------
INSERT INTO producto (nombre, precio, descripcion, url_imagen, oferta_activa, disponible, oferta_id, subcategoria_id, restaurante_id)
SELECT 'Bowl de quinoa', 460, 'Quinoa, garbanzos, palta y vegetales asados.', NULL, 0, 1, NULL, @sub_principal, @rest_vegano
FROM (SELECT 1) AS d
WHERE @rest_vegano IS NOT NULL AND NOT EXISTS (SELECT 1 FROM producto WHERE restaurante_id=@rest_vegano AND nombre='Bowl de quinoa');
SET @p = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_vegano AND nombre='Bowl de quinoa' LIMIT 1);
INSERT INTO plato (id_producto, tiempo_preparacion_minutos) SELECT @p, 14 FROM (SELECT 1) AS d
WHERE @p IS NOT NULL AND NOT EXISTS (SELECT 1 FROM plato WHERE id_producto=@p);

INSERT INTO producto (nombre, precio, descripcion, url_imagen, oferta_activa, disponible, oferta_id, subcategoria_id, restaurante_id)
SELECT 'Ensalada cesar vegana', 390, 'Lechuga, croutons, aderezo de anacardos y tofu.', NULL, 0, 1, NULL, @sub_ensalada, @rest_vegano
FROM (SELECT 1) AS d
WHERE @rest_vegano IS NOT NULL AND NOT EXISTS (SELECT 1 FROM producto WHERE restaurante_id=@rest_vegano AND nombre='Ensalada cesar vegana');
SET @p = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_vegano AND nombre='Ensalada cesar vegana' LIMIT 1);
INSERT INTO plato (id_producto, tiempo_preparacion_minutos) SELECT @p, 10 FROM (SELECT 1) AS d
WHERE @p IS NOT NULL AND NOT EXISTS (SELECT 1 FROM plato WHERE id_producto=@p);

INSERT INTO producto (nombre, precio, descripcion, url_imagen, oferta_activa, disponible, oferta_id, subcategoria_id, restaurante_id)
SELECT 'Jugo natural de naranja', 150, 'Exprimido del dia, 400ml.', NULL, 0, 1, NULL, @sub_bebida, @rest_vegano
FROM (SELECT 1) AS d
WHERE @rest_vegano IS NOT NULL AND NOT EXISTS (SELECT 1 FROM producto WHERE restaurante_id=@rest_vegano AND nombre='Jugo natural de naranja');
SET @p = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_vegano AND nombre='Jugo natural de naranja' LIMIT 1);
INSERT INTO articulo (id_producto) SELECT @p FROM (SELECT 1) AS d
WHERE @p IS NOT NULL AND NOT EXISTS (SELECT 1 FROM articulo WHERE id_producto=@p);

-- ----------------------------------------------------------------------------
-- 6.5 Heladeria Crema
-- ----------------------------------------------------------------------------
INSERT INTO producto (nombre, precio, descripcion, url_imagen, oferta_activa, disponible, oferta_id, subcategoria_id, restaurante_id)
SELECT 'Cuarto de helado', 320, 'Hasta 3 gustos a eleccion.', NULL, 1, 1, @of_postre, @sub_postre, @rest_helados
FROM (SELECT 1) AS d
WHERE @rest_helados IS NOT NULL AND NOT EXISTS (SELECT 1 FROM producto WHERE restaurante_id=@rest_helados AND nombre='Cuarto de helado');
SET @p = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_helados AND nombre='Cuarto de helado' LIMIT 1);
INSERT INTO plato (id_producto, tiempo_preparacion_minutos) SELECT @p, 5 FROM (SELECT 1) AS d
WHERE @p IS NOT NULL AND NOT EXISTS (SELECT 1 FROM plato WHERE id_producto=@p);

INSERT INTO producto (nombre, precio, descripcion, url_imagen, oferta_activa, disponible, oferta_id, subcategoria_id, restaurante_id)
SELECT 'Copa Crema especial', 280, 'Helado, dulce de leche, brownie y crema.', NULL, 1, 1, @of_postre, @sub_postre, @rest_helados
FROM (SELECT 1) AS d
WHERE @rest_helados IS NOT NULL AND NOT EXISTS (SELECT 1 FROM producto WHERE restaurante_id=@rest_helados AND nombre='Copa Crema especial');
SET @p = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_helados AND nombre='Copa Crema especial' LIMIT 1);
INSERT INTO plato (id_producto, tiempo_preparacion_minutos) SELECT @p, 6 FROM (SELECT 1) AS d
WHERE @p IS NOT NULL AND NOT EXISTS (SELECT 1 FROM plato WHERE id_producto=@p);

INSERT INTO producto (nombre, precio, descripcion, url_imagen, oferta_activa, disponible, oferta_id, subcategoria_id, restaurante_id)
SELECT 'Pote 1 litro', 540, 'Pote familiar para llevar.', NULL, 0, 1, NULL, @sub_postre, @rest_helados
FROM (SELECT 1) AS d
WHERE @rest_helados IS NOT NULL AND NOT EXISTS (SELECT 1 FROM producto WHERE restaurante_id=@rest_helados AND nombre='Pote 1 litro');
SET @p = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_helados AND nombre='Pote 1 litro' LIMIT 1);
INSERT INTO articulo (id_producto) SELECT @p FROM (SELECT 1) AS d
WHERE @p IS NOT NULL AND NOT EXISTS (SELECT 1 FROM articulo WHERE id_producto=@p);

-- ============================================================================
-- 7. PLATO_INGREDIENTES (relacion ManyToMany plato <-> ingrediente)
-- ============================================================================
-- Parrillada para dos
INSERT IGNORE INTO plato_ingredientes (plato_id_producto, ingredientes_id_ingrediente)
SELECT pl.id_producto, i.id_ingrediente
FROM producto pl
JOIN ingrediente i ON i.restaurante_id = @rest_parrilla
WHERE pl.restaurante_id = @rest_parrilla AND pl.nombre = 'Parrillada para dos'
  AND i.nombre IN ('Asado de tira','Chorizo','Morcilla','Ensalada criolla');
-- Provolone
INSERT IGNORE INTO plato_ingredientes (plato_id_producto, ingredientes_id_ingrediente)
SELECT pl.id_producto, i.id_ingrediente
FROM producto pl
JOIN ingrediente i ON i.restaurante_id = @rest_parrilla
WHERE pl.restaurante_id = @rest_parrilla AND pl.nombre = 'Provolone a la parrilla'
  AND i.nombre IN ('Provolone');

-- Pizza muzzarella
INSERT IGNORE INTO plato_ingredientes (plato_id_producto, ingredientes_id_ingrediente)
SELECT pl.id_producto, i.id_ingrediente
FROM producto pl
JOIN ingrediente i ON i.restaurante_id = @rest_napoli
WHERE pl.restaurante_id = @rest_napoli AND pl.nombre = 'Pizza muzzarella'
  AND i.nombre IN ('Muzzarella','Salsa de tomate');
-- Pizza napolitana
INSERT IGNORE INTO plato_ingredientes (plato_id_producto, ingredientes_id_ingrediente)
SELECT pl.id_producto, i.id_ingrediente
FROM producto pl
JOIN ingrediente i ON i.restaurante_id = @rest_napoli
WHERE pl.restaurante_id = @rest_napoli AND pl.nombre = 'Pizza napolitana'
  AND i.nombre IN ('Muzzarella','Salsa de tomate','Albahaca');

-- Hamburguesa clasica
INSERT IGNORE INTO plato_ingredientes (plato_id_producto, ingredientes_id_ingrediente)
SELECT pl.id_producto, i.id_ingrediente
FROM producto pl
JOIN ingrediente i ON i.restaurante_id = @rest_burger
WHERE pl.restaurante_id = @rest_burger AND pl.nombre = 'Hamburguesa clasica'
  AND i.nombre IN ('Medallon de carne','Queso cheddar','Lechuga','Tomate');
-- Hamburguesa bacon
INSERT IGNORE INTO plato_ingredientes (plato_id_producto, ingredientes_id_ingrediente)
SELECT pl.id_producto, i.id_ingrediente
FROM producto pl
JOIN ingrediente i ON i.restaurante_id = @rest_burger
WHERE pl.restaurante_id = @rest_burger AND pl.nombre = 'Hamburguesa bacon'
  AND i.nombre IN ('Medallon de carne','Queso cheddar','Bacon','Cebolla caramelizada');

-- Bowl de quinoa
INSERT IGNORE INTO plato_ingredientes (plato_id_producto, ingredientes_id_ingrediente)
SELECT pl.id_producto, i.id_ingrediente
FROM producto pl
JOIN ingrediente i ON i.restaurante_id = @rest_vegano
WHERE pl.restaurante_id = @rest_vegano AND pl.nombre = 'Bowl de quinoa'
  AND i.nombre IN ('Quinoa','Garbanzos','Palta');
-- Ensalada cesar vegana
INSERT IGNORE INTO plato_ingredientes (plato_id_producto, ingredientes_id_ingrediente)
SELECT pl.id_producto, i.id_ingrediente
FROM producto pl
JOIN ingrediente i ON i.restaurante_id = @rest_vegano
WHERE pl.restaurante_id = @rest_vegano AND pl.nombre = 'Ensalada cesar vegana'
  AND i.nombre IN ('Tofu');

-- ============================================================================
-- 8. CLIENTES (usuario + cliente + direcciones)
-- ============================================================================
-- Lucia
INSERT INTO usuario (nombre, email, foto_perfil, rol)
SELECT 'Lucia Fernandez', 'lucia@trego.seed', NULL, 'Cliente'
FROM (SELECT 1) AS d
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'lucia@trego.seed');
SET @cli_lucia = (SELECT id_usuario FROM usuario WHERE email = 'lucia@trego.seed' LIMIT 1);
INSERT INTO cliente (id_usuario, uid_cliente, fcm_token, telefono, habilitado)
SELECT @cli_lucia, 'uid-lucia-seed', NULL, '098100100', 1 FROM (SELECT 1) AS d
WHERE @cli_lucia IS NOT NULL AND NOT EXISTS (SELECT 1 FROM cliente WHERE id_usuario=@cli_lucia);
INSERT INTO cliente_direcciones (cliente_id_usuario, tag, calle, numero, apartamento, esquina, latitud, longitud)
SELECT @cli_lucia, 'Casa', 'Av. Italia', '2500', '301', 'Pereira', -34.90889, -56.14210 FROM (SELECT 1) AS d
WHERE @cli_lucia IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM cliente_direcciones WHERE cliente_id_usuario=@cli_lucia AND calle='Av. Italia' AND numero='2500');

-- Martin
INSERT INTO usuario (nombre, email, foto_perfil, rol)
SELECT 'Martin Suarez', 'martin@trego.seed', NULL, 'Cliente'
FROM (SELECT 1) AS d
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'martin@trego.seed');
SET @cli_martin = (SELECT id_usuario FROM usuario WHERE email = 'martin@trego.seed' LIMIT 1);
INSERT INTO cliente (id_usuario, uid_cliente, fcm_token, telefono, habilitado)
SELECT @cli_martin, 'uid-martin-seed', NULL, '098200200', 1 FROM (SELECT 1) AS d
WHERE @cli_martin IS NOT NULL AND NOT EXISTS (SELECT 1 FROM cliente WHERE id_usuario=@cli_martin);
INSERT INTO cliente_direcciones (cliente_id_usuario, tag, calle, numero, apartamento, esquina, latitud, longitud)
SELECT @cli_martin, 'Trabajo', 'Rambla Rep. del Peru', '1300', NULL, 'Pena', -34.91900, -56.15100 FROM (SELECT 1) AS d
WHERE @cli_martin IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM cliente_direcciones WHERE cliente_id_usuario=@cli_martin AND calle='Rambla Rep. del Peru' AND numero='1300');

-- Sofia
INSERT INTO usuario (nombre, email, foto_perfil, rol)
SELECT 'Sofia Rodriguez', 'sofia@trego.seed', NULL, 'Cliente'
FROM (SELECT 1) AS d
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'sofia@trego.seed');
SET @cli_sofia = (SELECT id_usuario FROM usuario WHERE email = 'sofia@trego.seed' LIMIT 1);
INSERT INTO cliente (id_usuario, uid_cliente, fcm_token, telefono, habilitado)
SELECT @cli_sofia, 'uid-sofia-seed', NULL, '098300300', 1 FROM (SELECT 1) AS d
WHERE @cli_sofia IS NOT NULL AND NOT EXISTS (SELECT 1 FROM cliente WHERE id_usuario=@cli_sofia);
INSERT INTO cliente_direcciones (cliente_id_usuario, tag, calle, numero, apartamento, esquina, latitud, longitud)
SELECT @cli_sofia, 'Casa', 'Canelones', '1820', '5B', 'Tristan Narvaja', -34.90420, -56.18020 FROM (SELECT 1) AS d
WHERE @cli_sofia IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM cliente_direcciones WHERE cliente_id_usuario=@cli_sofia AND calle='Canelones' AND numero='1820');

-- Diego (deshabilitado, para probar ese caso)
INSERT INTO usuario (nombre, email, foto_perfil, rol)
SELECT 'Diego Pereyra', 'diego@trego.seed', NULL, 'Cliente'
FROM (SELECT 1) AS d
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'diego@trego.seed');
SET @cli_diego = (SELECT id_usuario FROM usuario WHERE email = 'diego@trego.seed' LIMIT 1);
INSERT INTO cliente (id_usuario, uid_cliente, fcm_token, telefono, habilitado)
SELECT @cli_diego, 'uid-diego-seed', NULL, '098400400', 0 FROM (SELECT 1) AS d
WHERE @cli_diego IS NOT NULL AND NOT EXISTS (SELECT 1 FROM cliente WHERE id_usuario=@cli_diego);

-- ============================================================================
-- 9. COMENTARIOS / RESENIAS  (unique: 1 por cliente+restaurante)
-- ============================================================================
INSERT INTO comentario (texto, calificacion, cliente_id, restaurante_id, fecha_creacion)
SELECT 'Excelente asado, abundante y bien servido.', 5, @cli_lucia, @rest_parrilla, DATE_SUB(NOW(), INTERVAL 10 DAY)
FROM (SELECT 1) AS d
WHERE @cli_lucia IS NOT NULL AND @rest_parrilla IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM comentario WHERE cliente_id=@cli_lucia AND restaurante_id=@rest_parrilla);

INSERT INTO comentario (texto, calificacion, cliente_id, restaurante_id, fecha_creacion)
SELECT 'La pizza llego caliente y muy rica. Recomendado.', 5, @cli_martin, @rest_napoli, DATE_SUB(NOW(), INTERVAL 7 DAY)
FROM (SELECT 1) AS d
WHERE @cli_martin IS NOT NULL AND @rest_napoli IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM comentario WHERE cliente_id=@cli_martin AND restaurante_id=@rest_napoli);

INSERT INTO comentario (texto, calificacion, cliente_id, restaurante_id, fecha_creacion)
SELECT 'Buena hamburguesa pero tardo bastante el envio.', 3, @cli_sofia, @rest_burger, DATE_SUB(NOW(), INTERVAL 4 DAY)
FROM (SELECT 1) AS d
WHERE @cli_sofia IS NOT NULL AND @rest_burger IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM comentario WHERE cliente_id=@cli_sofia AND restaurante_id=@rest_burger);

INSERT INTO comentario (texto, calificacion, cliente_id, restaurante_id, fecha_creacion)
SELECT 'Opciones veganas muy completas, volvere.', 4, @cli_lucia, @rest_vegano, DATE_SUB(NOW(), INTERVAL 3 DAY)
FROM (SELECT 1) AS d
WHERE @cli_lucia IS NOT NULL AND @rest_vegano IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM comentario WHERE cliente_id=@cli_lucia AND restaurante_id=@rest_vegano);

INSERT INTO comentario (texto, calificacion, cliente_id, restaurante_id, fecha_creacion)
SELECT 'El mejor helado artesanal de la zona.', 5, @cli_martin, @rest_helados, DATE_SUB(NOW(), INTERVAL 2 DAY)
FROM (SELECT 1) AS d
WHERE @cli_martin IS NOT NULL AND @rest_helados IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM comentario WHERE cliente_id=@cli_martin AND restaurante_id=@rest_helados);

-- Recalcular calificacion promedio de cada restaurante segun sus comentarios
UPDATE restaurante r
SET calificacion_prom = COALESCE((
  SELECT AVG(c.calificacion) FROM comentario c WHERE c.restaurante_id = r.id_usuario
), r.calificacion_prom)
WHERE r.id_usuario IN (@rest_parrilla, @rest_napoli, @rest_burger, @rest_vegano, @rest_helados);

-- ============================================================================
-- 10. PEDIDOS  (pago -> [reclamo] -> pedido -> producto_pedido)
--     Cada pedido se identifica de forma estable por el id_transaccion del pago.
-- ============================================================================

-- ---------------------------------------------------------------------------
-- Pedido 1: Lucia en La Parrilla - ENTREGADO (hace 10 dias) con RECLAMO resuelto
-- ---------------------------------------------------------------------------
SET @ped_tx = 'SEED-PED-0001';
INSERT INTO pago (fecha_pago, monto, metodo_de_pago, moneda, id_transaccion, nro_ultim_dig_tarjeta)
SELECT DATE_SUB(NOW(), INTERVAL 10 DAY), 1380, 'MercadoPago', 'UYU', @ped_tx, '4242'
FROM (SELECT 1) AS d
WHERE NOT EXISTS (SELECT 1 FROM pago WHERE id_transaccion = @ped_tx);
SET @pago_id = (SELECT id_pago FROM pago WHERE id_transaccion = @ped_tx LIMIT 1);

INSERT INTO reclamo (texto, estado, fecha_reclamo, motivo_rechazo)
SELECT 'Faltaba la guarnicion en el pedido.', 'Resuelto', DATE_SUB(NOW(), INTERVAL 9 DAY), NULL
FROM (SELECT 1) AS d
WHERE @pago_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM pedido WHERE pago_id = @pago_id AND reclamo_id IS NOT NULL);
SET @reclamo_id = (SELECT MAX(id_reclamo) FROM reclamo WHERE texto = 'Faltaba la guarnicion en el pedido.');

INSERT INTO pedido (fecha_creacion, fecha_expiracion, total, estado, horario_entrega, razon_cancelacion,
       tiempo_preparacion, cliente_id, restaurante_id, reclamo_id, pago_id,
       tag, calle, numero, apartamento, esquina, latitud, longitud)
SELECT DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY), 1380, 'Entregado',
       DATE_SUB(NOW(), INTERVAL 10 DAY) + INTERVAL 45 MINUTE, NULL,
       35, @cli_lucia, @rest_parrilla, @reclamo_id, @pago_id,
       'Casa', 'Av. Italia', '2500', '301', 'Pereira', -34.90889, -56.14210
FROM (SELECT 1) AS d
WHERE @pago_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM pedido WHERE pago_id = @pago_id);
SET @ped_id = (SELECT id_pedido FROM pedido WHERE pago_id = @pago_id LIMIT 1);

SET @prod = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_parrilla AND nombre='Parrillada para dos' LIMIT 1);
INSERT INTO producto_pedido (cantidad, precio_suma, comentario_cliente, producto_id, pedido_id)
SELECT 1, 1290, 'Sin sal en la carne', @prod, @ped_id FROM (SELECT 1) AS d
WHERE @ped_id IS NOT NULL AND @prod IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM producto_pedido WHERE pedido_id=@ped_id AND producto_id=@prod);
SET @prod = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_parrilla AND nombre='Agua mineral 600ml' LIMIT 1);
INSERT INTO producto_pedido (cantidad, precio_suma, comentario_cliente, producto_id, pedido_id)
SELECT 1, 90, NULL, @prod, @ped_id FROM (SELECT 1) AS d
WHERE @ped_id IS NOT NULL AND @prod IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM producto_pedido WHERE pedido_id=@ped_id AND producto_id=@prod);

-- ---------------------------------------------------------------------------
-- Pedido 2: Martin en Napoli - ENTREGADO (hace 7 dias). Pizza con oferta 20%.
-- ---------------------------------------------------------------------------
SET @ped_tx = 'SEED-PED-0002';
INSERT INTO pago (fecha_pago, monto, metodo_de_pago, moneda, id_transaccion, nro_ultim_dig_tarjeta)
SELECT DATE_SUB(NOW(), INTERVAL 7 DAY), 1044, 'MercadoPago', 'UYU', @ped_tx, '5588'
FROM (SELECT 1) AS d
WHERE NOT EXISTS (SELECT 1 FROM pago WHERE id_transaccion = @ped_tx);
SET @pago_id = (SELECT id_pago FROM pago WHERE id_transaccion = @ped_tx LIMIT 1);

INSERT INTO pedido (fecha_creacion, fecha_expiracion, total, estado, horario_entrega, razon_cancelacion,
       tiempo_preparacion, cliente_id, restaurante_id, reclamo_id, pago_id,
       tag, calle, numero, apartamento, esquina, latitud, longitud)
SELECT DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), 1044, 'Entregado',
       DATE_SUB(NOW(), INTERVAL 7 DAY) + INTERVAL 40 MINUTE, NULL,
       24, @cli_martin, @rest_napoli, NULL, @pago_id,
       'Trabajo', 'Rambla Rep. del Peru', '1300', NULL, 'Pena', -34.91900, -56.15100
FROM (SELECT 1) AS d
WHERE @pago_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM pedido WHERE pago_id = @pago_id);
SET @ped_id = (SELECT id_pedido FROM pedido WHERE pago_id = @pago_id LIMIT 1);

SET @prod = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_napoli AND nombre='Pizza muzzarella' LIMIT 1);
INSERT INTO producto_pedido (cantidad, precio_suma, comentario_cliente, producto_id, pedido_id)
SELECT 2, 864, NULL, @prod, @ped_id FROM (SELECT 1) AS d
WHERE @ped_id IS NOT NULL AND @prod IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM producto_pedido WHERE pedido_id=@ped_id AND producto_id=@prod);
SET @prod = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_napoli AND nombre='Coca-Cola 1.5L' LIMIT 1);
INSERT INTO producto_pedido (cantidad, precio_suma, comentario_cliente, producto_id, pedido_id)
SELECT 1, 180, NULL, @prod, @ped_id FROM (SELECT 1) AS d
WHERE @ped_id IS NOT NULL AND @prod IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM producto_pedido WHERE pedido_id=@ped_id AND producto_id=@prod);

-- ---------------------------------------------------------------------------
-- Pedido 3: Sofia en Burger - EN CAMINO (hoy)
-- ---------------------------------------------------------------------------
SET @ped_tx = 'SEED-PED-0003';
INSERT INTO pago (fecha_pago, monto, metodo_de_pago, moneda, id_transaccion, nro_ultim_dig_tarjeta)
SELECT DATE_SUB(NOW(), INTERVAL 30 MINUTE), 825, 'MercadoPago', 'UYU', @ped_tx, '1111'
FROM (SELECT 1) AS d
WHERE NOT EXISTS (SELECT 1 FROM pago WHERE id_transaccion = @ped_tx);
SET @pago_id = (SELECT id_pago FROM pago WHERE id_transaccion = @ped_tx LIMIT 1);

INSERT INTO pedido (fecha_creacion, fecha_expiracion, total, estado, horario_entrega, razon_cancelacion,
       tiempo_preparacion, cliente_id, restaurante_id, reclamo_id, pago_id,
       tag, calle, numero, apartamento, esquina, latitud, longitud)
SELECT DATE_SUB(NOW(), INTERVAL 30 MINUTE), DATE_ADD(NOW(), INTERVAL 23 HOUR), 825, 'EnCamino',
       DATE_ADD(NOW(), INTERVAL 15 MINUTE), NULL,
       18, @cli_sofia, @rest_burger, NULL, @pago_id,
       'Casa', 'Canelones', '1820', '5B', 'Tristan Narvaja', -34.90420, -56.18020
FROM (SELECT 1) AS d
WHERE @pago_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM pedido WHERE pago_id = @pago_id);
SET @ped_id = (SELECT id_pedido FROM pedido WHERE pago_id = @pago_id LIMIT 1);

SET @prod = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_burger AND nombre='Hamburguesa bacon' LIMIT 1);
INSERT INTO producto_pedido (cantidad, precio_suma, comentario_cliente, producto_id, pedido_id)
SELECT 1, 390, 'Sin cebolla', @prod, @ped_id FROM (SELECT 1) AS d
WHERE @ped_id IS NOT NULL AND @prod IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM producto_pedido WHERE pedido_id=@ped_id AND producto_id=@prod);
SET @prod = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_burger AND nombre='Papas rusticas' LIMIT 1);
INSERT INTO producto_pedido (cantidad, precio_suma, comentario_cliente, producto_id, pedido_id)
SELECT 1, 230, NULL, @prod, @ped_id FROM (SELECT 1) AS d
WHERE @ped_id IS NOT NULL AND @prod IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM producto_pedido WHERE pedido_id=@ped_id AND producto_id=@prod);
SET @prod = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_burger AND nombre='Sprite 500ml' LIMIT 1);
INSERT INTO producto_pedido (cantidad, precio_suma, comentario_cliente, producto_id, pedido_id)
SELECT 1, 110, NULL, @prod, @ped_id FROM (SELECT 1) AS d
WHERE @ped_id IS NOT NULL AND @prod IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM producto_pedido WHERE pedido_id=@ped_id AND producto_id=@prod);

-- ---------------------------------------------------------------------------
-- Pedido 4: Lucia en Vegano - EN PREPARACION (hoy)
-- ---------------------------------------------------------------------------
SET @ped_tx = 'SEED-PED-0004';
INSERT INTO pago (fecha_pago, monto, metodo_de_pago, moneda, id_transaccion, nro_ultim_dig_tarjeta)
SELECT DATE_SUB(NOW(), INTERVAL 15 MINUTE), 610, 'MercadoPago', 'UYU', @ped_tx, '7777'
FROM (SELECT 1) AS d
WHERE NOT EXISTS (SELECT 1 FROM pago WHERE id_transaccion = @ped_tx);
SET @pago_id = (SELECT id_pago FROM pago WHERE id_transaccion = @ped_tx LIMIT 1);

INSERT INTO pedido (fecha_creacion, fecha_expiracion, total, estado, horario_entrega, razon_cancelacion,
       tiempo_preparacion, cliente_id, restaurante_id, reclamo_id, pago_id,
       tag, calle, numero, apartamento, esquina, latitud, longitud)
SELECT DATE_SUB(NOW(), INTERVAL 15 MINUTE), DATE_ADD(NOW(), INTERVAL 23 HOUR), 610, 'EnPreparacion',
       DATE_ADD(NOW(), INTERVAL 30 MINUTE), NULL,
       14, @cli_lucia, @rest_vegano, NULL, @pago_id,
       'Casa', 'Av. Italia', '2500', '301', 'Pereira', -34.90889, -56.14210
FROM (SELECT 1) AS d
WHERE @pago_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM pedido WHERE pago_id = @pago_id);
SET @ped_id = (SELECT id_pedido FROM pedido WHERE pago_id = @pago_id LIMIT 1);

SET @prod = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_vegano AND nombre='Bowl de quinoa' LIMIT 1);
INSERT INTO producto_pedido (cantidad, precio_suma, comentario_cliente, producto_id, pedido_id)
SELECT 1, 460, NULL, @prod, @ped_id FROM (SELECT 1) AS d
WHERE @ped_id IS NOT NULL AND @prod IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM producto_pedido WHERE pedido_id=@ped_id AND producto_id=@prod);
SET @prod = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_vegano AND nombre='Jugo natural de naranja' LIMIT 1);
INSERT INTO producto_pedido (cantidad, precio_suma, comentario_cliente, producto_id, pedido_id)
SELECT 1, 150, NULL, @prod, @ped_id FROM (SELECT 1) AS d
WHERE @ped_id IS NOT NULL AND @prod IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM producto_pedido WHERE pedido_id=@ped_id AND producto_id=@prod);

-- ---------------------------------------------------------------------------
-- Pedido 5: Martin en Burger - CANCELADO (hace 2 dias)
-- ---------------------------------------------------------------------------
SET @ped_tx = 'SEED-PED-0005';
INSERT INTO pago (fecha_pago, monto, metodo_de_pago, moneda, id_transaccion, nro_ultim_dig_tarjeta)
SELECT DATE_SUB(NOW(), INTERVAL 2 DAY), 650, 'MercadoPago', 'UYU', @ped_tx, '9090'
FROM (SELECT 1) AS d
WHERE NOT EXISTS (SELECT 1 FROM pago WHERE id_transaccion = @ped_tx);
SET @pago_id = (SELECT id_pago FROM pago WHERE id_transaccion = @ped_tx LIMIT 1);

INSERT INTO pedido (fecha_creacion, fecha_expiracion, total, estado, horario_entrega, razon_cancelacion,
       tiempo_preparacion, cliente_id, restaurante_id, reclamo_id, pago_id,
       tag, calle, numero, apartamento, esquina, latitud, longitud)
SELECT DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), 650, 'Cancelado',
       NULL, 'El local cerro antes de preparar el pedido',
       NULL, @cli_martin, @rest_burger, NULL, @pago_id,
       'Trabajo', 'Rambla Rep. del Peru', '1300', NULL, 'Pena', -34.91900, -56.15100
FROM (SELECT 1) AS d
WHERE @pago_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM pedido WHERE pago_id = @pago_id);
SET @ped_id = (SELECT id_pedido FROM pedido WHERE pago_id = @pago_id LIMIT 1);

SET @prod = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_burger AND nombre='Combo clasico' LIMIT 1);
INSERT INTO producto_pedido (cantidad, precio_suma, comentario_cliente, producto_id, pedido_id)
SELECT 1, 650, NULL, @prod, @ped_id FROM (SELECT 1) AS d
WHERE @ped_id IS NOT NULL AND @prod IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM producto_pedido WHERE pedido_id=@ped_id AND producto_id=@prod);

-- ---------------------------------------------------------------------------
-- Pedido 6: Sofia en Heladeria - PAGADO (recien, esperando aceptacion del local)
-- ---------------------------------------------------------------------------
SET @ped_tx = 'SEED-PED-0006';
INSERT INTO pago (fecha_pago, monto, metodo_de_pago, moneda, id_transaccion, nro_ultim_dig_tarjeta)
SELECT DATE_SUB(NOW(), INTERVAL 5 MINUTE), 440, 'MercadoPago', 'UYU', @ped_tx, '3030'
FROM (SELECT 1) AS d
WHERE NOT EXISTS (SELECT 1 FROM pago WHERE id_transaccion = @ped_tx);
SET @pago_id = (SELECT id_pago FROM pago WHERE id_transaccion = @ped_tx LIMIT 1);

INSERT INTO pedido (fecha_creacion, fecha_expiracion, total, estado, horario_entrega, razon_cancelacion,
       tiempo_preparacion, cliente_id, restaurante_id, reclamo_id, pago_id,
       tag, calle, numero, apartamento, esquina, latitud, longitud)
SELECT DATE_SUB(NOW(), INTERVAL 5 MINUTE), DATE_ADD(NOW(), INTERVAL 23 HOUR), 440, 'Pagado',
       DATE_ADD(NOW(), INTERVAL 40 MINUTE), NULL,
       NULL, @cli_sofia, @rest_helados, NULL, @pago_id,
       'Casa', 'Canelones', '1820', '5B', 'Tristan Narvaja', -34.90420, -56.18020
FROM (SELECT 1) AS d
WHERE @pago_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM pedido WHERE pago_id = @pago_id);
SET @ped_id = (SELECT id_pedido FROM pedido WHERE pago_id = @pago_id LIMIT 1);

SET @prod = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_helados AND nombre='Cuarto de helado' LIMIT 1);
INSERT INTO producto_pedido (cantidad, precio_suma, comentario_cliente, producto_id, pedido_id)
SELECT 1, 160, '3 gustos: dulce de leche, chocolate, frutilla', @prod, @ped_id FROM (SELECT 1) AS d
WHERE @ped_id IS NOT NULL AND @prod IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM producto_pedido WHERE pedido_id=@ped_id AND producto_id=@prod);
SET @prod = (SELECT id_producto FROM producto WHERE restaurante_id=@rest_helados AND nombre='Copa Crema especial' LIMIT 1);
INSERT INTO producto_pedido (cantidad, precio_suma, comentario_cliente, producto_id, pedido_id)
SELECT 1, 140, NULL, @prod, @ped_id FROM (SELECT 1) AS d
WHERE @ped_id IS NOT NULL AND @prod IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM producto_pedido WHERE pedido_id=@ped_id AND producto_id=@prod);

-- ============================================================================
-- 11. RESUMEN
-- ============================================================================
SELECT 'Restaurantes' AS entidad, COUNT(*) AS total FROM usuario WHERE email LIKE '%@trego.seed' AND rol='Restaurante'
UNION ALL SELECT 'Clientes', COUNT(*) FROM usuario WHERE email LIKE '%@trego.seed' AND rol='Cliente'
UNION ALL SELECT 'Productos (demo)', COUNT(*) FROM producto p JOIN usuario u ON u.id_usuario=p.restaurante_id WHERE u.email LIKE '%@trego.seed'
UNION ALL SELECT 'Ofertas', COUNT(*) FROM oferta
UNION ALL SELECT 'Comentarios', COUNT(*) FROM comentario c JOIN usuario u ON u.id_usuario=c.restaurante_id WHERE u.email LIKE '%@trego.seed'
UNION ALL SELECT 'Pedidos', COUNT(*) FROM pedido pe JOIN pago pa ON pa.id_pago=pe.pago_id WHERE pa.id_transaccion LIKE 'SEED-PED-%';

SELECT pe.id_pedido, u.nombre AS restaurante, cu.nombre AS cliente, pe.estado, pe.total, pe.fecha_creacion
FROM pedido pe
JOIN pago pa ON pa.id_pago = pe.pago_id
JOIN usuario u  ON u.id_usuario = pe.restaurante_id
JOIN usuario cu ON cu.id_usuario = pe.cliente_id
WHERE pa.id_transaccion LIKE 'SEED-PED-%'
ORDER BY pe.fecha_creacion DESC;
