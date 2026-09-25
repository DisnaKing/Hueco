-- Citas para AgendaEndpointTest, encima de huecos.sql (todos los días de 9:00 a 20:00; mañana, cierre "Festivo")
INSERT INTO cliente (cliente_id, name, telefono, email) VALUES
(100, 'Ana', '+34600111222', 'ana@example.com');

-- Pasado mañana, desordenadas a propósito; la de las 9:30 está cancelada y la de las 17:00 no tiene cliente
INSERT INTO cita (id, fecha, hora, estado, cliente_id, duracion_minutos, precio_total, notas, token) VALUES
(100, DATEADD('DAY', 2, CURRENT_DATE), '12:00', 'CONFIRMADA', 100, 60, 32.00, 'Pelo largo', 'token-100'),
(101, DATEADD('DAY', 2, CURRENT_DATE), '09:30', 'CANCELADA', 100, 30, 15.00, NULL, 'token-101'),
(102, DATEADD('DAY', 2, CURRENT_DATE), '17:00', 'CONFIRMADA', NULL, 30, 15.00, NULL, 'token-102');

INSERT INTO cita_servicio (cita_id, servicio_id) VALUES (100, 1), (101, 1), (102, 1);
