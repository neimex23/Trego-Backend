-- ============================================================================
-- Trego - Apertura automatica de locales (EVENT de MySQL)
-- ----------------------------------------------------------------------------
-- El backend ya cierra los locales solo (TregoSchedulers.cerrarLocalesVencidos,
-- cada minuto: cierra los que tienen cierre_programado <= ahora). Lo que falta
-- es la APERTURA automatica. Este EVENT la cubre del lado de la base:
--
--   Cada minuto abre (abierto = 1) los restaurantes que:
--     - estan habilitados y con la cuenta habilitada,
--     - estan cerrados,
--     - la hora actual cae dentro de su franja [hora_apertura, hora_cierre)
--       (soporta franjas que cruzan medianoche, ej. 18:00 -> 00:30),
--     - tienen al menos un producto disponible (misma precondicion que abrirLocal).
--
--   Al abrir, fija cierre_programado igual que calcularCierreProgramado() de
--   RestauranteService: hora_cierre de hoy, o de maniana si ya paso. Asi el
--   cierre lo termina disparando tu scheduler Java existente y ambos estados
--   se van alternando solos segun el horario, sin tocar el codigo Java.
--
-- IMPORTANTE - zona horaria:
--   NOW()/CURTIME() devuelven la hora del servidor MySQL. La app calcula en
--   America/Montevideo (UTC-3). Si tu MySQL corre en hora local de Uruguay
--   (lo habitual en desarrollo), no hay que hacer nada. Si corre en UTC, pone
--   @@global.time_zone en '-03:00' o ajusta con CONVERT_TZ (ver nota al final).
--
-- IMPORTANTE - planificador de eventos en Amazon RDS:
--   En RDS MySQL NO tenes el privilegio SUPER, asi que NO podes hacer
--   "SET GLOBAL event_scheduler = ON;" (da el error 1227: Access denied;
--   you need SUPER or SYSTEM_VARIABLES_ADMIN). El planificador se enciende
--   desde el PARAMETER GROUP de la instancia/cluster:
--     1) En la consola RDS -> Parameter groups, edita el parameter group
--        asociado a tu instancia (si usa el default, crea uno custom y
--        asignalo a la instancia; eso requiere reinicio).
--     2) Pone el parametro  event_scheduler = ON  y guarda.
--        (event_scheduler es "dynamic", no necesita reinicio para aplicar,
--         pero asignar un parameter group nuevo a la instancia si lo necesita.)
--     3) Verifica con:  SHOW VARIABLES LIKE 'event_scheduler';  -> debe dar ON.
--   En MySQL local con privilegios si podes encenderlo a mano descomentando
--   la linea "SET GLOBAL event_scheduler = ON;" mas abajo.
--
-- Ejecutar (local):
--   Get-Content scripts/event-apertura-automatica.sql | mysql -u root -p1234
-- ============================================================================

USE tregodb;

-- 1. El planificador de eventos debe estar encendido.
--    En RDS: hacelo desde el Parameter Group (event_scheduler = ON), NO aca.
--    En MySQL local con privilegios SUPER: descomenta la linea siguiente.
-- SET GLOBAL event_scheduler = ON;

-- 2. (Re)crear el evento.
--    Se usa DELIMITER para que cualquier cliente (DBeaver, Workbench, mysql CLI)
--    trate el CREATE EVENT ... DO ... como UNA sola sentencia y no la corte en
--    los ';'. IMPORTANTE: ejecuta el ARCHIVO COMPLETO (Alt+X en DBeaver =
--    "Execute script"), no sentencia por sentencia.
DROP EVENT IF EXISTS ev_apertura_automatica_locales;

DELIMITER $$

CREATE EVENT ev_apertura_automatica_locales
ON SCHEDULE EVERY 1 MINUTE
COMMENT 'Abre locales dentro de su horario; el cierre lo maneja el scheduler Java'
DO
BEGIN
  UPDATE restaurante r
  SET r.abierto = 1,
      r.cierre_programado = CASE
        WHEN TIMESTAMP(CURDATE(), r.hora_cierre) > NOW()
          THEN TIMESTAMP(CURDATE(), r.hora_cierre)
        ELSE TIMESTAMP(CURDATE() + INTERVAL 1 DAY, r.hora_cierre)
      END
  WHERE r.habilitado = 1
    AND r.cuentahabilitada = 1
    AND r.abierto = 0
    AND r.hora_apertura IS NOT NULL
    AND r.hora_cierre   IS NOT NULL
    -- Solo los locales de prueba cargados a mano (seed): emails '@trego.seed'
    AND r.id_usuario IN (SELECT id_usuario FROM usuario WHERE email LIKE '%@trego.seed')
    AND (
          -- Franja normal (no cruza medianoche): apertura <= ahora < cierre
          (r.hora_apertura <= r.hora_cierre
             AND CURTIME() >= r.hora_apertura
             AND CURTIME() <  r.hora_cierre)
       OR -- Franja nocturna (cruza medianoche): ahora >= apertura  o  ahora < cierre
          (r.hora_apertura >  r.hora_cierre
             AND (CURTIME() >= r.hora_apertura OR CURTIME() < r.hora_cierre))
        )
    AND EXISTS (
          SELECT 1 FROM producto p
          WHERE p.restaurante_id = r.id_usuario AND p.disponible = 1
        );
END $$

DELIMITER ;

-- 3. Verificacion: que el planificador este ON, y estado del evento y locales.
SHOW VARIABLES LIKE 'event_scheduler';   -- debe decir ON (si dice OFF, ver nota RDS arriba)

SHOW EVENTS WHERE Db = 'tregodb' AND Name = 'ev_apertura_automatica_locales';

SELECT r.id_usuario, u.nombre, r.abierto, r.hora_apertura, r.hora_cierre, r.cierre_programado
FROM restaurante r
JOIN usuario u ON u.id_usuario = r.id_usuario
WHERE u.email LIKE '%@trego.seed'
ORDER BY r.id_usuario;

-- ----------------------------------------------------------------------------
-- Notas
-- ----------------------------------------------------------------------------
-- * Para que se "vea" alternar en una demo sin esperar al horario real, podes
--   acortar las franjas de los locales demo, por ejemplo:
--       UPDATE restaurante SET hora_apertura = DATE_FORMAT(NOW(), '%H:%i:00'),
--                              hora_cierre   = DATE_FORMAT(NOW() + INTERVAL 3 MINUTE, '%H:%i:00')
--       WHERE id_usuario IN (SELECT id_usuario FROM usuario WHERE email LIKE '%@trego.seed');
--   El evento los abrira en el proximo minuto y el cierre Java los cerrara a los ~3 min.
--
-- * Si el servidor MySQL esta en UTC y queres forzar hora de Montevideo solo
--   para este evento, reemplaza NOW()/CURTIME()/CURDATE() por su version
--   convertida, p. ej.:
--       SET @ahora := CONVERT_TZ(NOW(), @@session.time_zone, '-03:00');
--   (requiere que NOW() devuelva UTC y usar @ahora en lugar de NOW()).
--
-- * Para desactivar la apertura automatica:
--       ALTER EVENT ev_apertura_automatica_locales DISABLE;   -- pausar
--       DROP EVENT IF EXISTS ev_apertura_automatica_locales;  -- eliminar
-- ============================================================================
