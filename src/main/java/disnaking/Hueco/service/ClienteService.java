package disnaking.Hueco.service;

import disnaking.Hueco.model.Cliente;
import disnaking.Hueco.repository.ClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente obtener(long id){
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente no encontrado"
                ));
    }

    public List<Cliente> listar(){
        return clienteRepository.findAll();
    }

    public Cliente crear(Cliente cliente){
        return clienteRepository.save(cliente);
    }
}
