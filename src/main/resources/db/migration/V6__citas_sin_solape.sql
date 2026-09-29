-- Última defensa contra dos citas a la vez: ninguna cita que ocupa hueco (PENDIENTE o CONFIRMADA)
-- puede solaparse con otra en [fecha + hora, + duración). El margen entre citas lo sigue poniendo
-- CalculadoraHuecos en Java. Si se viola, PostgreSQL devuelve 23P01 y la API responde 409 HORA_OCUPADA.
-- Con solo el rango en la restricción basta el operador && de GiST; btree_gist haría falta para
-- combinarlo con columnas normales (por ejemplo, si un día hubiera varios profesionales).
ALTER TABLE cita
    ADD CONSTRAINT ex_cita_solape EXCLUDE USING gist (
        tsrange(fecha + hora, fecha + hora + make_interval(mins => duracion_minutos)) WITH &&
    ) WHERE (estado IN ('PENDIENTE', 'CONFIRMADA'));
