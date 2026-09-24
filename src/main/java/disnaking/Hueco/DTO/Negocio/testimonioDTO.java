package disnaking.Hueco.DTO.Negocio;

public class testimonioDTO {
    private String autor;
    private String texto;

    public testimonioDTO(String autor, String texto) {
        this.autor = autor;
        this.texto = texto;
    }

    public String getAutor() {
        return autor;
    }

    public String getTexto() {
        return texto;
    }
}
