package disnaking.Hueco.service;

import disnaking.Hueco.DTO.Negocio.*;
import disnaking.Hueco.model.CierrePuntual;
import disnaking.Hueco.model.EstadoApertura;
import disnaking.Hueco.model.Negocio;
import disnaking.Hueco.model.Testimonio;
import disnaking.Hueco.model.TramoHorario;
import disnaking.Hueco.repository.NegocioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class NegocioService {

    // El comercio es una sola fila
    private static final long NEGOCIO_ID = 1L;
    private static final int MAX_DIAS_BUSQUEDA = 366;

    private final NegocioRepository negocioRepository;
    private final Clock clock;
    private final int diasVista;

    public NegocioService(NegocioRepository negocioRepository, Clock clock,
                          @Value("${hueco.dias-vista}") int diasVista) {
        this.negocioRepository = negocioRepository;
        this.clock = clock;
        this.diasVista = diasVista;
    }

    @Transactional(readOnly = true)
    public Optional<negocioDTO> obtener() {
        return negocioRepository.findById(NEGOCIO_ID).map(this::toDTO);
    }

    public DayOfWeek hoy() {
        return LocalDate.now(clock).getDayOfWeek();
    }

    public estadoHoyDTO estadoHoy(List<TramoHorario> horario, List<CierrePuntual> cierres) {
        LocalDateTime ahora = LocalDateTime.now(clock);
        LocalDate hoy = ahora.toLocalDate();
        LocalTime hora = ahora.toLocalTime();

        // Un festivo o unas vacaciones anulan los tramos de hoy
        if (!cerrado(cierres, hoy)) {
            for (TramoHorario tramo : tramosDe(horario, hoy.getDayOfWeek())) {
                if (!hora.isBefore(tramo.getApertura()) && hora.isBefore(tramo.getCierre())) {
                    return new estadoHoyDTO(EstadoApertura.ABIERTO, tramo.getCierre(), hoy);
                }
                if (hora.isBefore(tramo.getApertura())) {
                    return new estadoHoyDTO(EstadoApertura.ABRE_HOY, tramo.getApertura(), hoy);
                }
            }
        }

        // Próximo día con algún tramo y sin cierre; un año basta para cualquier cierre razonable
        for (int i = 1; i <= MAX_DIAS_BUSQUEDA; i++) {
            LocalDate fecha = hoy.plusDays(i);
            if (cerrado(cierres, fecha)) continue;
            List<TramoHorario> tramos = tramosDe(horario, fecha.getDayOfWeek());
            if (!tramos.isEmpty()) {
                return new estadoHoyDTO(EstadoApertura.CERRADO_HOY, tramos.getFirst().getApertura(), fecha);
            }
        }
        // Sin ningún tramo, o cerrado todo el año
        return new estadoHoyDTO(EstadoApertura.CERRADO_HOY, null, null);
    }

    private negocioDTO toDTO(Negocio negocio) {
        List<diaHorarioDTO> horario = Arrays.stream(DayOfWeek.values())
                .map(dia -> new diaHorarioDTO(dia, tramosDe(negocio.getHorario(), dia).stream()
                        .map(tramo -> new tramoHorarioDTO(tramo.getApertura(), tramo.getCierre()))
                        .toList()))
                .toList();

        return new negocioDTO(
                negocio.getEslogan(),
                negocio.getSobreNosotros(),
                negocio.getDireccion(),
                negocio.getTelefono(),
                negocio.getEmail(),
                horario,
                negocio.getRedesSociales().stream()
                        .map(red -> new redSocialDTO(red.getTipo(), red.getUrl()))
                        .toList(),
                negocio.getTestimonios().stream()
                        .sorted(Comparator.comparingInt(Testimonio::getOrden))
                        .map(testimonio -> new testimonioDTO(testimonio.getAutor(), testimonio.getTexto()))
                        .toList(),
                estadoHoy(negocio.getHorario(), negocio.getCierres()),
                hoy(),
                cierresProximos(negocio.getCierres())
        );
    }

    // Los que tocan el plazo de reserva (de hoy a hoy + diasVista), ordenados
    private List<cierreDTO> cierresProximos(List<CierrePuntual> cierres) {
        LocalDate hoy = LocalDate.now(clock);
        LocalDate fin = hoy.plusDays(diasVista);
        return cierres.stream()
                .filter(c -> !c.getHasta().isBefore(hoy) && !c.getDesde().isAfter(fin))
                .sorted(Comparator.comparing(CierrePuntual::getDesde))
                .map(c -> new cierreDTO(c.getDesde(), c.getHasta(), c.getMotivo()))
                .toList();
    }

    private static boolean cerrado(List<CierrePuntual> cierres, LocalDate fecha) {
        return cierres.stream().anyMatch(c -> c.cubre(fecha));
    }

    private static List<TramoHorario> tramosDe(List<TramoHorario> horario, DayOfWeek dia) {
        return horario.stream()
                .filter(tramo -> tramo.getDiaSemana() == dia)
                .sorted(Comparator.comparing(TramoHorario::getApertura))
                .toList();
    }
}
