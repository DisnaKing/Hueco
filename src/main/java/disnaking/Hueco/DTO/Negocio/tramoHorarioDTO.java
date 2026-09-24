package disnaking.Hueco.DTO.Negocio;

import java.time.LocalTime;

public class tramoHorarioDTO {
    private LocalTime apertura;
    private LocalTime cierre;

    public tramoHorarioDTO(LocalTime apertura, LocalTime cierre) {
        this.apertura = apertura;
        this.cierre = cierre;
    }

    public LocalTime getApertura() {
        return apertura;
    }

    public LocalTime getCierre() {
        return cierre;
    }
}
