package disnaking.Hueco.service;

import disnaking.Hueco.DTO.Hueco.diaHuecosDTO;
import disnaking.Hueco.config.ReservaProperties;
import disnaking.Hueco.model.CierrePuntual;
import disnaking.Hueco.model.EstadoDia;
import disnaking.Hueco.model.TramoHorario;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// Horas libres para una cita de cierta duración. Sin acceso a base de datos, para poder
// probarlo con reloj fijo y reutilizarlo al crear la cita (comprobar que la hora sigue libre).
@Component
public class CalculadoraHuecos {

    private final Clock clock;
    private final ReservaProperties reglas;

    public CalculadoraHuecos(Clock clock, ReservaProperties reglas) {
        this.clock = clock;
        this.reglas = reglas;
    }

    // Un elemento por día, desde hoy y durante diasVista días
    public List<diaHuecosDTO> dias(List<TramoHorario> horario, List<CierrePuntual> cierres,
                                   List<Ocupacion> ocupaciones, int duracionMinutos) {
        LocalDateTime ahora = LocalDateTime.now(clock);
        LocalDateTime primeraHora = ahora.plusMinutes(reglas.antelacionMinutos());
        LocalDate hoy = ahora.toLocalDate();

        List<diaHuecosDTO> dias = new ArrayList<>();
        for (int i = 0; i < reglas.diasVista(); i++) {
            LocalDate fecha = hoy.plusDays(i);
            List<TramoHorario> tramos = tramosDe(horario, fecha.getDayOfWeek());
            if (tramos.isEmpty() || cierres.stream().anyMatch(c -> c.cubre(fecha))) {
                dias.add(new diaHuecosDTO(fecha, EstadoDia.CERRADO, List.of()));
                continue;
            }

            List<int[]> ocupadas = intervalosOcupados(ocupaciones, fecha);
            List<String> horas = new ArrayList<>();
            for (TramoHorario tramo : tramos) {
                int cierre = minutos(tramo.getCierre());
                // La cita y su margen tienen que caber enteros en el tramo
                for (int inicio = minutos(tramo.getApertura());
                     inicio + duracionMinutos + reglas.margenMinutos() <= cierre;
                     inicio += reglas.pasoMinutos()) {
                    LocalTime hora = LocalTime.of(inicio / 60, inicio % 60);
                    if (fecha.atTime(hora).isBefore(primeraHora)) continue;
                    if (cabe(inicio, inicio + duracionMinutos + reglas.margenMinutos(), ocupadas)) {
                        horas.add(hora.toString());
                    }
                }
            }
            dias.add(new diaHuecosDTO(fecha, horas.isEmpty() ? EstadoDia.COMPLETO : EstadoDia.LIBRE, horas));
        }
        return dias;
    }

    // Para el paso 3: la hora elegida sigue libre si el mismo cálculo la ofrece
    public boolean libre(List<TramoHorario> horario, List<CierrePuntual> cierres, List<Ocupacion> ocupaciones,
                         int duracionMinutos, LocalDate fecha, LocalTime hora) {
        String buscada = hora.withSecond(0).withNano(0).toString();
        return dias(horario, cierres, ocupaciones, duracionMinutos).stream()
                .anyMatch(d -> d.getFecha().equals(fecha) && d.getHoras().contains(buscada));
    }

    // [inicio, fin) en minutos del día de las citas que bloquean, con su margen
    private List<int[]> intervalosOcupados(List<Ocupacion> ocupaciones, LocalDate fecha) {
        return ocupaciones.stream()
                .filter(o -> o.fecha().equals(fecha) && o.estado().bloqueaHueco())
                .map(o -> {
                    int inicio = minutos(o.hora());
                    return new int[]{inicio, inicio + o.duracionMinutos() + reglas.margenMinutos()};
                })
                .toList();
    }

    // Cabe si en ningún momento de [inicio, fin) se llega a la capacidad. Basta con mirar
    // el inicio de la cita nueva y el de cada cita que empieza dentro de ella.
    private boolean cabe(int inicio, int fin, List<int[]> ocupadas) {
        List<Integer> momentos = new ArrayList<>();
        momentos.add(inicio);
        for (int[] o : ocupadas) {
            if (o[0] > inicio && o[0] < fin) momentos.add(o[0]);
        }
        for (int momento : momentos) {
            long simultaneas = ocupadas.stream().filter(o -> o[0] <= momento && momento < o[1]).count();
            if (simultaneas >= reglas.capacidad()) return false;
        }
        return true;
    }

    private static List<TramoHorario> tramosDe(List<TramoHorario> horario, DayOfWeek dia) {
        return horario.stream()
                .filter(t -> t.getDiaSemana() == dia)
                .sorted((a, b) -> a.getApertura().compareTo(b.getApertura()))
                .toList();
    }

    private static int minutos(LocalTime hora) {
        return hora.getHour() * 60 + hora.getMinute();
    }
}
