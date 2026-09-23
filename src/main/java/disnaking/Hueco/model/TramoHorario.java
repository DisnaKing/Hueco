package disnaking.Hueco.model;

import java.time.LocalTime;

public class TramoHorario {
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public TramoHorario(LocalTime horaInicio, LocalTime horaFin) {
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }
}
