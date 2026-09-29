-- Columnas sin las que una fila no tiene sentido. Cliente.creado_en y el cliente de una cita
-- se quedan opcionales: la gestión puede crear clientes y citas sin ellos.
ALTER TABLE negocio_horario
    ALTER COLUMN dia_semana SET NOT NULL,
    ALTER COLUMN apertura SET NOT NULL,
    ALTER COLUMN cierre SET NOT NULL;

ALTER TABLE negocio_cierre
    ALTER COLUMN desde SET NOT NULL,
    ALTER COLUMN hasta SET NOT NULL;

ALTER TABLE negocio_red_social
    ALTER COLUMN tipo SET NOT NULL,
    ALTER COLUMN url SET NOT NULL;

ALTER TABLE negocio_testimonio
    ALTER COLUMN texto SET NOT NULL;

ALTER TABLE servicio
    ALTER COLUMN nombre SET NOT NULL,
    ALTER COLUMN precio SET NOT NULL;

ALTER TABLE cliente
    ALTER COLUMN name SET NOT NULL,
    ALTER COLUMN telefono SET NOT NULL;

-- Las citas de ejemplo de desarrollo se cargaban sin token
UPDATE cita SET token = gen_random_uuid()::text WHERE token IS NULL;

ALTER TABLE cita
    ALTER COLUMN fecha SET NOT NULL,
    ALTER COLUMN hora SET NOT NULL,
    ALTER COLUMN estado SET NOT NULL,
    ALTER COLUMN token SET NOT NULL;
