package disnaking.Hueco.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

// Reglas de la reserva pública como invitado (hueco.* en application.properties)
@ConfigurationProperties(prefix = "hueco")
public record ReservaPublicaProperties(
        // Se antepone al teléfono normalizado: +34 → +34600000000
        String prefijoTelefono,
        // Citas vivas (de hoy en adelante) que puede tener a la vez un mismo teléfono
        int maxCitasPorTelefono,
        // Reservas por hora desde una misma IP, contadas en memoria
        int maxReservasPorIpHora
) {
}
