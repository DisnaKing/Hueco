-- Deja la base vacía antes de los datos de cada test: todas las clases con el perfil test
-- comparten el mismo Postgres. Los ids generados vuelven a empezar en 1000, lejos de los fijos
-- de los scripts.
TRUNCATE cita_servicio, cita, cliente, servicio,
         negocio_cierre, negocio_testimonio, negocio_red_social, negocio_horario, negocio
    RESTART IDENTITY CASCADE;
ALTER TABLE servicio ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE cita ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE cliente ALTER COLUMN cliente_id RESTART WITH 1000;
