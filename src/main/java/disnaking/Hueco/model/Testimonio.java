package disnaking.Hueco.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Testimonio {

    private String autor;
    private String texto;
    private int orden;

    public Testimonio() {}

    public String getAutor() {
        return autor;
    }

    public String getTexto() {
        return texto;
    }

    public int getOrden() {
        return orden;
    }
}
