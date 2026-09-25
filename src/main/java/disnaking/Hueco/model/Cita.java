package disnaking.Hueco.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
public class Cita {

    @Id
    @GeneratedValue
    private long id;

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

    // Lo acordado al reservar: si luego cambia un servicio, la cita no cambia
    @Column(nullable = false)
    private int duracionMinutos;
    @Column(nullable = false)
    private BigDecimal precioTotal;

    @Column(length = 300)
    private String notas;

    // Identificador público de la cita (enlace de confirmación, .ics): no deja adivinar otras citas
    @Column(unique = true, length = 36)
    private String token;

    public Cita(long id, ArrayList<Servicio>servicios, LocalDate fecha, LocalTime hora, EstadoCita estado, Cliente cliente){
        this.id = id;
        this.servicios = servicios;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.cliente = cliente;
    }

    public Cita() {}

    public void setId(long id) {
        this.id = id;
    }

    public List<Servicio> getServicios() {
        return servicios;
    }

    public void setServicios(List<Servicio> servicios) {
        this.servicios = servicios;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    // Se calcula una sola vez, al guardar la cita por primera vez
    @PrePersist
    void calcularTotales() {
        if (token == null) token = UUID.randomUUID().toString();
        duracionMinutos = servicios.stream()
                .mapToInt(Servicio::getDuracionMinutos)
                .sum();
        precioTotal = servicios.stream()
                .map(Servicio::getPrecio)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public BigDecimal getPrecioTotal() {
        return precioTotal;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public long getId() {
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

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public String getToken() {
        return token;
    }
}
