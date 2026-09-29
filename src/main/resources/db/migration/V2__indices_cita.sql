-- La agenda y el cálculo de huecos buscan citas por fecha; el límite por teléfono, por cliente
CREATE INDEX ix_cita_fecha ON cita (fecha);
CREATE INDEX ix_cita_cliente_id ON cita (cliente_id);
