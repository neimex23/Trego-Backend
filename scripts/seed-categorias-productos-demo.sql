-- Asigna subcategorías a productos demo para filtros del menú (Bebida, Postre, Ensalada, Principal).
-- Ejecutar: mysql -u root -p1234 < scripts/seed-categorias-productos-demo.sql
USE tregodb;

INSERT INTO sub_categoria (nombre, categoria, url_imagen)
SELECT 'Platos principales', 'Principal', NULL
WHERE NOT EXISTS (SELECT 1 FROM sub_categoria WHERE categoria = 'Principal' LIMIT 1);

INSERT INTO sub_categoria (nombre, categoria, url_imagen)
SELECT 'Bebidas', 'Bebida', NULL
WHERE NOT EXISTS (SELECT 1 FROM sub_categoria WHERE categoria = 'Bebida' LIMIT 1);

INSERT INTO sub_categoria (nombre, categoria, url_imagen)
SELECT 'Postres', 'Postre', NULL
WHERE NOT EXISTS (SELECT 1 FROM sub_categoria WHERE categoria = 'Postre' LIMIT 1);

INSERT INTO sub_categoria (nombre, categoria, url_imagen)
SELECT 'Ensaladas', 'Ensalada', NULL
WHERE NOT EXISTS (SELECT 1 FROM sub_categoria WHERE categoria = 'Ensalada' LIMIT 1);

SET @sub_principal = (SELECT id_sub_categoria FROM sub_categoria WHERE categoria = 'Principal' ORDER BY id_sub_categoria LIMIT 1);
SET @sub_bebida = (SELECT id_sub_categoria FROM sub_categoria WHERE categoria = 'Bebida' ORDER BY id_sub_categoria LIMIT 1);
SET @sub_postre = (SELECT id_sub_categoria FROM sub_categoria WHERE categoria = 'Postre' ORDER BY id_sub_categoria LIMIT 1);
SET @sub_ensalada = (SELECT id_sub_categoria FROM sub_categoria WHERE categoria = 'Ensalada' ORDER BY id_sub_categoria LIMIT 1);

-- Principal (aparece en "Plato Principal")
UPDATE producto SET subcategoria_id = @sub_principal
WHERE nombre IN (
  'Hamburguesa clásica', 'Pizza muzzarella', 'Pizza napolitana',
  'Roll California', 'Sashimi salmón', 'Papas fritas', 'Fainá'
) AND @sub_principal IS NOT NULL;

-- Bebida
UPDATE producto SET subcategoria_id = @sub_bebida
WHERE nombre LIKE 'Coca-Cola%' AND @sub_bebida IS NOT NULL;

-- Ensalada
UPDATE producto SET subcategoria_id = @sub_ensalada
WHERE nombre IN ('Arroz primavera', 'Ensalada César', 'Ensalada mixta', 'Wakame')
  AND @sub_ensalada IS NOT NULL;

-- Postre (productos existentes por nombre)
UPDATE producto SET subcategoria_id = @sub_postre
WHERE nombre IN ('Flan casero', 'Brownie con helado', 'Mochi de té verde')
  AND @sub_postre IS NOT NULL;

-- Postres demo por restaurante (si no existen)
SET @r = 3;
INSERT INTO producto (nombre, precio, descripcion, url_imagen, disponible, oferta_activa, restaurante_id, subcategoria_id)
SELECT 'Flan casero', 180, 'Flan de vainilla con dulce de leche', NULL, 1, 0, @r, @sub_postre
WHERE @sub_postre IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM producto WHERE restaurante_id = @r AND nombre = 'Flan casero');
SET @p = LAST_INSERT_ID();
INSERT INTO plato (id_producto, tiempo_preparacion_minutos)
SELECT @p, 5 FROM DUAL WHERE @p > 0;

SET @r = 4;
INSERT INTO producto (nombre, precio, descripcion, url_imagen, disponible, oferta_activa, restaurante_id, subcategoria_id)
SELECT 'Brownie con helado', 220, 'Brownie tibio con bocha de vainilla', NULL, 1, 0, @r, @sub_postre
WHERE @sub_postre IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM producto WHERE restaurante_id = @r AND nombre = 'Brownie con helado');
SET @p = LAST_INSERT_ID();
INSERT INTO plato (id_producto, tiempo_preparacion_minutos)
SELECT @p, 8 FROM DUAL WHERE @p > 0;

SET @r = 5;
INSERT INTO producto (nombre, precio, descripcion, url_imagen, disponible, oferta_activa, restaurante_id, subcategoria_id)
SELECT 'Mochi de té verde', 150, '2 unidades de mochi', NULL, 1, 0, @r, @sub_postre
WHERE @sub_postre IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM producto WHERE restaurante_id = @r AND nombre = 'Mochi de té verde');
SET @p = LAST_INSERT_ID();
INSERT INTO plato (id_producto, tiempo_preparacion_minutos)
SELECT @p, 3 FROM DUAL WHERE @p > 0;

-- Cualquier producto sin categoría → Principal por defecto
UPDATE producto SET subcategoria_id = @sub_principal
WHERE subcategoria_id IS NULL AND @sub_principal IS NOT NULL;

SELECT p.id_producto, p.nombre, p.restaurante_id, sc.categoria AS categoria_menu
FROM producto p
LEFT JOIN sub_categoria sc ON sc.id_sub_categoria = p.subcategoria_id
ORDER BY p.restaurante_id, sc.categoria, p.nombre;
