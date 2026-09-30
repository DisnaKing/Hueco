-- El margen entre citas pasa a la restricción de solape: cada cita ocupa [inicio, fin + margen).
-- Con esto la base de datos garantiza la hora entera y la reserva ya no necesita bloquear el negocio
-- (solo bloquea por teléfono, ver ReservaService). margen_minutos es el margen con el que se reservó:
-- las citas anteriores quedan con 0, así que su rango no cambia y la restricción nueva no falla sobre ellas.
-- Los endpoints de gestión no lo rellenan (0): para ellos solo se impide el solape estricto, como antes.
ALTER TABLE cita
    ADD COLUMN margen_minutos integer NOT NULL DEFAULT 0,
    ADD CONSTRAINT ck_cita_margen CHECK (margen_minutos >= 0);

ALTER TABLE cita DROP CONSTRAINT ex_cita_solape;

ALTER TABLE cita
    ADD CONSTRAINT ex_cita_solape EXCLUDE USING gist (
        tsrange(fecha + hora, fecha + hora + make_interval(mins => duracion_minutos + margen_minutos)) WITH &&
    ) WHERE (estado IN ('PENDIENTE', 'CONFIRMADA'));
