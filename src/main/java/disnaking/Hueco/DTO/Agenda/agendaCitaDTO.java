package disnaking.Hueco.DTO.Agenda;

import disnaking.Hueco.model.EstadoCita;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

public class agendaCitaDTO {
    private LocalTime inicio;
    private LocalTime fin;
    private EstadoCita estado;
    private List<String> servicios;
    private BigDecimal total;
    // null en las citas creadas sin cliente desde la gestión
    private agendaClienteDTO cliente;
    private String notas;

    public agendaCitaDTO(LocalTime inicio, LocalTime fin, EstadoCita estado, List<String> servicios,
                         BigDecimal total, agendaClienteDTO cliente, String notas) {
        this.inicio = inicio;
        this.fin = fin;
        this.estado = estado;
        this.servicios = servicios;
        this.total = total;
        this.cliente = cliente;
        this.notas = notas;
    }

    public LocalTime getInicio() {
        return inicio;
    }

    public LocalTime getFin() {
        return fin;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public List<String> getServicios() {
        return servicios;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public agendaClienteDTO getCliente() {
        return cliente;
    }

    public String getNotas() {
        return notas;
    }
}
