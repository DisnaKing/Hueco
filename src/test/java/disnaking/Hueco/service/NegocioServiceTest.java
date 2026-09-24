package disnaking.Hueco.service;

import disnaking.Hueco.DTO.Negocio.estadoHoyDTO;
import disnaking.Hueco.model.EstadoApertura;
import disnaking.Hueco.model.TramoHorario;
import disnaking.Hueco.repository.NegocioRepository;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class NegocioServiceTest {

    private static final ZoneId MADRID = ZoneId.of("Europe/Madrid");

    // 21/09/2026 es lunes
    private static final LocalDate LUNES = LocalDate.of(2026, 9, 21);
    private static final LocalDate VIERNES = LUNES.plusDays(4);
    private static final LocalDate SABADO = LUNES.plusDays(5);
    private static final LocalDate DOMINGO = LUNES.plusDays(6);

    // Lunes a viernes 9:00-13:30 y 16:00-20:00; sábado 9:00-14:00; domingo cerrado
    private static List<TramoHorario> horario() {
        List<TramoHorario> horario = new ArrayList<>();
        for (DayOfWeek dia : List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)) {
            // La tarde primero para comprobar que se ordenan los tramos
            horario.add(new TramoHorario(dia, LocalTime.of(16, 0), LocalTime.of(20, 0)));
            horario.add(new TramoHorario(dia, LocalTime.of(9, 0), LocalTime.of(13, 30)));
        }
        horario.add(new TramoHorario(DayOfWeek.SATURDAY, LocalTime.of(9, 0), LocalTime.of(14, 0)));
        return horario;
    }

    private static estadoHoyDTO estadoA(LocalDate fecha, int hora, int minuto) {
        Instant instante = fecha.atTime(hora, minuto).atZone(MADRID).toInstant();
        NegocioService service = new NegocioService(mock(NegocioRepository.class), Clock.fixed(instante, MADRID));
        return service.estadoHoy(horario());
    }

    @Test
    void dentroDeUnTramo() {
        estadoHoyDTO estado = estadoA(LUNES, 10, 0);

        assertThat(estado.getEstado()).isEqualTo(EstadoApertura.ABIERTO);
        assertThat(estado.getHora()).isEqualTo(LocalTime.of(13, 30));
        assertThat(estado.getDia()).isEqualTo(DayOfWeek.MONDAY);
    }

    @Test
    void enLaPausaDelMediodia() {
        estadoHoyDTO estado = estadoA(LUNES, 14, 0);

        assertThat(estado.getEstado()).isEqualTo(EstadoApertura.ABRE_HOY);
        assertThat(estado.getHora()).isEqualTo(LocalTime.of(16, 0));
    }

    @Test
    void antesDeAbrir() {
        estadoHoyDTO estado = estadoA(LUNES, 8, 0);

        assertThat(estado.getEstado()).isEqualTo(EstadoApertura.ABRE_HOY);
        assertThat(estado.getHora()).isEqualTo(LocalTime.of(9, 0));
    }

    @Test
    void despuesDelCierre() {
        estadoHoyDTO estado = estadoA(LUNES, 21, 0);

        assertThat(estado.getEstado()).isEqualTo(EstadoApertura.CERRADO_HOY);
        assertThat(estado.getDia()).isEqualTo(DayOfWeek.TUESDAY);
        assertThat(estado.getHora()).isEqualTo(LocalTime.of(9, 0));
    }

    @Test
    void justoALaHoraDeCierreYaEstaCerrado() {
        estadoHoyDTO estado = estadoA(VIERNES, 20, 0);

        assertThat(estado.getEstado()).isEqualTo(EstadoApertura.CERRADO_HOY);
        assertThat(estado.getDia()).isEqualTo(DayOfWeek.SATURDAY);
    }

    @Test
    void enUnDiaCerrado() {
        estadoHoyDTO estado = estadoA(DOMINGO, 12, 0);

        assertThat(estado.getEstado()).isEqualTo(EstadoApertura.CERRADO_HOY);
        assertThat(estado.getDia()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(estado.getHora()).isEqualTo(LocalTime.of(9, 0));
    }

    @Test
    void sabadoPorLaTardeElProximoDiaEsElLunes() {
        estadoHoyDTO estado = estadoA(SABADO, 17, 0);

        assertThat(estado.getEstado()).isEqualTo(EstadoApertura.CERRADO_HOY);
        assertThat(estado.getDia()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(estado.getHora()).isEqualTo(LocalTime.of(9, 0));
    }

    @Test
    void sinHorarioNoHayProximaApertura() {
        Instant instante = LUNES.atTime(10, 0).atZone(MADRID).toInstant();
        NegocioService service = new NegocioService(mock(NegocioRepository.class), Clock.fixed(instante, MADRID));

        estadoHoyDTO estado = service.estadoHoy(List.of());

        assertThat(estado.getEstado()).isEqualTo(EstadoApertura.CERRADO_HOY);
        assertThat(estado.getDia()).isNull();
        assertThat(estado.getHora()).isNull();
    }
}
