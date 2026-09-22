package disnaking.Hueco.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Cliente {

    @Id
    @GeneratedValue
    private long cliente_id;
    private  String name;

    @OneToMany(mappedBy="cliente")
    private List<Cita> citas = new ArrayList<>();

    public Cliente(){}

    public long getId() {
        return this.cliente_id;
    }
}
