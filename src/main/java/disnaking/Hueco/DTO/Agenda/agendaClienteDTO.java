package disnaking.Hueco.DTO.Agenda;

public class agendaClienteDTO {
    private String nombre;
    private String telefono;
    private String email;

    public agendaClienteDTO(String nombre, String telefono, String email) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }
}
