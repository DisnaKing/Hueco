package disnaking.Hueco.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Cliente {

    @Id
    @GeneratedValue
    private long cliente_id;
    private  String name;

    // Normalizado (+34XXXXXXXXX): identifica al cliente que reserva como invitado
    @Column(unique = true)
    private String telefono;
    private String email;
    private LocalDateTime creadoEn;

    @OneToMany(mappedBy="cliente")
    private List<Cita> citas = new ArrayList<>();

    public Cliente(){}

    public long getId() {
        return this.cliente_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Cita> getCitas() {
        return citas;
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

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}
