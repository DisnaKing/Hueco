package disnaking.Hueco.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

// Una sola fila (id = 1) con el contenido del comercio. El nombre se fija al compilar en el frontend.
@Entity
public class Negocio {

    @Id
    private long id;
    private String eslogan;
    @Lob
    private String sobreNosotros;
    private String direccion;
    private String telefono;
    private String email;

    // Un día sin tramos está cerrado; varios tramos en un día son un horario partido
    @ElementCollection
    @CollectionTable(name = "negocio_horario", joinColumns = @JoinColumn(name = "negocio_id"))
    private List<TramoHorario> horario = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "negocio_red_social", joinColumns = @JoinColumn(name = "negocio_id"))
    private List<RedSocial> redesSociales = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "negocio_testimonio", joinColumns = @JoinColumn(name = "negocio_id"))
    private List<Testimonio> testimonios = new ArrayList<>();

    public Negocio() {}

    public long getId() {
        return id;
    }

    public String getEslogan() {
        return eslogan;
    }

    public String getSobreNosotros() {
        return sobreNosotros;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public List<TramoHorario> getHorario() {
        return horario;
    }

    public List<RedSocial> getRedesSociales() {
        return redesSociales;
    }

    public List<Testimonio> getTestimonios() {
        return testimonios;
    }
}
