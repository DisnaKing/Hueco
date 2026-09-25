package disnaking.Hueco.controller;

import disnaking.Hueco.DTO.Cita.citaAgendaDTO;
import disnaking.Hueco.DTO.Cita.citaCrearDTO;
import disnaking.Hueco.DTO.Cita.citaPatchDTO;
import disnaking.Hueco.DTO.Cliente.clienteAgendaDTO;
import disnaking.Hueco.Exception.Cita.CitaNotFoundException;
import disnaking.Hueco.model.Cita;
import disnaking.Hueco.repository.CitaRepository;
import disnaking.Hueco.service.CitaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaRepository citaRepository;
    private final CitaService citaService;

    public CitaController(CitaRepository repo, CitaService citaService){
        citaRepository = repo;
        this.citaService = citaService;
    }

    @GetMapping
    public List<citaAgendaDTO> listar(){
        return citaRepository.findAll().stream()
                .map(cita -> new citaAgendaDTO(
                        cita.getId(),
                        cita.getFecha(),
                        cita.getHora(),
                        cita.getEstado(),
                        new clienteAgendaDTO(cita.getCliente().getId(), cita.getCliente().getName())
                ))
                .toList();
    }

    @GetMapping("/{id}")
    public Cita mostrarCliente(@PathVariable long id){
        return citaRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Cita no encontrada"));
    }

    @PostMapping("/create")
    public ResponseEntity<Cita> crear(@RequestBody citaCrearDTO datos) {
        Cita creado = citaService.crear(datos);
        URI location = URI.create("/api/citas/" + creado.getId());
        return ResponseEntity.created(location).body(creado);
    }

    @PatchMapping("/{id}")
    public Cita edit(@PathVariable long id, @RequestBody citaPatchDTO cambios){
        Cita cita = citaRepository.findById(id).orElseThrow(() -> new CitaNotFoundException(id));

        if (cambios.getFecha() != null) cita.setFecha(cambios.getFecha());

        if (cambios.getHora() != null) cita.setHora(cambios.getHora());

        if (cambios.getEstado() != null) cita.setEstado(cambios.getEstado());

        return citaRepository.save(cita);

    };

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> eliminar(@PathVariable long id){
        citaRepository.delete(citaRepository.getReferenceById(id));
        return ResponseEntity.noContent().build();
    }

}
