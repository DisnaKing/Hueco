package disnaking.Hueco.service;

import disnaking.Hueco.DTO.Hueco.diaHuecosDTO;
import disnaking.Hueco.config.ReservaProperties;
import disnaking.Hueco.model.Negocio;
import disnaking.Hueco.model.Servicio;
import disnaking.Hueco.repository.CitaRepository;
import disnaking.Hueco.repository.NegocioRepository;
import disnaking.Hueco.repository.ServicioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
public class HuecoService {

    // El comercio es una sola fila
    private static final long NEGOCIO_ID = 1L;

    private final NegocioRepository negocioRepository;
    private final ServicioRepository servicioRepository;
    private final CitaRepository citaRepository;
    private final CalculadoraHuecos calculadora;
    private final Clock clock;
    private final ReservaProperties reglas;

    public HuecoService(NegocioRepository negocioRepository, ServicioRepository servicioRepository,
                        CitaRepository citaRepository, CalculadoraHuecos calculadora, Clock clock,
                        ReservaProperties reglas) {
        this.negocioRepository = negocioRepository;
        this.servicioRepository = servicioRepository;
        this.citaRepository = citaRepository;
        this.calculadora = calculadora;
        this.clock = clock;
        this.reglas = reglas;
    }

    // Los ids que no son servicios activos se ignoran; si no queda ninguno, 400
    @Transactional(readOnly = true)
    public List<diaHuecosDTO> huecos(List<Long> servicioIds) {
        List<Servicio> servicios = servicioRepository.findAllById(servicioIds).stream()
                .filter(Servicio::isActivo)
                .toList();
        if (servicios.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ningún servicio válido");
        }
        int duracion = servicios.stream().mapToInt(Servicio::getDuracionMinutos).sum();

        Negocio negocio = negocioRepository.findById(NEGOCIO_ID).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Negocio no encontrado"));

        return calculadora.dias(negocio.getHorario(), negocio.getCierres(), ocupacionesDelPlazo(), duracion);
    }

    // Citas de hoy hasta el final del plazo de reserva, como las necesita la calculadora
    public List<Ocupacion> ocupacionesDelPlazo() {
        LocalDate hoy = LocalDate.now(clock);
        return citaRepository.findByFechaBetween(hoy, hoy.plusDays(reglas.diasVista() - 1)).stream()
                .map(c -> new Ocupacion(c.getFecha(), c.getHora(), c.getDuracionMinutos(), c.getEstado()))
                .toList();
    }
}
