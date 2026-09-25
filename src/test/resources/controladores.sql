-- Datos mínimos para ControladoresTest
INSERT INTO servicio (id, nombre, descripcion, duracion_minutos, precio, categoria, orden, activo) VALUES
(1, 'Corte', NULL, 30, 15.00, NULL, 1, TRUE),
(2, 'Tinte', NULL, 60, 32.00, NULL, 2, TRUE);

INSERT INTO cliente (cliente_id, name) VALUES (100, 'Ana');

-- La 100 es de Ana con dos servicios; la 101 no tiene cliente para poder devolverla sin ciclos
INSERT INTO cita (id, fecha, hora, estado, cliente_id, duracion_minutos, precio_total) VALUES
(100, '2026-10-01', '10:00', 'PENDIENTE', 100, 90, 47.00),
(101, '2026-10-02', '11:00', 'PENDIENTE', NULL, 30, 15.00);

INSERT INTO cita_servicio (cita_id, servicio_id) VALUES (100, 1), (100, 2);
