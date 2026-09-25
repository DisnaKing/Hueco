package disnaking.Hueco.service;

import disnaking.Hueco.DTO.Hueco.diaHuecosDTO;
import disnaking.Hueco.config.ReservaProperties;
import disnaking.Hueco.model.CierrePuntual;
import disnaking.Hueco.model.EstadoCita;
import disnaking.Hueco.model.EstadoDia;
import disnaking.Hueco.model.TramoHorario;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CalculadoraHuecosTest {

    private static final ZoneId MADRID = ZoneId.of("Europe/Madrid");
    // 20/09/2026 es domingo (cerrado); el lunes 21 es el primer día que se trabaja
    private static final LocalDate DOMINGO = LocalDate.of(2026, 9, 20);
    private static final LocalDate LUNES = DOMINGO.plusDays(1);

    // capacidad 1, paso 15, margen 5, 30 días vista, 2 h de antelación
    private static final ReservaProperties REGLAS = new ReservaProperties(1, 15, 5, 30, 120);

    // Lunes a viernes 9:00-13:30 y 16:00-20:00; sábado 9:00-14:00; domingo cerrado
    private static List<TramoHorario> horario() {
        List<TramoHorario> horario = new ArrayList<>();
        for (DayOfWeek dia : List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)) {
            horario.add(new TramoHorario(dia, LocalTime.of(9, 0), LocalTime.of(13, 30)));
            horario.add(new TramoHorario(dia, LocalTime.of(16, 0), LocalTime.of(20, 0)));
        }
        horario.add(new TramoHorario(DayOfWeek.SATURDAY, LocalTime.of(9, 0), LocalTime.of(14, 0)));
        return horario;
    }

    private static CalculadoraHuecos calculadora(LocalDateTime ahora, ReservaProperties reglas) {
        return new CalculadoraHuecos(Clock.fixed(ahora.atZone(MADRID).toInstant(), MADRID), reglas);
    }

    // Por defecto, "ahora" es el domingo a mediodía: la antelación no afecta al lunes
    private static List<diaHuecosDTO> dias(int duracion, List<Ocupacion> ocupaciones) {
        return calculadora(DOMINGO.atTime(12, 0), REGLAS).dias(horario(), List.of(), ocupaciones, duracion);
    }

    private static diaHuecosDTO dia(List<diaHuecosDTO> dias, LocalDate fecha) {
        return dias.stream().filter(d -> d.getFecha().equals(fecha)).findFirst().orElseThrow();
    }

    private static Ocupacion cita(LocalDate fecha, int hora, int minuto, int duracion, EstadoCita estado) {
        return new Ocupacion(fecha, LocalTime.of(hora, minuto), duracion, estado);
    }

    @Test
    void elServicioNoCabeAntesDeLaPausaDelMediodia() {
        // 60 min + 5 de margen: a las 12:30 acabaría a las 13:35, después del cierre de las 13:30
        List<String> horas = dia(dias(60, List.of()), LUNES).getHoras();

        assertThat(horas).contains("12:15").doesNotContain("12:30", "12:45", "13:00");
        assertThat(horas).contains("16:00");
    }

    @Test
    void dejaElMargenDespuesDeCadaCita() {
        // Cita de 10:00 a 10:30 más 5 de margen: ocupa hasta las 10:35
        List<String> horas = dia(dias(30, List.of(cita(LUNES, 10, 0, 30, EstadoCita.CONFIRMADA))), LUNES).getHoras();

        assertThat(horas).contains("09:15");
        // 9:30 + 30 + 5 = 10:05 pisaría la cita de las 10:00
        assertThat(horas).doesNotContain("09:30", "10:00", "10:30");
        assertThat(horas).contains("10:45");
    }

    @Test
    void conCapacidad1UnaCitaOcupaLaHora() {
        List<String> horas = dia(dias(30, List.of(cita(LUNES, 10, 0, 30, EstadoCita.PENDIENTE))), LUNES).getHoras();

        assertThat(horas).doesNotContain("10:00");
    }

    @Test
    void conCapacidad2CabenDosCitasALaVezPeroNoTres() {
        ReservaProperties dosSillas = new ReservaProperties(2, 15, 5, 30, 120);
        CalculadoraHuecos calc = calculadora(DOMINGO.atTime(12, 0), dosSillas);

        List<String> conUna = dia(calc.dias(horario(), List.of(),
                List.of(cita(LUNES, 10, 0, 30, EstadoCita.CONFIRMADA)), 30), LUNES).getHoras();
        List<String> conDos = dia(calc.dias(horario(), List.of(),
                List.of(cita(LUNES, 10, 0, 30, EstadoCita.CONFIRMADA),
                        cita(LUNES, 10, 15, 30, EstadoCita.CONFIRMADA)), 30), LUNES).getHoras();

        assertThat(conUna).contains("10:00");
        // De 10:15 a 10:35 coinciden las dos: no cabe una tercera que empiece a las 10:00
        assertThat(conDos).doesNotContain("10:00", "10:15");
        assertThat(conDos).contains("09:30");
    }

    @Test
    void respetaLaAntelacionMinimaHoy() {
        // Lunes a las 10:00: nada antes de las 12:00
        List<diaHuecosDTO> dias = calculadora(LUNES.atTime(10, 0), REGLAS)
                .dias(horario(), List.of(), List.of(), 30);
        diaHuecosDTO hoy = dias.getFirst();

        assertThat(hoy.getFecha()).isEqualTo(LUNES);
        assertThat(hoy.getHoras()).doesNotContain("11:45").contains("12:00");
    }

    @Test
    void hoyCuandoLaAntelacionNoDejaNingunaHoraEstaCompleto() {
        // Lunes a las 18:30: ya no cabe nada hasta el cierre de las 20:00
        diaHuecosDTO hoy = calculadora(LUNES.atTime(18, 30), REGLAS)
                .dias(horario(), List.of(), List.of(), 30).getFirst();

        assertThat(hoy.getEstado()).isEqualTo(EstadoDia.COMPLETO);
        assertThat(hoy.getHoras()).isEmpty();
    }

    @Test
    void ofreceDiasVistaDiasDesdeHoy() {
        List<diaHuecosDTO> dias = dias(30, List.of());

        assertThat(dias).hasSize(30);
        assertThat(dias.getFirst().getFecha()).isEqualTo(DOMINGO);
        assertThat(dias.getLast().getFecha()).isEqualTo(DOMINGO.plusDays(29));
        assertThat(dias.getFirst().getEstado()).isEqualTo(EstadoDia.CERRADO);
    }

    @Test
    void unDiaDeCierrePuntualEstaCerrado() {
        List<diaHuecosDTO> dias = calculadora(DOMINGO.atTime(12, 0), REGLAS)
                .dias(horario(), List.of(new CierrePuntual(LUNES, LUNES, "Festivo")), List.of(), 30);

        assertThat(dia(dias, LUNES).getEstado()).isEqualTo(EstadoDia.CERRADO);
        assertThat(dia(dias, LUNES).getHoras()).isEmpty();
        assertThat(dia(dias, LUNES.plusDays(1)).getEstado()).isEqualTo(EstadoDia.LIBRE);
    }

    @Test
    void lasCitasCanceladasNoBloquean() {
        List<Ocupacion> ocupaciones = List.of(
                cita(LUNES, 10, 0, 30, EstadoCita.CANCELADA),
                cita(LUNES, 11, 0, 30, EstadoCita.NO_SHOW));

        assertThat(dia(dias(30, ocupaciones), LUNES).getHoras()).contains("10:00", "11:00");
    }

    @Test
    void conPasoDe15UnServicioDe20EmpiezaEnLaRejilla() {
        List<String> horas = dia(dias(20, List.of()), LUNES).getHoras();

        assertThat(horas).contains("09:00", "09:15", "09:30").doesNotContain("09:20");
        // 13:00 + 20 + 5 = 13:25 cabe; 13:15 + 25 = 13:40 no
        assertThat(horas).contains("13:00").doesNotContain("13:15");
    }

    @Test
    void unDiaSinHuecosEstaCompleto() {
        // Una cita que ocupa toda la mañana y otra toda la tarde
        List<Ocupacion> ocupaciones = List.of(
                cita(LUNES, 9, 0, 265, EstadoCita.CONFIRMADA),
                cita(LUNES, 16, 0, 235, EstadoCita.CONFIRMADA));

        diaHuecosDTO lunes = dia(dias(30, ocupaciones), LUNES);

        assertThat(lunes.getEstado()).isEqualTo(EstadoDia.COMPLETO);
        assertThat(lunes.getHoras()).isEmpty();
    }

    @Test
    void libreSirveParaComprobarUnaHoraConcreta() {
        CalculadoraHuecos calc = calculadora(DOMINGO.atTime(12, 0), REGLAS);
        List<Ocupacion> ocupaciones = List.of(cita(LUNES, 10, 0, 30, EstadoCita.CONFIRMADA));

        assertThat(calc.libre(horario(), List.of(), ocupaciones, 30, LUNES, LocalTime.of(9, 0))).isTrue();
        assertThat(calc.libre(horario(), List.of(), ocupaciones, 30, LUNES, LocalTime.of(10, 0))).isFalse();
    }
}
