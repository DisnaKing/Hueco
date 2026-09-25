package disnaking.Hueco.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TelefonosTest {

    @Test
    void normalizaLosFormatosHabituales() {
        for (String escrito : new String[]{"600111222", "600 11 12 22", "600-111-222", "+34 600 111 222", "0034600111222", " (600) 111.222 "}) {
            assertThat(Telefonos.normalizar(escrito, "+34")).as(escrito).contains("+34600111222");
        }
    }

    @Test
    void aceptaFijosYMovilesEspanoles() {
        assertThat(Telefonos.normalizar("912345678", "+34")).contains("+34912345678");
        assertThat(Telefonos.normalizar("712345678", "+34")).contains("+34712345678");
    }

    @Test
    void rechazaLoQueNoEsUnTelefonoEspanol() {
        for (String escrito : new String[]{null, "", "12345", "512345678", "6001112223", "+44 7700 900123", "abc"}) {
            assertThat(Telefonos.normalizar(escrito, "+34")).as(String.valueOf(escrito)).isEmpty();
        }
    }
}
