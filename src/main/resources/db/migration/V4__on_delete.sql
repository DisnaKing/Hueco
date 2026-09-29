-- Qué pasa con las filas relacionadas al borrar:
-- - una cita se lleva sus líneas de cita_servicio;
-- - un servicio o un cliente con citas no se pueden borrar (se desactiva el servicio; el cliente se queda);
-- - el negocio se lleva su horario, cierres, redes y testimonios.
ALTER TABLE cita_servicio
    DROP CONSTRAINT fk_cita_servicio_cita,
    DROP CONSTRAINT fk_cita_servicio_servicio,
    ADD CONSTRAINT fk_cita_servicio_cita FOREIGN KEY (cita_id) REFERENCES cita (id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_cita_servicio_servicio FOREIGN KEY (servicio_id) REFERENCES servicio (id) ON DELETE RESTRICT;

ALTER TABLE cita
    DROP CONSTRAINT fk_cita_cliente,
    ADD CONSTRAINT fk_cita_cliente FOREIGN KEY (cliente_id) REFERENCES cliente (cliente_id) ON DELETE RESTRICT;

ALTER TABLE negocio_horario
    DROP CONSTRAINT fk_negocio_horario_negocio,
    ADD CONSTRAINT fk_negocio_horario_negocio FOREIGN KEY (negocio_id) REFERENCES negocio (id) ON DELETE CASCADE;

ALTER TABLE negocio_cierre
    DROP CONSTRAINT fk_negocio_cierre_negocio,
    ADD CONSTRAINT fk_negocio_cierre_negocio FOREIGN KEY (negocio_id) REFERENCES negocio (id) ON DELETE CASCADE;

ALTER TABLE negocio_red_social
    DROP CONSTRAINT fk_negocio_red_social_negocio,
    ADD CONSTRAINT fk_negocio_red_social_negocio FOREIGN KEY (negocio_id) REFERENCES negocio (id) ON DELETE CASCADE;

ALTER TABLE negocio_testimonio
    DROP CONSTRAINT fk_negocio_testimonio_negocio,
    ADD CONSTRAINT fk_negocio_testimonio_negocio FOREIGN KEY (negocio_id) REFERENCES negocio (id) ON DELETE CASCADE;

-- Una cita no se repite servicio
ALTER TABLE cita_servicio
    ADD CONSTRAINT pk_cita_servicio PRIMARY KEY (cita_id, servicio_id);
