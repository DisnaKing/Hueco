package disnaking.Hueco.service;

import disnaking.Hueco.config.ReservaPublicaProperties;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.assertj.core.api.Assertions.assertThat;

class LimiteReservasPorIpTest {

    // Reloj que se puede adelantar a mano
    private static final class RelojManual extends Clock {
        private Instant ahora = Instant.parse("2026-09-21T10:00:00Z");

        void avanzar(Duration tiempo) {
            ahora = ahora.plus(tiempo);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return ahora;
        }
    }

    @Test
    void permiteHastaElMaximoPorHoraYLuegoSeLibera() {
        RelojManual reloj = new RelojManual();
        LimiteReservasPorIp limite = new LimiteReservasPorIp(reloj, new ReservaPublicaProperties("+34", 3, 2));

        limite.registrar("1.1.1.1");
        limite.registrar("1.1.1.1");
        assertThat(limite.permitido("1.1.1.1")).isFalse();
        // Otra IP no se ve afectada
        assertThat(limite.permitido("2.2.2.2")).isTrue();

        reloj.avanzar(Duration.ofMinutes(61));
        assertThat(limite.permitido("1.1.1.1")).isTrue();
    }
}
