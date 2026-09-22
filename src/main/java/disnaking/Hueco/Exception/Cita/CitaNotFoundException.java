package disnaking.Hueco.Exception.Cita;

public class CitaNotFoundException extends RuntimeException {
    public CitaNotFoundException(Long id) {
        super("No se encontró la cita con id " + id);
    }
}
