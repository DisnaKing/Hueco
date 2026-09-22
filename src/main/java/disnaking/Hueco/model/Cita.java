package disnaking.Hueco.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Cita {

    @Id
    @GeneratedValue
    private int id;

    @ManyToMany
    @JoinTable(name = "cita_servicio",
            joinColumns = @JoinColumn(name = "cita_id"),
            inverseJoinColumns = @JoinColumn(name = "servicio_id"))
    private List<Servicio> servicios = new ArrayList<>();

    private LocalDate fecha;
    private LocalTime hora;

    @Enumerated(EnumType.STRING)
    private EstadoCita estado;

    @ManyToOne
    @JoinColumn(name="cliente_id")
    private Cliente cliente;

    public Cita(int id, ArrayList<Servicio>servicios, LocalDate fecha, LocalTime hora, EstadoCita estado, Cliente cliente){
        this.id = id;
        this.servicios = servicios;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.cliente = cliente;
    }

    public Cita() {}

    public int getDuracionTotalMinutos() {
        return servicios.stream()
                .mapToInt(Servicio::getDuracionMinutos)
                .sum();
    }

    public BigDecimal getPrecioTotal() {
        return servicios.stream()
                .map(Servicio::getPrecio)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }
}
