package disnaking.Hueco.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

// Emails de cada reserva (hueco.email.*). El servidor SMTP va en spring.mail.*; sin él o sin remitente
// no se manda nada y la reserva funciona igual.
@ConfigurationProperties(prefix = "hueco.email")
public record EmailProperties(
        // Remitente verificado en el proveedor, por ejemplo "Peluquería Ejemplo <citas@peluqueria.es>"
        String remitente,
        // Nombre del comercio en el asunto, el texto y el .ics
        String nombreComercio,
        // Dirección pública de la web, para los enlaces: https://peluqueria.es
        String urlWeb,
        // Quién recibe el aviso de cada cita nueva; si falta, el email del negocio
        String avisoComercio,
        // Intentos por email antes de darlo por perdido (y dejarlo en el log)
        @DefaultValue("3") int intentos,
        // Espera entre intentos; se duplica en cada uno
        @DefaultValue("30s") Duration pausaReintento
) {

    public boolean configurado() {
        return remitente != null && !remitente.isBlank();
    }
}
