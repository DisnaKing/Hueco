package disnaking.Hueco.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

// Pone en fila las reservas de un mismo teléfono: el límite de citas vivas y el alta del cliente son
// "comprobar y luego escribir". Reservas de teléfonos distintos no se esperan; la hora la garantiza ex_cita_solape.
@Component
public class BloqueoTelefono {

    // Espacio de nombres de los bloqueos consultivos de la aplicación
    private static final int RESERVAS_POR_TELEFONO = 1;

    private final JdbcTemplate jdbc;

    public BloqueoTelefono(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Hasta el final de la transacción en curso. Dos teléfonos con el mismo hash solo se esperan entre sí.
    public void bloquear(String telefono) {
        jdbc.query("SELECT pg_advisory_xact_lock(?, hashtext(?))", rs -> {}, RESERVAS_POR_TELEFONO, telefono);
    }
}
