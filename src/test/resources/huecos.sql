-- Datos mínimos para HuecosEndpointTest: se trabaja todos los días para que la forma no dependa de hoy
INSERT INTO servicio (id, nombre, descripcion, duracion_minutos, precio, categoria, orden, activo) VALUES
(1, 'Corte', NULL, 30, 15.00, NULL, 1, TRUE),
(2, 'Inactivo', NULL, 30, 15.00, NULL, 2, FALSE);

INSERT INTO negocio (id, eslogan, sobre_nosotros, direccion, telefono, email) VALUES
(1, 'Eslogan', 'Texto', 'Calle', '+34 600 000 000', 'a@b.es');

INSERT INTO negocio_horario (negocio_id, dia_semana, apertura, cierre) VALUES
(1, 'MONDAY', '09:00', '20:00'), (1, 'TUESDAY', '09:00', '20:00'), (1, 'WEDNESDAY', '09:00', '20:00'),
(1, 'THURSDAY', '09:00', '20:00'), (1, 'FRIDAY', '09:00', '20:00'), (1, 'SATURDAY', '09:00', '20:00'),
(1, 'SUNDAY', '09:00', '20:00');

-- Mañana cerrado por un cierre puntual
INSERT INTO negocio_cierre (negocio_id, desde, hasta, motivo) VALUES
(1, DATEADD('DAY', 1, CURRENT_DATE), DATEADD('DAY', 1, CURRENT_DATE), 'Festivo');
