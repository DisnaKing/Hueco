package disnaking.Hueco.controller;

import disnaking.Hueco.Exception.Reserva.ReservaRechazadaException.Motivo;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.Map;

// Restricciones de la base de datos que tienen respuesta propia, en todos los endpoints (reserva y gestión)
@RestControllerAdvice
public class ErroresBaseDeDatos {

    // exclusion_violation: la cita se solapa con otra (restricción ex_cita_solape, V6)
    private static final String SOLAPE = "23P01";

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> integridad(DataIntegrityViolationException e) {
        if (!SOLAPE.equals(sqlState(e))) throw e;
        return ResponseEntity.status(Motivo.HORA_OCUPADA.getStatus()).body(Map.of("motivo", Motivo.HORA_OCUPADA.name()));
    }

    private static String sqlState(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof SQLException sql && sql.getSQLState() != null) return sql.getSQLState();
        }
        return null;
    }
}
