package disnaking.Hueco.service;

import disnaking.Hueco.model.Cita;
import disnaking.Hueco.model.Cliente;
import disnaking.Hueco.repository.CitaRepository;

import java.util.List;

public class CitaService {
    private final CitaRepository citaRepository;
    public CitaService(CitaRepository repo){
        citaRepository = repo;
    }

    public Cita obtener(long id){
        return citaRepository.getReferenceById(id);
    }
    public List<Cita> listar(){
        return citaRepository.findAll();
    }
    public Cita crear(Cita cita){
        return citaRepository.save(cita);
    }
}
