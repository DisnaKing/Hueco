package disnaking.Hueco.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Embeddable
public class TramoHorario {

    @Enumerated(EnumType.STRING)
    private DayOfWeek diaSemana;
    private LocalTime apertura;
    private LocalTime cierre;

    public TramoHorario() {}

    public TramoHorario(DayOfWeek diaSemana, LocalTime apertura, LocalTime cierre) {
        this.diaSemana = diaSemana;
        this.apertura = apertura;
        this.cierre = cierre;
    }

    public DayOfWeek getDiaSemana() {
        return diaSemana;
    }

    public LocalTime getApertura() {
        return apertura;
    }

    public LocalTime getCierre() {
        return cierre;
    }
}
