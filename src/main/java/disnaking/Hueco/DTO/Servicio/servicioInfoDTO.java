package disnaking.Hueco.DTO.Servicio;

import disnaking.Hueco.model.Servicio;

import java.math.BigDecimal;

public class servicioInfoDTO {
    private long id;
    private String nombre;
    private String descripcion;
    private int duracionMinutos;
    private BigDecimal precio;
    private String categoria;

    public servicioInfoDTO(long id, String nombre, String descripcion, int duracionMinutos, BigDecimal precio, String categoria) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracionMinutos = duracionMinutos;
        this.precio = precio;
        this.categoria = categoria;
    }

    public static servicioInfoDTO from(Servicio servicio) {
        return new servicioInfoDTO(
                servicio.getId(),
                servicio.getNombre(),
                servicio.getDescripcion(),
                servicio.getDuracionMinutos(),
                servicio.getPrecio(),
                servicio.getCategoria()
        );
    }

    public long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public String getCategoria() {
        return categoria;
    }
}
