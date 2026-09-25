package disnaking.Hueco.DTO.Reserva;

import java.math.BigDecimal;

public class servicioReservadoDTO {
    private String nombre;
    private int duracionMinutos;
    private BigDecimal precio;

    public servicioReservadoDTO(String nombre, int duracionMinutos, BigDecimal precio) {
        this.nombre = nombre;
        this.duracionMinutos = duracionMinutos;
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public BigDecimal getPrecio() {
        return precio;
    }
}
