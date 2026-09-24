-- Datos mínimos para HomeEndpointsTest, independientes del seed de desarrollo
DELETE FROM negocio_testimonio;
DELETE FROM negocio_red_social;
DELETE FROM negocio_horario;
DELETE FROM negocio;
DELETE FROM servicio;

-- El 3 está inactivo; el orden no coincide con el id
INSERT INTO servicio (id, nombre, descripcion, duracion_minutos, precio, categoria, orden, activo) VALUES
(1, 'Servicio B', 'Descripción B', 45, 25.00, 'Cat 1', 2, TRUE),
(2, 'Servicio A', 'Descripción A', 30, 15.00, 'Cat 1', 1, TRUE),
(3, 'Servicio inactivo', 'No debe salir', 20, 10.00, 'Cat 2', 3, FALSE),
(4, 'Servicio C', NULL, 60, 32.50, NULL, 4, TRUE);

INSERT INTO negocio (id, eslogan, sobre_nosotros, direccion, telefono, email) VALUES
(1, 'Eslogan de prueba', 'Texto de prueba', 'Calle Prueba 1', '+34 600 111 222', 'prueba@example.com');

-- Horario partido de lunes a viernes, sábado solo por la mañana, domingo cerrado
INSERT INTO negocio_horario (negocio_id, dia_semana, apertura, cierre) VALUES
(1, 'MONDAY', '16:00', '20:00'), (1, 'MONDAY', '09:00', '13:30'),
(1, 'TUESDAY', '09:00', '13:30'), (1, 'TUESDAY', '16:00', '20:00'),
(1, 'WEDNESDAY', '09:00', '13:30'), (1, 'WEDNESDAY', '16:00', '20:00'),
(1, 'THURSDAY', '09:00', '13:30'), (1, 'THURSDAY', '16:00', '20:00'),
(1, 'FRIDAY', '09:00', '13:30'), (1, 'FRIDAY', '16:00', '20:00'),
(1, 'SATURDAY', '09:00', '14:00');

INSERT INTO negocio_red_social (negocio_id, tipo, url) VALUES
(1, 'INSTAGRAM', 'https://www.instagram.com/prueba');

-- Desordenados a propósito
INSERT INTO negocio_testimonio (negocio_id, autor, texto, orden) VALUES
(1, 'Tercero', 'Texto 3', 3),
(1, 'Primero', 'Texto 1', 1),
(1, 'Segundo', 'Texto 2', 2);
