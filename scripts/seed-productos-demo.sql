-- Productos de prueba para restaurantes demo (Burger, Pizza, Sushi)
-- Ejecutar: mysql -u root -p1234 < scripts/seed-productos-demo.sql
USE tregodb;

-- Burger Montevideo (id 3)
SET @r = (SELECT id_usuario FROM usuario WHERE email = 'seed-burger@trego.test' LIMIT 1);
SET @r = IFNULL(@r, 3);

INSERT INTO producto (nombre, precio, descripcion, url_imagen, disponible, oferta_activa, restaurante_id)
SELECT 'Hamburguesa clásica', 450, 'Carne 150g, lechuga, tomate y salsa especial', NULL, 1, 0, @r
WHERE @r IS NOT NULL AND NOT EXISTS (SELECT 1 FROM producto WHERE restaurante_id = @r LIMIT 1);
SET @p = LAST_INSERT_ID();
INSERT INTO plato (id_producto, tiempo_preparacion_minutos)
SELECT @p, 18 FROM DUAL WHERE @p > 0;

INSERT INTO producto (nombre, precio, descripcion, url_imagen, disponible, oferta_activa, restaurante_id)
SELECT 'Papas fritas', 220, 'Porción grande crujientes', NULL, 1, 0, @r
WHERE @r IS NOT NULL AND (SELECT COUNT(*) FROM producto WHERE restaurante_id = @r) < 2;
SET @p = LAST_INSERT_ID();
INSERT INTO plato (id_producto, tiempo_preparacion_minutos)
SELECT @p, 10 FROM DUAL WHERE @p > 0;

INSERT INTO producto (nombre, precio, descripcion, url_imagen, disponible, oferta_activa, restaurante_id)
SELECT 'Coca-Cola 500ml', 120, 'Bebida fría', NULL, 1, 0, @r
WHERE @r IS NOT NULL AND (SELECT COUNT(*) FROM producto WHERE restaurante_id = @r) < 3;
SET @p = LAST_INSERT_ID();
INSERT INTO plato (id_producto, tiempo_preparacion_minutos)
SELECT @p, 2 FROM DUAL WHERE @p > 0;

UPDATE restaurante SET abierto = 1 WHERE id_usuario = @r;

-- Pizza Centro (id 4)
SET @r = (SELECT id_usuario FROM usuario WHERE email = 'seed-pizza@trego.test' LIMIT 1);
SET @r = IFNULL(@r, 4);

INSERT INTO producto (nombre, precio, descripcion, url_imagen, disponible, oferta_activa, restaurante_id)
SELECT 'Pizza muzzarella', 550, 'Masa a la piedra, salsa y muzzarella', NULL, 1, 0, @r
WHERE @r IS NOT NULL AND NOT EXISTS (SELECT 1 FROM producto WHERE restaurante_id = @r LIMIT 1);
SET @p = LAST_INSERT_ID();
INSERT INTO plato (id_producto, tiempo_preparacion_minutos)
SELECT @p, 25 FROM DUAL WHERE @p > 0;

INSERT INTO producto (nombre, precio, descripcion, url_imagen, disponible, oferta_activa, restaurante_id)
SELECT 'Pizza napolitana', 620, 'Tomate, ajo, albahaca y queso', NULL, 1, 0, @r
WHERE @r IS NOT NULL AND (SELECT COUNT(*) FROM producto WHERE restaurante_id = @r) < 2;
SET @p = LAST_INSERT_ID();
INSERT INTO plato (id_producto, tiempo_preparacion_minutos)
SELECT @p, 28 FROM DUAL WHERE @p > 0;

INSERT INTO producto (nombre, precio, descripcion, url_imagen, disponible, oferta_activa, restaurante_id)
SELECT 'Fainá', 180, 'Porción de fainá casero', NULL, 1, 0, @r
WHERE @r IS NOT NULL AND (SELECT COUNT(*) FROM producto WHERE restaurante_id = @r) < 3;
SET @p = LAST_INSERT_ID();
INSERT INTO plato (id_producto, tiempo_preparacion_minutos)
SELECT @p, 12 FROM DUAL WHERE @p > 0;

UPDATE restaurante SET abierto = 1 WHERE id_usuario = @r;

-- Sushi Pocitos (id 5)
SET @r = (SELECT id_usuario FROM usuario WHERE email = 'seed-sushi@trego.test' LIMIT 1);
SET @r = IFNULL(@r, 5);

INSERT INTO producto (nombre, precio, descripcion, url_imagen, disponible, oferta_activa, restaurante_id)
SELECT 'Roll California', 480, '8 piezas con palta y kanikama', NULL, 1, 0, @r
WHERE @r IS NOT NULL AND NOT EXISTS (SELECT 1 FROM producto WHERE restaurante_id = @r LIMIT 1);
SET @p = LAST_INSERT_ID();
INSERT INTO plato (id_producto, tiempo_preparacion_minutos)
SELECT @p, 20 FROM DUAL WHERE @p > 0;

INSERT INTO producto (nombre, precio, descripcion, url_imagen, disponible, oferta_activa, restaurante_id)
SELECT 'Sashimi salmón', 650, '12 láminas de salmón fresco', NULL, 1, 0, @r
WHERE @r IS NOT NULL AND (SELECT COUNT(*) FROM producto WHERE restaurante_id = @r) < 2;
SET @p = LAST_INSERT_ID();
INSERT INTO plato (id_producto, tiempo_preparacion_minutos)
SELECT @p, 15 FROM DUAL WHERE @p > 0;

INSERT INTO producto (nombre, precio, descripcion, url_imagen, disponible, oferta_activa, restaurante_id)
SELECT 'Arroz primavera', 200, 'Guarnición de arroz y vegetales', NULL, 1, 0, @r
WHERE @r IS NOT NULL AND (SELECT COUNT(*) FROM producto WHERE restaurante_id = @r) < 3;
SET @p = LAST_INSERT_ID();
INSERT INTO plato (id_producto, tiempo_preparacion_minutos)
SELECT @p, 12 FROM DUAL WHERE @p > 0;

UPDATE restaurante SET abierto = 1 WHERE id_usuario = @r;

-- Resumen
SELECT u.id_usuario, u.nombre, COUNT(p.id_producto) AS productos, r.abierto
FROM usuario u
JOIN restaurante r ON r.id_usuario = u.id_usuario
LEFT JOIN producto p ON p.restaurante_id = u.id_usuario
WHERE u.rol = 'Restaurante' AND u.email LIKE 'seed-%@trego.test'
GROUP BY u.id_usuario, u.nombre, r.abierto
ORDER BY u.id_usuario;
