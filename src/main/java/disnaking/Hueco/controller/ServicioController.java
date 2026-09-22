package disnaking.Hueco.controller;

import disnaking.Hueco.model.Servicio;
import disnaking.Hueco.repository.ServicioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/api/servicios")
public class ServicioController {

    private final ServicioRepository servicioRepository;

    public ServicioController(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    @GetMapping
    public List<Servicio> listar() {
        return servicioRepository.findAll();
    }

    @GetMapping("/{id}")
    public Servicio mostrarCliente(@PathVariable long id){
        return servicioRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Servicio no encontrado"));
    }

    @PostMapping("/create")
    public ResponseEntity<Servicio> crear(@RequestBody Servicio servicio) {
        Servicio creado = servicioRepository.save(servicio);
        URI location = URI.create("/clientes/" + creado.getId());
        return ResponseEntity.created(location).body(creado);
    }
}
