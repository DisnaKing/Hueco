package disnaking.Hueco.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorreosReservaTest {

    @Test
    void duracionComoEnLaWeb() {
        assertThat(CorreosReserva.duracion(30)).isEqualTo("30 min");
        assertThat(CorreosReserva.duracion(60)).isEqualTo("1 h");
        assertThat(CorreosReserva.duracion(75)).isEqualTo("1 h 15 min");
    }
}
