package disnaking.Hueco.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

// Reglas de reserva de cada instalación (hueco.* en application.properties)
@ConfigurationProperties(prefix = "hueco")
public record ReservaProperties(
        // Citas que se pueden atender a la vez
        int capacidad,
        // Cada cuántos minutos se ofrece una hora de inicio
        int pasoMinutos,
        // Tiempo de preparación después de cada cita
        int margenMinutos,
        // Hasta cuántos días vista se puede reservar, contando hoy
        int diasVista,
        // Antelación mínima para reservar
        int antelacionMinutos
) {
}
