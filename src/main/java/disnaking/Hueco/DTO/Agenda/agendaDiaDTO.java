package disnaking.Hueco.DTO.Agenda;

import java.time.LocalDate;
import java.util.List;

public class agendaDiaDTO {
    private LocalDate fecha;
    // Sin tramos ese día de la semana, o con cierre puntual
    private boolean cerrado;
    // Motivo del cierre puntual; null si simplemente no se trabaja ese día
    private String motivoCierre;
    private List<agendaCitaDTO> citas;

    public agendaDiaDTO(LocalDate fecha, boolean cerrado, String motivoCierre, List<agendaCitaDTO> citas) {
        this.fecha = fecha;
        this.cerrado = cerrado;
        this.motivoCierre = motivoCierre;
        this.citas = citas;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public boolean isCerrado() {
        return cerrado;
    }

    public String getMotivoCierre() {
        return motivoCierre;
    }

    public List<agendaCitaDTO> getCitas() {
        return citas;
    }
}
