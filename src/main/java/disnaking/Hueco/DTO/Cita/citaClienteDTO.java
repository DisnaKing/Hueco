package disnaking.Hueco.DTO.Cita;

import disnaking.Hueco.model.EstadoCita;

public class citaClienteDTO {
    private long id;
    private String name;
    private EstadoCita estado;

    public citaClienteDTO(long id, String name, EstadoCita estado){
        this.id = id;
        this.name = name;
        this.estado = estado;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public EstadoCita getEstado() {
        return estado;
    }
}
