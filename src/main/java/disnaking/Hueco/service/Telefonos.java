package disnaking.Hueco.service;

import java.util.Optional;
import java.util.regex.Pattern;

// Teléfonos de España: 9 cifras que empiezan por 6, 7, 8 o 9, con o sin +34 / 0034 delante
public final class Telefonos {

    private static final Pattern NUMERO_ESPANOL = Pattern.compile("[6789]\\d{8}");

    private Telefonos() {
    }

    // " 600 11-22 33 " o "+34 600112233" → "+34600112233"; vacío si no es un teléfono válido
    public static Optional<String> normalizar(String telefono, String prefijo) {
        if (telefono == null) return Optional.empty();
        String cifras = telefono.replaceAll("[\\s.\\-()]", "");
        if (cifras.startsWith("+34")) cifras = cifras.substring(3);
        else if (cifras.startsWith("0034")) cifras = cifras.substring(4);
        return NUMERO_ESPANOL.matcher(cifras).matches() ? Optional.of(prefijo + cifras) : Optional.empty();
    }
}
