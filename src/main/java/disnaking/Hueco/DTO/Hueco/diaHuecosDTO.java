package disnaking.Hueco.DTO.Hueco;

import disnaking.Hueco.model.EstadoDia;

import java.time.LocalDate;
import java.util.List;

public class diaHuecosDTO {
    private LocalDate fecha;
    private EstadoDia estado;
    // Horas de inicio libres, "HH:mm", en la zona horaria del comercio
    private List<String> horas;

    public diaHuecosDTO(LocalDate fecha, EstadoDia estado, List<String> horas) {
        this.fecha = fecha;
        this.estado = estado;
        this.horas = horas;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public EstadoDia getEstado() {
        return estado;
    }

    public List<String> getHoras() {
        return horas;
    }
}
