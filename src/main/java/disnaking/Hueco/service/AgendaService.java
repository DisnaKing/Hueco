package disnaking.Hueco.service;

import disnaking.Hueco.DTO.Agenda.agendaCitaDTO;
import disnaking.Hueco.DTO.Agenda.agendaClienteDTO;
import disnaking.Hueco.DTO.Agenda.agendaDiaDTO;
import disnaking.Hueco.model.*;
import disnaking.Hueco.repository.CitaRepository;
import disnaking.Hueco.repository.NegocioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Agenda del comercio: solo lectura, una semana a partir de un día
@Service
public class AgendaService {

    // El comercio es una sola fila
    private static final long NEGOCIO_ID = 1L;
    private static final int DIAS = 7;

    private final NegocioRepository negocioRepository;
    private final CitaRepository citaRepository;
    private final Clock clock;

    public AgendaService(NegocioRepository negocioRepository, CitaRepository citaRepository, Clock clock) {
        this.negocioRepository = negocioRepository;
        this.citaRepository = citaRepository;
        this.clock = clock;
    }

    // desde null: hoy, en la zona horaria del comercio
    @Transactional(readOnly = true)
    public List<agendaDiaDTO> semana(LocalDate desde) {
        LocalDate inicio = desde != null ? desde : LocalDate.now(clock);
        LocalDate fin = inicio.plusDays(DIAS - 1);
        Negocio negocio = negocioRepository.findById(NEGOCIO_ID).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Negocio no encontrado"));

        Map<LocalDate, List<Cita>> citasPorDia = citaRepository.findByFechaBetween(inicio, fin).stream()
                .collect(Collectors.groupingBy(Cita::getFecha));

        List<agendaDiaDTO> dias = new ArrayList<>();
        for (LocalDate fecha = inicio; !fecha.isAfter(fin); fecha = fecha.plusDays(1)) {
            LocalDate dia = fecha;
            CierrePuntual cierre = negocio.getCierres().stream().filter(c -> c.cubre(dia)).findFirst().orElse(null);
            boolean sinTramos = negocio.getHorario().stream().noneMatch(t -> t.getDiaSemana() == dia.getDayOfWeek());

            List<agendaCitaDTO> citas = citasPorDia.getOrDefault(dia, List.of()).stream()
                    .sorted(Comparator.comparing(Cita::getHora))
                    .map(AgendaService::toDTO)
                    .toList();
            dias.add(new agendaDiaDTO(dia, cierre != null || sinTramos, cierre != null ? cierre.getMotivo() : null, citas));
        }
        return dias;
    }

    private static agendaCitaDTO toDTO(Cita cita) {
        Cliente c = cita.getCliente();
        return new agendaCitaDTO(
                cita.getHora(),
                cita.getHora().plusMinutes(cita.getDuracionMinutos()),
                cita.getEstado(),
                cita.getServicios().stream().map(Servicio::getNombre).toList(),
                cita.getPrecioTotal(),
                c == null ? null : new agendaClienteDTO(c.getName(), c.getTelefono(), c.getEmail()),
                cita.getNotas());
    }
}
