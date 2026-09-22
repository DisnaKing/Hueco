package disnaking.Hueco.DTO.Cita;

import disnaking.Hueco.model.Cliente;
import disnaking.Hueco.model.EstadoCita;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public class citaPatchDTO {
    private LocalDate fecha;
    private LocalTime hora;
    private EstadoCita estado;

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }
}
