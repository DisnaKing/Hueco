-- Peluquería de ejemplo para desarrollo (solo perfil dev).
-- Migración repetible: Flyway la vuelve a ejecutar si cambia el fichero, pero solo carga
-- datos con la base vacía. Para recargarla (y refrescar las fechas): docker compose down -v
DO $$
DECLARE
    -- Lunes de dentro de dos semanas: las citas caen en martes, miércoles y jueves, lejos de las vacaciones
    lunes date := CURRENT_DATE + (15 - EXTRACT(ISODOW FROM CURRENT_DATE)::int);
BEGIN
IF EXISTS (SELECT 1 FROM negocio) THEN
    RETURN;
END IF;

INSERT INTO servicio (id, nombre, descripcion, duracion_minutos, precio, categoria, orden, activo) VALUES
(1, 'Corte mujer', 'Lavado, corte y secado a tu estilo.', 45, 25.00, 'Corte', 2, TRUE),
(2, 'Corte hombre', 'Corte a tijera o máquina con lavado incluido.', 30, 15.00, 'Corte', 1, TRUE),
(3, 'Corte infantil', 'Para menores de 12 años.', 20, 10.00, 'Corte', 3, TRUE),
(4, 'Tinte raíz', 'Retoque de color en la raíz con productos sin amoniaco.', 60, 32.00, 'Color', 4, TRUE),
(5, 'Mechas', 'Mechas de papel o balayage, con matizado.', 120, 65.00, 'Color', 5, TRUE),
(6, 'Peinado de fiesta', 'Recogido o ondas para eventos.', 45, 30.00, 'Color', 6, FALSE),
(7, 'Hidratación profunda', 'Mascarilla y masaje capilar para cabellos secos.', 30, 18.00, 'Tratamientos', 7, TRUE),
(8, 'Alisado de keratina', 'Reduce el encrespado durante semanas.', 90, 80.00, 'Tratamientos', 8, TRUE);

INSERT INTO negocio (id, eslogan, sobre_nosotros, direccion, telefono, email) VALUES
(1,
 'Tu pelo, en buenas manos',
 'Somos una peluquería de barrio con más de quince años de oficio. Trabajamos sin prisas, con productos de calidad y escuchando lo que quieres antes de coger las tijeras.',
 'Calle Mayor 12, 28013 Madrid',
 '+34 600 000 000',
 'hola@peluqueriaejemplo.es');

-- Lunes a viernes, horario partido; sábado solo de mañana; domingo cerrado
INSERT INTO negocio_horario (negocio_id, dia_semana, apertura, cierre) VALUES
(1, 'MONDAY', '09:00', '13:30'), (1, 'MONDAY', '16:00', '20:00'),
(1, 'TUESDAY', '09:00', '13:30'), (1, 'TUESDAY', '16:00', '20:00'),
(1, 'WEDNESDAY', '09:00', '13:30'), (1, 'WEDNESDAY', '16:00', '20:00'),
(1, 'THURSDAY', '09:00', '13:30'), (1, 'THURSDAY', '16:00', '20:00'),
(1, 'FRIDAY', '09:00', '13:30'), (1, 'FRIDAY', '16:00', '20:00'),
(1, 'SATURDAY', '09:00', '14:00');

INSERT INTO negocio_red_social (negocio_id, tipo, url) VALUES
(1, 'INSTAGRAM', 'https://www.instagram.com/peluqueriaejemplo'),
(1, 'WHATSAPP', 'https://wa.me/34600000000');

INSERT INTO negocio_testimonio (negocio_id, autor, texto, orden) VALUES
(1, 'Lucía M.', 'Salí encantada con el corte. Me explicaron todo y el resultado fue justo lo que pedía.', 1),
(1, 'Javier R.', 'Rápidos, puntuales y muy buen trato. Ya no voy a otro sitio.', 2),
(1, 'Marta G.', 'Las mechas me duraron muchísimo. Repetiré seguro.', 3);

-- Vacaciones de 2 días la semana que viene, relativas a hoy
INSERT INTO negocio_cierre (negocio_id, desde, hasta, motivo) VALUES
(1, CURRENT_DATE + 7, CURRENT_DATE + 8, 'Vacaciones');

-- Clientes de ejemplo para ver nombres y teléfonos en la agenda
INSERT INTO cliente (cliente_id, name, telefono, email, creado_en) VALUES
(1, 'Lucía Martín', '+34611000001', 'lucia@example.com', CURRENT_TIMESTAMP),
(2, 'Javier Ruiz', '+34622000002', NULL, CURRENT_TIMESTAMP);

-- Citas para ver horas ocupadas en el calendario.
-- Martes casi lleno: solo quedan huecos cortos hacia las 12:15.
-- Miércoles: la mañana entera ocupada.
-- Jueves: completo, para ver el día tachado en el calendario.
INSERT INTO cita (id, fecha, hora, estado, cliente_id, duracion_minutos, precio_total, notas) VALUES
(1, lunes + 1, '09:00', 'CONFIRMADA', 1, 120, 65.00, NULL),
(2, lunes + 1, '11:15', 'CONFIRMADA', 2, 45, 25.00, 'Prefiere máquina del 2'),
(3, lunes + 1, '16:00', 'PENDIENTE', 1, 60, 32.00, NULL),
(4, lunes + 1, '17:15', 'CONFIRMADA', NULL, 90, 80.00, NULL),
(5, lunes + 1, '19:00', 'CONFIRMADA', 2, 30, 15.00, 'Prefiere máquina del 2'),
(6, lunes + 2, '09:00', 'CONFIRMADA', 1, 120, 65.00, NULL),
(7, lunes + 2, '11:15', 'PENDIENTE', NULL, 90, 80.00, NULL),
(8, lunes + 3, '09:00', 'CONFIRMADA', 2, 265, 150.00, 'Prefiere máquina del 2'),
(9, lunes + 3, '16:00', 'CONFIRMADA', NULL, 235, 130.00, NULL);

INSERT INTO cita_servicio (cita_id, servicio_id) VALUES
(1, 5), (2, 1), (3, 4), (4, 8), (5, 2), (6, 5), (7, 8), (8, 5), (8, 8), (9, 5), (9, 4);

-- Lo que se cree desde la web o la API empieza después de los ids del ejemplo
ALTER TABLE servicio ALTER COLUMN id RESTART WITH 101;
ALTER TABLE cita ALTER COLUMN id RESTART WITH 101;
ALTER TABLE cliente ALTER COLUMN cliente_id RESTART WITH 101;
END
$$;
