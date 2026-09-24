package disnaking.Hueco.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.math.BigDecimal;

@Entity
public class Servicio {

    @Id
    @GeneratedValue
    private long id;
    private String nombre;
    @Column(length = 160)
    private String descripcion;
    private int duracionMinutos;
    private BigDecimal precio;
    private boolean activo;
    // Puede ser nula: los comercios sin categorías muestran una lista simple
    private String categoria;
    private int orden;


    public Servicio() {}

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public String getCategoria() {
        return categoria;
    }

    public int getOrden() {
        return orden;
    }

    public long getId() {
        return id;
    }
}
