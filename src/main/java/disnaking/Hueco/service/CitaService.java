package disnaking.Hueco.service;

import disnaking.Hueco.DTO.Cita.citaCrearDTO;
import disnaking.Hueco.model.Cita;
import disnaking.Hueco.model.EstadoCita;
import disnaking.Hueco.model.Servicio;
import disnaking.Hueco.repository.CitaRepository;
import disnaking.Hueco.repository.ClienteRepository;
import disnaking.Hueco.repository.ServicioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashSet;
import java.util.List;

@Service
public class CitaService {
    private final CitaRepository citaRepository;
    private final ServicioRepository servicioRepository;
    private final ClienteRepository clienteRepository;

    public CitaService(CitaRepository citaRepository, ServicioRepository servicioRepository,
                       ClienteRepository clienteRepository) {
        this.citaRepository = citaRepository;
        this.servicioRepository = servicioRepository;
        this.clienteRepository = clienteRepository;
    }

    public Cita obtener(long id){
        return citaRepository.getReferenceById(id);
    }
    public List<Cita> listar(){
        return citaRepository.findAll();
    }

    // Carga los servicios de la base de datos para que la duración y el precio guardados sean los reales
    @Transactional
    public Cita crear(citaCrearDTO datos) {
        List<Long> ids = List.copyOf(new LinkedHashSet<>(datos.getServicios()));
        if (ids.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cita necesita al menos un servicio");
        }
        List<Servicio> servicios = servicioRepository.findAllById(ids);
        if (servicios.size() != ids.size() || !servicios.stream().allMatch(Servicio::isActivo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Algún servicio no existe o no está activo");
        }

        Cita cita = new Cita();
        cita.setServicios(servicios);
        cita.setFecha(datos.getFecha());
        cita.setHora(datos.getHora());
        cita.setEstado(datos.getEstado() != null ? datos.getEstado() : EstadoCita.PENDIENTE);
        if (datos.getClienteId() != null) {
            cita.setCliente(clienteRepository.findById(datos.getClienteId()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cliente no encontrado")));
        }
        return citaRepository.save(cita);
    }
}
