package disnaking.Hueco.DTO.Servicio;

import disnaking.Hueco.model.servicioCategoria;

import java.math.BigDecimal;

public class servicioDTO {
    private long id;
    private String name;
    private int duracionMinutos;
    private BigDecimal precio;
    private boolean activo;
    private servicioCategoria categoria;

    public servicioDTO(long id, String name, int duracionMinutos, BigDecimal precio, boolean activo, servicioCategoria categoria) {
        this.id = id;
        this.name = name;
        this.duracionMinutos = duracionMinutos;
        this.precio = precio;
        this.activo = activo;
        this.categoria = categoria;
    }
}
