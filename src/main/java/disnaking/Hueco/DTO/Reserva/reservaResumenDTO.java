package disnaking.Hueco.DTO.Reserva;

import disnaking.Hueco.model.EstadoCita;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// Lo que ve quien tiene el enlace de la cita: sin nombre, teléfono, email ni notas
public class reservaResumenDTO {
    private LocalDate fecha;
    private LocalTime hora;
    private int duracionMinutos;
    private BigDecimal precioTotal;
    private EstadoCita estado;
    private List<servicioReservadoDTO> servicios;

    public reservaResumenDTO(LocalDate fecha, LocalTime hora, int duracionMinutos, BigDecimal precioTotal,
                             EstadoCita estado, List<servicioReservadoDTO> servicios) {
        this.fecha = fecha;
        this.hora = hora;
        this.duracionMinutos = duracionMinutos;
        this.precioTotal = precioTotal;
        this.estado = estado;
        this.servicios = servicios;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public BigDecimal getPrecioTotal() {
        return precioTotal;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public List<servicioReservadoDTO> getServicios() {
        return servicios;
    }
}
