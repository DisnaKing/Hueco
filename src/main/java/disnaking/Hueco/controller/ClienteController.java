package disnaking.Hueco.controller;

import disnaking.Hueco.DTO.Cita.citaClienteDTO;
import disnaking.Hueco.DTO.Cliente.clienteInfoDTO;
import disnaking.Hueco.DTO.Servicio.servicioInfoDTO;
import disnaking.Hueco.model.Cita;
import disnaking.Hueco.model.Cliente;
import disnaking.Hueco.repository.ClienteRepository;
import disnaking.Hueco.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteRepository clienteRepository;

    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @GetMapping
    public List<clienteInfoDTO> listar() {
        return clienteRepository.findAll().stream()
                .map(cliente -> new clienteInfoDTO(
                        cliente.getId(),
                        cliente.getName(),
                        cliente.getCitas().stream()
                                .map(cita-> new citaClienteDTO(
                                        cita.getId(),
                                        cita.getServicios().stream()
                                                .map(servicioInfoDTO::from).toString(),
                                        cita.getEstado()
                                )).toList()
                )).toList();
    }

    @GetMapping("/{id}")
    public Cliente mostrarCliente(@PathVariable long id){
        return clienteRepository.getReferenceById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<Cliente> crear(@RequestBody Cliente cliente) {
        Cliente creado = clienteRepository.save(cliente);
        URI location = URI.create("/clientes/" + creado.getId());
        return ResponseEntity.created(location).body(creado);
    }


}
