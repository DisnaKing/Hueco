package disnaking.Hueco.DTO.Cliente;

import disnaking.Hueco.DTO.Cita.citaClienteDTO;

import java.util.ArrayList;
import java.util.List;

public class clienteInfoDTO {
    private long id;
    private String name;
    private List<citaClienteDTO> citas = new ArrayList<>();

    public clienteInfoDTO(long id, String name, List<citaClienteDTO>citas){
        this.id = id;
        this.name = name;
        this.citas = citas;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<citaClienteDTO> getCitas() {
        return citas;
    }
}
