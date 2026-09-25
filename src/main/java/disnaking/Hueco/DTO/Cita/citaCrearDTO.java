package disnaking.Hueco.DTO.Cita;

import disnaking.Hueco.model.EstadoCita;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// Cuerpo de POST /api/citas/create. Los servicios van por id: la duración y el precio
// los calcula el backend, nunca los manda el cliente.
public class citaCrearDTO {
    private LocalDate fecha;
    private LocalTime hora;
    private EstadoCita estado;
    private List<Long> servicios = new ArrayList<>();
    private Long clienteId;

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }

    public List<Long> getServicios() {
        return servicios;
    }

    public void setServicios(List<Long> servicios) {
        this.servicios = servicios;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }
}
