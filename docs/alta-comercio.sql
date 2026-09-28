-- Alta de un comercio en producción. No es una migración: se ejecuta una sola vez, a mano, después del
-- primer arranque (cuando Flyway ya ha creado las tablas). Por ejemplo:
--   psql -h <host> -U hueco -d hueco -f alta-comercio.sql
-- Cambia los valores de ejemplo por los del comercio. Va en una transacción: si algo falla, no queda nada a medias.

BEGIN;

-- El negocio es siempre la fila id = 1. El nombre del comercio no va aquí: se fija en frontend/src/business.config.js
INSERT INTO negocio (id, eslogan, sobre_nosotros, direccion, telefono, email) VALUES
(1,
 'Eslogan del comercio',
 'Texto de la sección "Sobre nosotros".',
 'Calle, número, código postal y ciudad',
 '+34 600 000 000',
 'hola@comercio.es');

-- Horario semanal. Un día sin tramos está cerrado; dos tramos el mismo día son horario partido.
-- dia_semana: MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
INSERT INTO negocio_horario (negocio_id, dia_semana, apertura, cierre) VALUES
(1, 'TUESDAY',   '09:00', '14:00'), (1, 'TUESDAY',   '16:00', '20:00'),
(1, 'WEDNESDAY', '09:00', '14:00'), (1, 'WEDNESDAY', '16:00', '20:00'),
(1, 'THURSDAY',  '09:00', '14:00'), (1, 'THURSDAY',  '16:00', '20:00'),
(1, 'FRIDAY',    '09:00', '14:00'), (1, 'FRIDAY',    '16:00', '20:00'),
(1, 'SATURDAY',  '09:00', '14:00');

-- Servicios. Solo los activos salen en la web; orden fija el orden de la carta; categoria es opcional.
INSERT INTO servicio (nombre, descripcion, duracion_minutos, precio, categoria, orden, activo) VALUES
('Corte', 'Lavado, corte y secado.', 30, 15.00, NULL, 1, TRUE);

-- Opcional: redes sociales (INSTAGRAM, FACEBOOK, TIKTOK, WHATSAPP, X, YOUTUBE), testimonios y cierres puntuales.
-- INSERT INTO negocio_red_social (negocio_id, tipo, url) VALUES (1, 'INSTAGRAM', 'https://www.instagram.com/...');
-- INSERT INTO negocio_testimonio (negocio_id, autor, texto, orden) VALUES (1, 'Nombre', 'Texto', 1);
-- INSERT INTO negocio_cierre (negocio_id, desde, hasta, motivo) VALUES (1, '2026-12-24', '2026-12-26', 'Navidad');

COMMIT;
