package disnaking.Hueco.Exception.Reserva;

import java.util.Map;

// 400 con un mensaje por campo, para mostrarlo junto a cada campo del formulario
public class ReservaInvalidaException extends RuntimeException {
    private final Map<String, String> errores;

    public ReservaInvalidaException(Map<String, String> errores) {
        super("Reserva no válida: " + errores.keySet());
        this.errores = errores;
    }

    public Map<String, String> getErrores() {
        return errores;
    }
}
