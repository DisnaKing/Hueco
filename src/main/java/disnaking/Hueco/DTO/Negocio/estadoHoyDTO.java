package disnaking.Hueco.DTO.Negocio;

import disnaking.Hueco.model.EstadoApertura;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class estadoHoyDTO {
    private EstadoApertura estado;
    private LocalTime hora;
    private DayOfWeek dia;
    // Fecha de ese día; con un cierre largo la próxima apertura puede estar a más de una semana
    private LocalDate fecha;

    public estadoHoyDTO(EstadoApertura estado, LocalTime hora, LocalDate fecha) {
        this.estado = estado;
        this.hora = hora;
        this.fecha = fecha;
        this.dia = fecha == null ? null : fecha.getDayOfWeek();
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

    public LocalDate getFecha() {
        return fecha;
    }
}
