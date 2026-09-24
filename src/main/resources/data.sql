-- Peluquería de ejemplo para desarrollo

INSERT INTO servicio (id, nombre, descripcion, duracion_minutos, precio, categoria, orden, activo) VALUES
(1, 'Corte mujer', 'Lavado, corte y secado a tu estilo.', 45, 25.00, 'Corte', 2, TRUE),
(2, 'Corte hombre', 'Corte a tijera o máquina con lavado incluido.', 30, 15.00, 'Corte', 1, TRUE),
(3, 'Corte infantil', 'Para menores de 12 años.', 20, 10.00, 'Corte', 3, TRUE),
(4, 'Tinte raíz', 'Retoque de color en la raíz con productos sin amoniaco.', 60, 32.00, 'Color', 4, TRUE),
(5, 'Mechas', 'Mechas de papel o balayage, con matizado.', 120, 65.00, 'Color', 5, TRUE),
(6, 'Peinado de fiesta', 'Recogido o ondas para eventos.', 45, 30.00, 'Color', 6, FALSE),
(7, 'Hidratación profunda', 'Mascarilla y masaje capilar para cabellos secos.', 30, 18.00, 'Tratamientos', 7, TRUE),
(8, 'Alisado de keratina', 'Reduce el encrespado durante semanas.', 90, 80.00, 'Tratamientos', 8, TRUE);

-- Evita que los servicios creados desde la API choquen con los ids del seed
ALTER SEQUENCE servicio_seq RESTART WITH 101;

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
