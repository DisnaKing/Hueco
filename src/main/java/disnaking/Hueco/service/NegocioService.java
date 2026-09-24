package disnaking.Hueco.service;

import disnaking.Hueco.DTO.Negocio.*;
import disnaking.Hueco.model.EstadoApertura;
import disnaking.Hueco.model.Negocio;
import disnaking.Hueco.model.Testimonio;
import disnaking.Hueco.model.TramoHorario;
import disnaking.Hueco.repository.NegocioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.DayOfWeek;
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

    private final NegocioRepository negocioRepository;
    private final Clock clock;

    public NegocioService(NegocioRepository negocioRepository, Clock clock) {
        this.negocioRepository = negocioRepository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public Optional<negocioDTO> obtener() {
        return negocioRepository.findById(NEGOCIO_ID).map(this::toDTO);
    }

    public estadoHoyDTO estadoHoy(List<TramoHorario> horario) {
        LocalDateTime ahora = LocalDateTime.now(clock);
        DayOfWeek hoy = ahora.getDayOfWeek();
        LocalTime hora = ahora.toLocalTime();

        for (TramoHorario tramo : tramosDe(horario, hoy)) {
            if (!hora.isBefore(tramo.getApertura()) && hora.isBefore(tramo.getCierre())) {
                return new estadoHoyDTO(EstadoApertura.ABIERTO, tramo.getCierre(), hoy);
            }
            if (hora.isBefore(tramo.getApertura())) {
                return new estadoHoyDTO(EstadoApertura.ABRE_HOY, tramo.getApertura(), hoy);
            }
        }

        // Busca el próximo día con algún tramo; como mucho, el mismo día de la semana que viene
        for (int i = 1; i <= 7; i++) {
            DayOfWeek dia = hoy.plus(i);
            List<TramoHorario> tramos = tramosDe(horario, dia);
            if (!tramos.isEmpty()) {
                return new estadoHoyDTO(EstadoApertura.CERRADO_HOY, tramos.getFirst().getApertura(), dia);
            }
        }
        // Sin ningún tramo en toda la semana
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
                estadoHoy(negocio.getHorario())
        );
    }

    private static List<TramoHorario> tramosDe(List<TramoHorario> horario, DayOfWeek dia) {
        return horario.stream()
                .filter(tramo -> tramo.getDiaSemana() == dia)
                .sorted(Comparator.comparing(TramoHorario::getApertura))
                .toList();
    }
}
