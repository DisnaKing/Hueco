package disnaking.Hueco.DTO.Negocio;

import disnaking.Hueco.model.EstadoApertura;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class estadoHoyDTO {
    private EstadoApertura estado;
    private LocalTime hora;
    private DayOfWeek dia;

    public estadoHoyDTO(EstadoApertura estado, LocalTime hora, DayOfWeek dia) {
        this.estado = estado;
        this.hora = hora;
        this.dia = dia;
    }

    public EstadoApertura getEstado() {
        return estado;
    }

    public LocalTime getHora() {
        return hora;
    }

    public DayOfWeek getDia() {
        return dia;
    }
}
