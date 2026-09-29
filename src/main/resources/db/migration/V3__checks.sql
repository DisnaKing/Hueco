-- Valores que el dominio no admite. El estado ya tiene su CHECK desde V1 (ck_cita_estado).
ALTER TABLE servicio
    ADD CONSTRAINT ck_servicio_duracion_minutos CHECK (duracion_minutos > 0),
    ADD CONSTRAINT ck_servicio_precio CHECK (precio >= 0);

ALTER TABLE cita
    ADD CONSTRAINT ck_cita_duracion_minutos CHECK (duracion_minutos > 0),
    ADD CONSTRAINT ck_cita_precio_total CHECK (precio_total >= 0);

ALTER TABLE negocio_horario
    ADD CONSTRAINT ck_negocio_horario_apertura_cierre CHECK (apertura < cierre);

ALTER TABLE negocio_cierre
    ADD CONSTRAINT ck_negocio_cierre_desde_hasta CHECK (desde <= hasta);
