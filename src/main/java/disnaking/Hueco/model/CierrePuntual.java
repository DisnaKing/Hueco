package disnaking.Hueco.model;

import jakarta.persistence.Embeddable;

import java.time.LocalDate;

// Festivo o vacaciones: días completos, con desde y hasta incluidos
@Embeddable
public class CierrePuntual {

    private LocalDate desde;
    private LocalDate hasta;
    private String motivo;

    public CierrePuntual() {}

    public CierrePuntual(LocalDate desde, LocalDate hasta, String motivo) {
        this.desde = desde;
        this.hasta = hasta;
        this.motivo = motivo;
    }

    public boolean cubre(LocalDate fecha) {
        return !fecha.isBefore(desde) && !fecha.isAfter(hasta);
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
