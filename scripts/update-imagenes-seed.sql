-- ============================================================================
-- Trego - Imagenes para los datos de prueba (seed)
-- ----------------------------------------------------------------------------
-- Setea imagenes reales en los registros cargados por seed-demo-completo.sql:
--   * usuario.foto_perfil   -> logo de cada restaurante y avatar de cada cliente
--   * restaurante.foto_portada -> foto de portada del local
--   * producto.url_imagen   -> foto de cada plato/articulo/combo
--   * oferta.url_imagen     -> banner de cada oferta
--   * sub_categoria.url_imagen -> icono/foto de cada categoria del menu
--
-- Solo toca filas del seed: restaurantes/clientes con email '%@trego.seed',
-- y los productos de esos restaurantes. Las subcategorias y ofertas se
-- matchean por su texto exacto (creado por el seed).
--
-- Es idempotente: son UPDATE, podes correrlo las veces que quieras.
-- Requiere que seed-demo-completo.sql ya se haya ejecutado.
--
-- IMAGENES:
--   Se usan URLs directas del CDN de Unsplash (images.unsplash.com), que son
--   estables y de uso libre, y avatares de i.pravatar.cc para los clientes.
--   NOTA: no se pudieron verificar contra la red desde el entorno de armado;
--   si alguna no cargara, se reemplaza la URL en el SET correspondiente.
--
-- Ejecutar (local):
--   Get-Content scripts/update-imagenes-seed.sql | mysql -u root -p1234
-- ============================================================================

USE tregodb;

-- ============================================================================
-- 1. RESTAURANTES: logo (usuario.foto_perfil) + portada (restaurante.foto_portada)
-- ============================================================================
UPDATE usuario u
SET u.foto_perfil = CASE u.email
      WHEN 'parrilla@trego.seed' THEN 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=600&q=80&auto=format&fit=crop'
      WHEN 'napoli@trego.seed'   THEN 'https://images.unsplash.com/photo-1513104890138-7c749659a591?w=600&q=80&auto=format&fit=crop'
      WHEN 'burger@trego.seed'   THEN 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=600&q=80&auto=format&fit=crop'
      WHEN 'vegano@trego.seed'   THEN 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=600&q=80&auto=format&fit=crop'
      WHEN 'helados@trego.seed'  THEN 'https://images.unsplash.com/photo-1501443762994-82bd5dba89dc?w=600&q=80&auto=format&fit=crop'
      ELSE u.foto_perfil
    END
WHERE u.email IN ('parrilla@trego.seed','napoli@trego.seed','burger@trego.seed',
                  'vegano@trego.seed','helados@trego.seed');

UPDATE restaurante r
JOIN usuario u ON u.id_usuario = r.id_usuario
SET r.foto_portada = CASE u.email
      WHEN 'parrilla@trego.seed' THEN 'https://images.unsplash.com/photo-1544025162-d76694265947?w=1200&q=80&auto=format&fit=crop'
      WHEN 'napoli@trego.seed'   THEN 'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=1200&q=80&auto=format&fit=crop'
      WHEN 'burger@trego.seed'   THEN 'https://images.unsplash.com/photo-1550547660-d9450f859349?w=1200&q=80&auto=format&fit=crop'
      WHEN 'vegano@trego.seed'   THEN 'https://images.unsplash.com/photo-1490645935967-10de6ba17061?w=1200&q=80&auto=format&fit=crop'
      WHEN 'helados@trego.seed'  THEN 'https://images.unsplash.com/photo-1488477181946-6428a0291777?w=1200&q=80&auto=format&fit=crop'
      ELSE r.foto_portada
    END
WHERE u.email IN ('parrilla@trego.seed','napoli@trego.seed','burger@trego.seed',
                  'vegano@trego.seed','helados@trego.seed');

-- ============================================================================
-- 2. CLIENTES: avatar (usuario.foto_perfil)  -- via i.pravatar.cc
-- ============================================================================
UPDATE usuario u
SET u.foto_perfil = CASE u.email
      WHEN 'lucia@trego.seed'  THEN 'https://i.pravatar.cc/300?img=45'
      WHEN 'martin@trego.seed' THEN 'https://i.pravatar.cc/300?img=12'
      WHEN 'sofia@trego.seed'  THEN 'https://i.pravatar.cc/300?img=47'
      WHEN 'diego@trego.seed'  THEN 'https://i.pravatar.cc/300?img=15'
      ELSE u.foto_perfil
    END
WHERE u.email IN ('lucia@trego.seed','martin@trego.seed','sofia@trego.seed','diego@trego.seed');

-- ============================================================================
-- 3. PRODUCTOS: url_imagen por nombre (acotado a restaurantes del seed)
-- ============================================================================
UPDATE producto p
JOIN usuario u ON u.id_usuario = p.restaurante_id
SET p.url_imagen = CASE p.nombre
      -- Parrilla
      WHEN 'Parrillada para dos'      THEN 'https://images.unsplash.com/photo-1544025162-d76694265947?w=900&q=80&auto=format&fit=crop'
      WHEN 'Provolone a la parrilla'  THEN 'https://images.unsplash.com/photo-1513104890138-7c749659a591?w=900&q=80&auto=format&fit=crop'
      WHEN 'Agua mineral 600ml'       THEN 'https://images.unsplash.com/photo-1560023907-5f339617ea30?w=900&q=80&auto=format&fit=crop'
      -- Napoli
      WHEN 'Pizza muzzarella'         THEN 'https://images.unsplash.com/photo-1513104890138-7c749659a591?w=900&q=80&auto=format&fit=crop'
      WHEN 'Pizza napolitana'         THEN 'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=900&q=80&auto=format&fit=crop'
      WHEN 'Faina'                    THEN 'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=900&q=80&auto=format&fit=crop'
      WHEN 'Coca-Cola 1.5L'           THEN 'https://images.unsplash.com/photo-1554866585-cd94860890b7?w=900&q=80&auto=format&fit=crop'
      -- Burger
      WHEN 'Hamburguesa clasica'      THEN 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=900&q=80&auto=format&fit=crop'
      WHEN 'Hamburguesa bacon'        THEN 'https://images.unsplash.com/photo-1550547660-d9450f859349?w=900&q=80&auto=format&fit=crop'
      WHEN 'Papas rusticas'           THEN 'https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=900&q=80&auto=format&fit=crop'
      WHEN 'Sprite 500ml'             THEN 'https://images.unsplash.com/photo-1554866585-cd94860890b7?w=900&q=80&auto=format&fit=crop'
      WHEN 'Combo clasico'            THEN 'https://images.unsplash.com/photo-1550547660-d9450f859349?w=900&q=80&auto=format&fit=crop'
      -- Vegano
      WHEN 'Bowl de quinoa'           THEN 'https://images.unsplash.com/photo-1490645935967-10de6ba17061?w=900&q=80&auto=format&fit=crop'
      WHEN 'Ensalada cesar vegana'    THEN 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=900&q=80&auto=format&fit=crop'
      WHEN 'Jugo natural de naranja'  THEN 'https://images.unsplash.com/photo-1600271886742-f049cd451bba?w=900&q=80&auto=format&fit=crop'
      -- Helados
      WHEN 'Cuarto de helado'         THEN 'https://images.unsplash.com/photo-1501443762994-82bd5dba89dc?w=900&q=80&auto=format&fit=crop'
      WHEN 'Copa Crema especial'      THEN 'https://images.unsplash.com/photo-1488477181946-6428a0291777?w=900&q=80&auto=format&fit=crop'
      WHEN 'Pote 1 litro'             THEN 'https://images.unsplash.com/photo-1501443762994-82bd5dba89dc?w=900&q=80&auto=format&fit=crop'
      ELSE p.url_imagen
    END
WHERE u.email LIKE '%@trego.seed';

-- ============================================================================
-- 4. SUBCATEGORIAS: url_imagen por nombre (catalogo creado por el seed)
-- ============================================================================
UPDATE sub_categoria s
SET s.url_imagen = CASE s.nombre
      WHEN 'Platos principales' THEN 'https://images.unsplash.com/photo-1544025162-d76694265947?w=600&q=80&auto=format&fit=crop'
      WHEN 'Entradas'           THEN 'https://images.unsplash.com/photo-1513104890138-7c749659a591?w=600&q=80&auto=format&fit=crop'
      WHEN 'Guarniciones'       THEN 'https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=600&q=80&auto=format&fit=crop'
      WHEN 'Ensaladas'          THEN 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=600&q=80&auto=format&fit=crop'
      WHEN 'Bebidas'            THEN 'https://images.unsplash.com/photo-1554866585-cd94860890b7?w=600&q=80&auto=format&fit=crop'
      WHEN 'Postres'            THEN 'https://images.unsplash.com/photo-1501443762994-82bd5dba89dc?w=600&q=80&auto=format&fit=crop'
      ELSE s.url_imagen
    END
WHERE s.nombre IN ('Platos principales','Entradas','Guarniciones','Ensaladas','Bebidas','Postres');

-- ============================================================================
-- 5. OFERTAS: url_imagen por descripcion (textos creados por el seed)
-- ============================================================================
UPDATE oferta o
SET o.url_imagen = CASE o.descripcion
      WHEN '2x1 en hamburguesas - solo esta semana' THEN 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=1000&q=80&auto=format&fit=crop'
      WHEN 'Pizza grande 20% off'                    THEN 'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=1000&q=80&auto=format&fit=crop'
      WHEN 'Postres a mitad de precio'               THEN 'https://images.unsplash.com/photo-1488477181946-6428a0291777?w=1000&q=80&auto=format&fit=crop'
      ELSE o.url_imagen
    END
WHERE o.descripcion IN ('2x1 en hamburguesas - solo esta semana','Pizza grande 20% off','Postres a mitad de precio');

-- ============================================================================
-- 6. Verificacion
-- ============================================================================
SELECT 'Restaurantes (logo+portada)' AS bloque, u.email, u.foto_perfil, r.foto_portada
FROM restaurante r JOIN usuario u ON u.id_usuario = r.id_usuario
WHERE u.email LIKE '%@trego.seed' ORDER BY u.email;

SELECT 'Clientes (avatar)' AS bloque, u.email, u.foto_perfil
FROM usuario u WHERE u.email IN ('lucia@trego.seed','martin@trego.seed','sofia@trego.seed','diego@trego.seed');

SELECT 'Productos' AS bloque, p.nombre, p.url_imagen
FROM producto p JOIN usuario u ON u.id_usuario = p.restaurante_id
WHERE u.email LIKE '%@trego.seed' ORDER BY p.nombre;

SELECT 'Subcategorias' AS bloque, s.nombre, s.url_imagen FROM sub_categoria s
WHERE s.nombre IN ('Platos principales','Entradas','Guarniciones','Ensaladas','Bebidas','Postres');

SELECT 'Ofertas' AS bloque, o.descripcion, o.url_imagen FROM oferta o
WHERE o.descripcion IN ('2x1 en hamburguesas - solo esta semana','Pizza grande 20% off','Postres a mitad de precio');
