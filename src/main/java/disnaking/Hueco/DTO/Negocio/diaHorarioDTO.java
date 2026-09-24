package disnaking.Hueco.DTO.Negocio;

import java.time.DayOfWeek;
import java.util.List;

public class diaHorarioDTO {
    private DayOfWeek dia;
    // Vacía si ese día está cerrado
    private List<tramoHorarioDTO> tramos;

    public diaHorarioDTO(DayOfWeek dia, List<tramoHorarioDTO> tramos) {
        this.dia = dia;
        this.tramos = tramos;
    }

    public DayOfWeek getDia() {
        return dia;
    }

    public List<tramoHorarioDTO> getTramos() {
        return tramos;
    }
}
