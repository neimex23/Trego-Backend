-- Ingredientes de prueba para platos demo (restaurantes 3, 4, 5)
-- Ejecutar: Get-Content scripts/seed-ingredientes-platos-demo.sql | mysql -u root -p1234
USE tregodb;

-- ========== Burger Montevideo (restaurante 3) ==========
INSERT INTO ingrediente (nombre, restaurante_id)
SELECT 'Queso cheddar', 3 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre = 'Queso cheddar' AND restaurante_id = 3);
INSERT INTO ingrediente (nombre, restaurante_id)
SELECT 'Cebolla', 3 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre = 'Cebolla' AND restaurante_id = 3);
INSERT INTO ingrediente (nombre, restaurante_id)
SELECT 'Tomate', 3 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre = 'Tomate' AND restaurante_id = 3);
INSERT INTO ingrediente (nombre, restaurante_id)
SELECT 'Lechuga', 3 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre = 'Lechuga' AND restaurante_id = 3);
INSERT INTO ingrediente (nombre, restaurante_id)
SELECT 'Bacon', 3 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre = 'Bacon' AND restaurante_id = 3);
INSERT INTO ingrediente (nombre, restaurante_id)
SELECT 'Huevo', 3 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre = 'Huevo' AND restaurante_id = 3);
INSERT INTO ingrediente (nombre, restaurante_id)
SELECT 'Ketchup', 3 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre = 'Ketchup' AND restaurante_id = 3);
INSERT INTO ingrediente (nombre, restaurante_id)
SELECT 'Mayonesa', 3 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre = 'Mayonesa' AND restaurante_id = 3);

-- Hamburguesa (producto 1)
INSERT IGNORE INTO plato_ingredientes (plato_id_producto, ingredientes_id_ingrediente)
SELECT 1, i.id_ingrediente FROM ingrediente i
WHERE i.restaurante_id = 3 AND i.nombre IN ('Queso cheddar', 'Cebolla', 'Tomate', 'Lechuga', 'Bacon', 'Huevo');
-- Papas fritas (producto 2)
INSERT IGNORE INTO plato_ingredientes (plato_id_producto, ingredientes_id_ingrediente)
SELECT 2, i.id_ingrediente FROM ingrediente i
WHERE i.restaurante_id = 3 AND i.nombre IN ('Ketchup', 'Mayonesa');

-- ========== Pizza Centro (restaurante 4) ==========
INSERT INTO ingrediente (nombre, restaurante_id)
SELECT 'Muzzarella', 4 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre = 'Muzzarella' AND restaurante_id = 4);
INSERT INTO ingrediente (nombre, restaurante_id)
SELECT 'Salsa de tomate', 4 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre = 'Salsa de tomate' AND restaurante_id = 4);
INSERT INTO ingrediente (nombre, restaurante_id)
SELECT 'Orégano', 4 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre = 'Orégano' AND restaurante_id = 4);
INSERT INTO ingrediente (nombre, restaurante_id)
SELECT 'Aceitunas', 4 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre = 'Aceitunas' AND restaurante_id = 4);
INSERT INTO ingrediente (nombre, restaurante_id)
SELECT 'Jamón', 4 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre = 'Jamón' AND restaurante_id = 4);
INSERT INTO ingrediente (nombre, restaurante_id)
SELECT 'Albahaca', 4 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre = 'Albahaca' AND restaurante_id = 4);

INSERT IGNORE INTO plato_ingredientes (plato_id_producto, ingredientes_id_ingrediente)
SELECT 4, i.id_ingrediente FROM ingrediente i
WHERE i.restaurante_id = 4 AND i.nombre IN ('Muzzarella', 'Salsa de tomate', 'Orégano');
INSERT IGNORE INTO plato_ingredientes (plato_id_producto, ingredientes_id_ingrediente)
SELECT 5, i.id_ingrediente FROM ingrediente i
WHERE i.restaurante_id = 4 AND i.nombre IN ('Muzzarella', 'Salsa de tomate', 'Jamón', 'Albahaca');
INSERT IGNORE INTO plato_ingredientes (plato_id_producto, ingredientes_id_ingrediente)
SELECT 6, i.id_ingrediente FROM ingrediente i
WHERE i.restaurante_id = 4 AND i.nombre IN ('Muzzarella', 'Orégano');

-- ========== Sushi Pocitos (restaurante 5) ==========
INSERT INTO ingrediente (nombre, restaurante_id)
SELECT 'Palta', 5 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre = 'Palta' AND restaurante_id = 5);
INSERT INTO ingrediente (nombre, restaurante_id)
SELECT 'Kanikama', 5 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre = 'Kanikama' AND restaurante_id = 5);
INSERT INTO ingrediente (nombre, restaurante_id)
SELECT 'Pepino', 5 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre = 'Pepino' AND restaurante_id = 5);
INSERT INTO ingrediente (nombre, restaurante_id)
SELECT 'Sésamo', 5 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre = 'Sésamo' AND restaurante_id = 5);
INSERT INTO ingrediente (nombre, restaurante_id)
SELECT 'Salmón', 5 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre = 'Salmón' AND restaurante_id = 5);
INSERT INTO ingrediente (nombre, restaurante_id)
SELECT 'Arroz', 5 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre = 'Arroz' AND restaurante_id = 5);
INSERT INTO ingrediente (nombre, restaurante_id)
SELECT 'Zanahoria', 5 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM ingrediente WHERE nombre = 'Zanahoria' AND restaurante_id = 5);

INSERT IGNORE INTO plato_ingredientes (plato_id_producto, ingredientes_id_ingrediente)
SELECT 7, i.id_ingrediente FROM ingrediente i
WHERE i.restaurante_id = 5 AND i.nombre IN ('Palta', 'Kanikama', 'Pepino', 'Sésamo');
INSERT IGNORE INTO plato_ingredientes (plato_id_producto, ingredientes_id_ingrediente)
SELECT 8, i.id_ingrediente FROM ingrediente i
WHERE i.restaurante_id = 5 AND i.nombre IN ('Salmón', 'Sésamo');
INSERT IGNORE INTO plato_ingredientes (plato_id_producto, ingredientes_id_ingrediente)
SELECT 9, i.id_ingrediente FROM ingrediente i
WHERE i.restaurante_id = 5 AND i.nombre IN ('Arroz', 'Zanahoria');

-- Resumen
SELECT p.id_producto, p.nombre AS producto, p.restaurante_id,
       GROUP_CONCAT(i.nombre ORDER BY i.nombre SEPARATOR ', ') AS ingredientes
FROM producto p
JOIN plato pl ON pl.id_producto = p.id_producto
LEFT JOIN plato_ingredientes pi ON pi.plato_id_producto = p.id_producto
LEFT JOIN ingrediente i ON i.id_ingrediente = pi.ingredientes_id_ingrediente
WHERE p.restaurante_id IN (3, 4, 5)
GROUP BY p.id_producto, p.nombre, p.restaurante_id
ORDER BY p.restaurante_id, p.id_producto;
