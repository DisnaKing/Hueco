package disnaking.Hueco.DTO.Negocio;

import java.time.LocalDate;

public class cierreDTO {
    private LocalDate desde;
    private LocalDate hasta;
    private String motivo;

    public cierreDTO(LocalDate desde, LocalDate hasta, String motivo) {
        this.desde = desde;
        this.hasta = hasta;
        this.motivo = motivo;
    }

    public LocalDate getDesde() {
        return desde;
    }

    public LocalDate getHasta() {
        return hasta;
    }

    public String getMotivo() {
        return motivo;
    }
}
