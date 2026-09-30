package disnaking.Hueco.controller;

import disnaking.Hueco.Exception.Reserva.ReservaRechazadaException.Motivo;
import disnaking.Hueco.repository.EstadoSql;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

// Restricciones de la base de datos que tienen respuesta propia, en todos los endpoints (reserva y gestión)
@RestControllerAdvice
public class ErroresBaseDeDatos {

    // La cita se solapa con otra (restricción ex_cita_solape)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> integridad(DataIntegrityViolationException e) {
        if (!EstadoSql.esSolape(e)) throw e;
        return ResponseEntity.status(Motivo.HORA_OCUPADA.getStatus()).body(Map.of("motivo", Motivo.HORA_OCUPADA.name()));
    }
}
