package disnaking.Hueco.service;

import disnaking.Hueco.model.Cita;
import disnaking.Hueco.model.Cliente;
import disnaking.Hueco.model.Servicio;
import disnaking.Hueco.repository.CitaRepository;
import disnaking.Hueco.repository.ServicioRepository;

import java.util.List;

public class ServicioService {
    private final ServicioRepository servicioRepository;
    public ServicioService(ServicioRepository repo){
        servicioRepository = repo;
    }

    public Servicio obtener(long id){
        return servicioRepository.getReferenceById(id);
    }
    public List<Servicio> listar(){
        return servicioRepository.findAll();
    }
    public Servicio crear(Servicio servicio){
        return servicioRepository.save(servicio);
    }
}
