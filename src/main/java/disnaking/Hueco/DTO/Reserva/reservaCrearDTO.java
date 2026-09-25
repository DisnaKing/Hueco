package disnaking.Hueco.DTO.Reserva;

import java.util.ArrayList;
import java.util.List;

// Cuerpo de POST /api/reservas. Fecha y hora llegan como texto para devolver un error por campo
// si no son válidas, en lugar de un 400 genérico de Jackson.
public class reservaCrearDTO {
    private List<Long> servicios = new ArrayList<>();
    private String fecha;
    private String hora;
    private String nombre;
    private String telefono;
    private String email;
    private String notas;
    // Campo trampa: invisible para las personas; si llega relleno, lo ha rellenado un bot
    private String website;

    public List<Long> getServicios() {
        return servicios;
    }

    public void setServicios(List<Long> servicios) {
        this.servicios = servicios;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
