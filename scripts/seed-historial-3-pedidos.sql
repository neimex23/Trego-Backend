-- 3 compras de prueba para historial del cliente (ajustar @cliente_id si hace falta)
USE tregodb;

SET @cliente_id = (SELECT id_usuario FROM cliente ORDER BY id_usuario DESC LIMIT 1);

-- Restaurantes demo (si no existen)
INSERT INTO usuario (nombre, email, rol)
SELECT 'Burger Montevideo', 'seed-burger@trego.test', 'Restaurante'
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'seed-burger@trego.test');

SET @r1 = (SELECT id_usuario FROM usuario WHERE email = 'seed-burger@trego.test');

INSERT INTO restaurante (id_usuario, calificacion_prom, categoria, descripcion, habilitado, radio_entrega, calle, numero, latitud, longitud)
SELECT @r1, 4.5, 'ComidaRapida', 'Hamburguesas y papas', 1, 10, 'Av. 18 de Julio', 1200, -34.9011, -56.1645
WHERE @r1 IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM restaurante WHERE id_usuario = @r1);

INSERT INTO usuario (nombre, email, rol)
SELECT 'Pizza Centro', 'seed-pizza@trego.test', 'Restaurante'
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'seed-pizza@trego.test');

SET @r2 = (SELECT id_usuario FROM usuario WHERE email = 'seed-pizza@trego.test');

INSERT INTO restaurante (id_usuario, calificacion_prom, categoria, descripcion, habilitado, radio_entrega, calle, numero, latitud, longitud)
SELECT @r2, 4.8, 'Pizza', 'Pizzas a la piedra', 1, 12, 'Ejido', 1450, -34.9050, -56.1910
WHERE @r2 IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM restaurante WHERE id_usuario = @r2);

INSERT INTO usuario (nombre, email, rol)
SELECT 'Sushi Pocitos', 'seed-sushi@trego.test', 'Restaurante'
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'seed-sushi@trego.test');

SET @r3 = (SELECT id_usuario FROM usuario WHERE email = 'seed-sushi@trego.test');

INSERT INTO restaurante (id_usuario, calificacion_prom, categoria, descripcion, habilitado, radio_entrega, calle, numero, latitud, longitud)
SELECT @r3, 4.2, 'Otros', 'Sushi y rolls', 1, 8, 'Bvar. Artigas', 800, -34.9090, -56.1550
WHERE @r3 IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM restaurante WHERE id_usuario = @r3);

-- Pedidos de prueba (estados visibles en /Historial)
INSERT INTO pedido (estado, fecha_creacion, fecha_expiracion, total, cliente_id, restaurante_id, calle, numero, latitud, longitud)
VALUES
  ('Entregado', DATE_SUB(NOW(), INTERVAL 12 DAY), NULL, 890.00, @cliente_id, @r1, 'Av. Brasil', 2500, -34.9100, -56.1700),
  ('Pagado', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 19 HOUR), 1250.50, @cliente_id, @r2, 'Av. Brasil', 2500, -34.9100, -56.1700),
  ('EnPreparacion', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 20 HOUR), 640.00, @cliente_id, @r3, 'Av. Brasil', 2500, -34.9100, -56.1700);

SELECT 'Cliente usado' AS info, @cliente_id AS cliente_id;
SELECT p.id_pedido, p.estado, p.total, p.fecha_creacion, u.nombre AS restaurante
FROM pedido p
JOIN restaurante r ON r.id_usuario = p.restaurante_id
JOIN usuario u ON u.id_usuario = r.id_usuario
WHERE p.cliente_id = @cliente_id
ORDER BY p.fecha_creacion DESC;
