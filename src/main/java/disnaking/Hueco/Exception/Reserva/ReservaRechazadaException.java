package disnaking.Hueco.Exception.Reserva;

import org.springframework.http.HttpStatus;

// Reserva bien formada que no se puede hacer: la hora ya está ocupada (409) o se ha pasado un límite (429)
public class ReservaRechazadaException extends RuntimeException {

    public enum Motivo {
        // 409: la hora se ha ocupado mientras el cliente rellenaba sus datos
        HORA_OCUPADA(HttpStatus.CONFLICT),
        // 429: el teléfono ya tiene el máximo de citas vivas
        LIMITE_TELEFONO(HttpStatus.TOO_MANY_REQUESTS),
        // 429: demasiadas reservas desde la misma IP en la última hora
        LIMITE_IP(HttpStatus.TOO_MANY_REQUESTS);

        private final HttpStatus status;

        Motivo(HttpStatus status) {
            this.status = status;
        }

        public HttpStatus getStatus() {
            return status;
        }
    }

    private final Motivo motivo;

    public ReservaRechazadaException(Motivo motivo) {
        super("Reserva rechazada: " + motivo);
        this.motivo = motivo;
    }

    public Motivo getMotivo() {
        return motivo;
    }
}
