package disnaking.Hueco.service;

import disnaking.Hueco.config.EmailProperties;
import disnaking.Hueco.model.Cita;
import disnaking.Hueco.model.Cliente;
import disnaking.Hueco.model.Negocio;
import disnaking.Hueco.model.Servicio;

import java.text.NumberFormat;
import java.time.Clock;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

// Texto de los emails de una cita nueva: la confirmación al cliente y el aviso al comercio
public final class CorreosReserva {

    private static final Locale ES = Locale.of("es", "ES");
    private static final DateTimeFormatter DIA = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", ES);
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("H:mm");

    // ics: contenido del adjunto cita.ics, o null si no lleva
    public record Correo(String para, String responderA, String asunto, String texto, String ics) {
    }

    private CorreosReserva() {
    }

    // Vacío si el cliente no dejó email
    public static Optional<Correo> paraCliente(Cita cita, Negocio negocio, EmailProperties email, Clock clock) {
        Cliente cliente = cita.getCliente();
        if (cliente == null || vacio(cliente.getEmail())) return Optional.empty();

        StringBuilder texto = new StringBuilder()
                .append("Hola, ").append(cliente.getName()).append(":\n\n")
                .append("Tu cita").append(enComercio(email)).append(" está confirmada.\n\n")
                .append(detalle(cita));
        if (!vacio(negocio.getDireccion())) texto.append("Dónde: ").append(negocio.getDireccion()).append('\n');
        enlace(email, "/reservar/confirmada/" + cita.getToken())
                .ifPresent(url -> texto.append("\nConsulta tu cita cuando quieras:\n").append(url).append('\n'));
        texto.append("\nTe adjuntamos la cita para que la añadas a tu calendario.\n");
        if (!vacio(negocio.getTelefono())) {
            texto.append("Para cambiarla o cancelarla, llama al ").append(negocio.getTelefono()).append(".\n");
        }

        String asunto = "Cita confirmada" + enComercio(email) + ": " + cuando(cita);
        return Optional.of(new Correo(cliente.getEmail(), vacioANull(negocio.getEmail()), asunto, texto.toString(),
                CalendarioIcs.evento(cita, negocio, email.nombreComercio(), clock)));
    }

    // Vacío si no hay a quién avisar
    public static Optional<Correo> paraComercio(Cita cita, Negocio negocio, EmailProperties email) {
        String para = !vacio(email.avisoComercio()) ? email.avisoComercio() : negocio.getEmail();
        if (vacio(para)) return Optional.empty();

        Cliente cliente = cita.getCliente();
        StringBuilder texto = new StringBuilder("Nueva cita desde la web.\n\n").append(detalle(cita));
        String responderA = null;
        if (cliente != null) {
            texto.append("Cliente: ").append(cliente.getName()).append('\n')
                    .append("Teléfono: ").append(cliente.getTelefono()).append('\n');
            if (!vacio(cliente.getEmail())) {
                texto.append("Email: ").append(cliente.getEmail()).append('\n');
                responderA = cliente.getEmail();
            }
        }
        if (!vacio(cita.getNotas())) texto.append("Notas: ").append(cita.getNotas()).append('\n');
        enlace(email, "/agenda").ifPresent(url -> texto.append("\nAgenda: ").append(url).append('\n'));

        String nombre = cliente != null ? cliente.getName() + ", " : "";
        return Optional.of(new Correo(para, responderA, "Nueva cita: " + nombre + cuando(cita), texto.toString(), null));
    }

    // "martes 29 de septiembre a las 10:00"
    static String cuando(Cita cita) {
        return DIA.format(cita.getFecha()) + " a las " + HORA.format(cita.getHora());
    }

    private static String detalle(Cita cita) {
        String servicios = cita.getServicios().stream().map(Servicio::getNombre).collect(Collectors.joining(", "));
        return "Cuándo: " + cuando(cita) + "\n"
                + "Servicios: " + servicios + "\n"
                + "Duración: " + duracion(cita.getDuracionMinutos()) + "\n"
                + "Total: " + NumberFormat.getCurrencyInstance(ES).format(cita.getPrecioTotal()) + "\n";
    }

    // 75 → "1 h 15 min", como en la web
    static String duracion(int minutos) {
        int horas = minutos / 60;
        int resto = minutos % 60;
        if (horas == 0) return resto + " min";
        if (resto == 0) return horas + " h";
        return horas + " h " + resto + " min";
    }

    private static Optional<String> enlace(EmailProperties email, String ruta) {
        if (vacio(email.urlWeb())) return Optional.empty();
        return Optional.of(email.urlWeb().replaceAll("/+$", "") + ruta);
    }

    private static String enComercio(EmailProperties email) {
        return vacio(email.nombreComercio()) ? "" : " en " + email.nombreComercio().trim();
    }

    private static boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }

    private static String vacioANull(String texto) {
        return vacio(texto) ? null : texto;
    }
}
