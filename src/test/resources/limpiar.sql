-- Deja la base vacía antes de los datos de cada test: todas las clases con el perfil test
-- comparten contexto y, por tanto, la misma base H2.
DELETE FROM cita_servicio;
DELETE FROM cita;
DELETE FROM cliente;
DELETE FROM servicio;
DELETE FROM negocio_cierre;
DELETE FROM negocio_testimonio;
DELETE FROM negocio_red_social;
DELETE FROM negocio_horario;
DELETE FROM negocio;

-- Lo que se cree por la API empieza lejos de los ids fijos de los scripts
ALTER SEQUENCE servicio_seq RESTART WITH 1000;
ALTER SEQUENCE cita_seq RESTART WITH 1000;
ALTER SEQUENCE cliente_seq RESTART WITH 1000;
