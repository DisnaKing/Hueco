package disnaking.Hueco.DTO.Cita;

import disnaking.Hueco.DTO.Cliente.clienteAgendaDTO;
import disnaking.Hueco.model.EstadoCita;

import java.time.LocalDate;
import java.time.LocalTime;

public class citaAgendaDTO {
    private Long id;
    private LocalDate fecha;
    private LocalTime hora;
    private EstadoCita estado;
    private clienteAgendaDTO cliente;

    public citaAgendaDTO(Long id, LocalDate fecha, LocalTime hora, EstadoCita estado, clienteAgendaDTO cliente) {
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.cliente = cliente;
    }
}
