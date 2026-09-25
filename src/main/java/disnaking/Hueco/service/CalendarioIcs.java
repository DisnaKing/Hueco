package disnaking.Hueco.service;

import disnaking.Hueco.model.Cita;
import disnaking.Hueco.model.Negocio;
import disnaking.Hueco.model.Servicio;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

// Evento iCalendar (RFC 5545) de una cita, para "Añadir a mi calendario". Las horas van en UTC,
// calculadas con la zona horaria del comercio: así cada calendario la muestra en la hora local correcta.
public final class CalendarioIcs {

    private static final DateTimeFormatter UTC = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
            .withZone(ZoneOffset.UTC);
    private static final String FIN = "\r\n";

    private CalendarioIcs() {
    }

    public static String evento(Cita cita, Negocio negocio, String nombreComercio, Clock clock) {
        Instant inicio = cita.getFecha().atTime(cita.getHora()).atZone(clock.getZone()).toInstant();
        Instant fin = inicio.plusSeconds(cita.getDuracionMinutos() * 60L);
        String titulo = nombreComercio == null || nombreComercio.isBlank() ? "Cita" : "Cita en " + nombreComercio.trim();
        String servicios = cita.getServicios().stream().map(Servicio::getNombre).collect(Collectors.joining(", "));
        String descripcion = servicios + (negocio.getTelefono() != null ? "\nPara cambiarla o cancelarla: " + negocio.getTelefono() : "");

        StringBuilder ics = new StringBuilder();
        linea(ics, "BEGIN:VCALENDAR");
        linea(ics, "VERSION:2.0");
        linea(ics, "PRODID:-//Hueco//Reservas//ES");
        linea(ics, "CALSCALE:GREGORIAN");
        linea(ics, "METHOD:PUBLISH");
        linea(ics, "BEGIN:VEVENT");
        linea(ics, "UID:" + cita.getToken() + "@hueco");
        linea(ics, "DTSTAMP:" + UTC.format(clock.instant()));
        linea(ics, "DTSTART:" + UTC.format(inicio));
        linea(ics, "DTEND:" + UTC.format(fin));
        linea(ics, "SUMMARY:" + escapar(titulo));
        if (negocio.getDireccion() != null) linea(ics, "LOCATION:" + escapar(negocio.getDireccion()));
        linea(ics, "DESCRIPTION:" + escapar(descripcion));
        linea(ics, "END:VEVENT");
        linea(ics, "END:VCALENDAR");
        return ics.toString();
    }

    // Texto de una propiedad: \ , ; y saltos de línea van escapados
    static String escapar(String texto) {
        return texto.replace("\\", "\\\\").replace(";", "\\;").replace(",", "\\,").replace("\n", "\\n");
    }

    // Las líneas de más de 75 bytes se parten y siguen con un espacio al principio (sin partir caracteres UTF-8)
    private static void linea(StringBuilder ics, String contenido) {
        int bytes = 0;
        for (int i = 0; i < contenido.length(); ) {
            int cp = contenido.codePointAt(i);
            int largo = new String(Character.toChars(cp)).getBytes(StandardCharsets.UTF_8).length;
            if (bytes + largo > 75) {
                ics.append(FIN).append(' ');
                bytes = 1;
            }
            ics.appendCodePoint(cp);
            bytes += largo;
            i += Character.charCount(cp);
        }
        ics.append(FIN);
    }
}
