package disnaking.Hueco.service;

import disnaking.Hueco.DTO.Reserva.reservaCrearDTO;
import disnaking.Hueco.DTO.Reserva.reservaResumenDTO;
import disnaking.Hueco.DTO.Reserva.servicioReservadoDTO;
import disnaking.Hueco.Exception.Reserva.ReservaInvalidaException;
import disnaking.Hueco.Exception.Reserva.ReservaRechazadaException;
import disnaking.Hueco.Exception.Reserva.ReservaRechazadaException.Motivo;
import disnaking.Hueco.config.ReservaPublicaProperties;
import disnaking.Hueco.model.*;
import disnaking.Hueco.repository.CitaRepository;
import disnaking.Hueco.repository.ClienteRepository;
import disnaking.Hueco.repository.NegocioRepository;
import disnaking.Hueco.repository.ServicioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

// Reserva pública como invitado: valida, aplica los límites y crea la cita solo si la hora sigue libre
@Service
public class ReservaService {

    // El comercio es una sola fila
    private static final long NEGOCIO_ID = 1L;
    private static final int MAX_NOMBRE = 100;
    private static final int MAX_EMAIL = 150;
    private static final int MAX_NOTAS = 300;
    private static final Pattern EMAIL = Pattern.compile("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");

    private final NegocioRepository negocioRepository;
    private final ServicioRepository servicioRepository;
    private final CitaRepository citaRepository;
    private final ClienteRepository clienteRepository;
    private final HuecoService huecoService;
    private final CalculadoraHuecos calculadora;
    private final LimiteReservasPorIp limitePorIp;
    private final ReservaPublicaProperties reglas;
    private final Clock clock;

    public ReservaService(NegocioRepository negocioRepository, ServicioRepository servicioRepository,
                          CitaRepository citaRepository, ClienteRepository clienteRepository,
                          HuecoService huecoService, CalculadoraHuecos calculadora,
                          LimiteReservasPorIp limitePorIp, ReservaPublicaProperties reglas, Clock clock) {
        this.negocioRepository = negocioRepository;
        this.servicioRepository = servicioRepository;
        this.citaRepository = citaRepository;
        this.clienteRepository = clienteRepository;
        this.huecoService = huecoService;
        this.calculadora = calculadora;
        this.limitePorIp = limitePorIp;
        this.reglas = reglas;
        this.clock = clock;
    }

    // Devuelve el token de la cita nueva
    @Transactional
    public String reservar(reservaCrearDTO datos, String ip) {
        // Campo trampa relleno: se responde como si hubiera ido bien, sin guardar nada ni dar pistas
        if (datos.getWebsite() != null && !datos.getWebsite().isBlank()) {
            return UUID.randomUUID().toString();
        }

        Map<String, String> errores = new LinkedHashMap<>();
        String nombre = recortar(datos.getNombre());
        if (nombre == null) errores.put("nombre", "Escribe tu nombre");
        else if (nombre.length() > MAX_NOMBRE) errores.put("nombre", "Como mucho " + MAX_NOMBRE + " caracteres");

        Optional<String> telefono = Telefonos.normalizar(datos.getTelefono(), reglas.prefijoTelefono());
        if (telefono.isEmpty()) errores.put("telefono", "Escribe un teléfono de 9 cifras");

        String email = recortar(datos.getEmail());
        if (email != null && (email.length() > MAX_EMAIL || !EMAIL.matcher(email).matches())) {
            errores.put("email", "Revisa el email");
        }

        String notas = recortar(datos.getNotas());
        if (notas != null && notas.length() > MAX_NOTAS) errores.put("notas", "Como mucho " + MAX_NOTAS + " caracteres");

        List<Servicio> servicios = servicioRepository.findAllById(new LinkedHashSet<>(datos.getServicios())).stream()
                .filter(Servicio::isActivo)
                .toList();
        if (servicios.isEmpty()) errores.put("servicios", "Elige al menos un servicio");

        LocalDate fecha = parsear(datos.getFecha(), LocalDate::parse);
        if (fecha == null) errores.put("fecha", "Elige una fecha");
        LocalTime hora = parsear(datos.getHora(), LocalTime::parse);
        if (hora == null) errores.put("hora", "Elige una hora");

        if (!errores.isEmpty()) throw new ReservaInvalidaException(errores);

        if (!limitePorIp.permitido(ip)) throw new ReservaRechazadaException(Motivo.LIMITE_IP);

        // A partir de aquí, una reserva cada vez: la segunda de dos simultáneas espera y ve la hora ocupada
        Negocio negocio = negocioRepository.findByIdParaReservar(NEGOCIO_ID).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Negocio no encontrado"));

        long vivas = citaRepository.countByClienteTelefonoAndFechaGreaterThanEqualAndEstadoIn(
                telefono.get(), LocalDate.now(clock), List.of(EstadoCita.PENDIENTE, EstadoCita.CONFIRMADA));
        if (vivas >= reglas.maxCitasPorTelefono()) throw new ReservaRechazadaException(Motivo.LIMITE_TELEFONO);

        int duracion = servicios.stream().mapToInt(Servicio::getDuracionMinutos).sum();
        if (!calculadora.libre(negocio.getHorario(), negocio.getCierres(), huecoService.ocupacionesDelPlazo(),
                duracion, fecha, hora)) {
            throw new ReservaRechazadaException(Motivo.HORA_OCUPADA);
        }

        // El mismo teléfono es el mismo cliente; se actualizan el nombre y, si lo da, el email
        Cliente cliente = clienteRepository.findByTelefono(telefono.get()).orElseGet(() -> {
            Cliente nuevo = new Cliente();
            nuevo.setTelefono(telefono.get());
            nuevo.setCreadoEn(LocalDateTime.now(clock));
            return nuevo;
        });
        cliente.setName(nombre);
        if (email != null) cliente.setEmail(email);
        clienteRepository.save(cliente);

        // Sin panel de administración nadie confirmaría una PENDIENTE: nace confirmada
        Cita cita = new Cita();
        cita.setServicios(new ArrayList<>(servicios));
        cita.setFecha(fecha);
        cita.setHora(hora);
        cita.setEstado(EstadoCita.CONFIRMADA);
        cita.setCliente(cliente);
        cita.setNotas(notas);
        citaRepository.save(cita);

        limitePorIp.registrar(ip);
        return cita.getToken();
    }

    @Transactional(readOnly = true)
    public reservaResumenDTO resumen(String token) {
        Cita cita = buscar(token);
        return new reservaResumenDTO(cita.getFecha(), cita.getHora(), cita.getDuracionMinutos(), cita.getPrecioTotal(),
                cita.getEstado(), cita.getServicios().stream()
                .map(s -> new servicioReservadoDTO(s.getNombre(), s.getDuracionMinutos(), s.getPrecio()))
                .toList());
    }

    @Transactional(readOnly = true)
    public String ics(String token, String nombreComercio) {
        Cita cita = buscar(token);
        Negocio negocio = negocioRepository.findById(NEGOCIO_ID).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Negocio no encontrado"));
        return CalendarioIcs.evento(cita, negocio, nombreComercio, clock);
    }

    private Cita buscar(String token) {
        return citaRepository.findByToken(token).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada"));
    }

    private static String recortar(String texto) {
        if (texto == null || texto.isBlank()) return null;
        return texto.trim();
    }

    // null si falta o no tiene el formato esperado
    private static <T> T parsear(String texto, Function<String, T> lector) {
        if (texto == null) return null;
        try {
            return lector.apply(texto);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
