package disnaking.Hueco.DTO.Servicio;

import java.math.BigDecimal;

public class servicioDTO {
    private long id;
    private String name;
    private int duracionMinutos;
    private BigDecimal precio;
    private boolean activo;
    private String categoria;

    public servicioDTO(long id, String name, int duracionMinutos, BigDecimal precio, boolean activo, String categoria) {
        this.id = id;
        this.name = name;
        this.duracionMinutos = duracionMinutos;
        this.precio = precio;
        this.activo = activo;
        this.categoria = categoria;
    }
}
