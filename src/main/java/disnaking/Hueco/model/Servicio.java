package disnaking.Hueco.model;

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
    private int duracionMinutos;
    private BigDecimal precio;
    private boolean activo;


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

    public boolean isActivo() {
        return activo;
    }

    public long getId() {
        return id;
    }
}
