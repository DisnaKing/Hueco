package disnaking.Hueco.controller;

import disnaking.Hueco.model.Cliente;
import disnaking.Hueco.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public List<Cliente> listar() {
        return clienteService.listar();
    }

    @GetMapping("/{id}")
    public Cliente mostrarCliente(@PathVariable long id){
        return clienteService.obtener(id);
    }

    @PostMapping("/create")
    public ResponseEntity<Cliente> crear(@RequestBody Cliente cliente) {
        Cliente creado = clienteService.crear(cliente);
        URI location = URI.create("/clientes/" + creado.getId());
        return ResponseEntity.created(location).body(creado);
    }

    
}
