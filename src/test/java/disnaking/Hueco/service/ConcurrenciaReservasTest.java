package disnaking.Hueco.service;

import disnaking.Hueco.DTO.Reserva.reservaCrearDTO;
import disnaking.Hueco.Exception.Reserva.ReservaRechazadaException;
import disnaking.Hueco.repository.EstadoSql;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// Las reservas ya no se ponen en fila por el negocio: solo por teléfono. La hora (con su margen) la
// garantiza ex_cita_solape. huecos.sql: todos los días de 9:00 a 20:00 salvo mañana; servicio 1 = 30 min.
@SpringBootTest
@ActiveProfiles("test")
@Sql({"/limpiar.sql", "/huecos.sql"})
class ConcurrenciaReservasTest {

    private static final LocalDate DIA = LocalDate.now(ZoneId.of("Europe/Madrid")).plusDays(2);
    // Margen por defecto (hueco.margen-minutos)
    private static final int MARGEN = 5;

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private TransactionTemplate tx;

    private final ExecutorService hilos = Executors.newCachedThreadPool();

    @AfterEach
    void cerrarHilos() {
        hilos.shutdownNow();
    }

    private static reservaCrearDTO datos(String hora, String telefono) {
        reservaCrearDTO datos = new reservaCrearDTO();
        datos.setServicios(List.of(1L));
        datos.setFecha(DIA.toString());
        datos.setHora(hora);
        datos.setNombre("Cliente " + telefono);
        datos.setTelefono(telefono);
        return datos;
    }

    // Token de la cita o el motivo del rechazo
    private String reservar(String hora, String telefono) {
        try {
            return reservaService.reservar(datos(hora, telefono), "10.0.0.1");
        } catch (ReservaRechazadaException e) {
            return e.getMotivo().name();
        }
    }

    private Future<String> reservarEnOtroHilo(String hora, String telefono, CountDownLatch salida) {
        return hilos.submit(() -> {
            salida.await();
            return reservar(hora, telefono);
        });
    }

    // Abre una transacción en otro hilo, ejecuta el SQL y la mantiene abierta hasta que se suelte
    private Future<?> mantenerTransaccion(String sql, CountDownLatch dentro, CountDownLatch soltar) {
        return hilos.submit(() -> tx.executeWithoutResult(estado -> {
            jdbc.execute(sql);
            dentro.countDown();
            try {
                soltar.await(20, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }));
    }

    private String insertCita(String hora, String estado) {
        return """
                INSERT INTO cita (fecha, hora, estado, duracion_minutos, precio_total, token, margen_minutos)
                VALUES ('%s', '%s', '%s', 30, 15, gen_random_uuid()::text, %d)""".formatted(DIA, hora, estado, MARGEN);
    }

    private int citas() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM cita", Integer.class);
    }

    @Test
    void laBaseDeDatosRechazaUnaCitaDentroDelMargenDeOtra() {
        reservar("10:00", "600111222");

        // 10:30 es justo el final de la cita de las 10:00, pero cae en su margen
        assertThatThrownBy(() -> jdbc.update(insertCita("10:30", "CONFIRMADA")))
                .isInstanceOf(DataIntegrityViolationException.class)
                .satisfies(e -> assertThat(EstadoSql.esSolape(e)).isTrue());
        jdbc.update(insertCita("10:" + (30 + MARGEN), "CONFIRMADA"));
        // Las canceladas no ocupan hueco
        jdbc.update(insertCita("10:20", "CANCELADA"));

        assertThat(citas()).isEqualTo(3);
    }

    @Test
    void siOtraReservaCogeElMargenMientrasTantoDa409() throws Exception {
        // Otra transacción tiene insertada, sin confirmar, una cita a las 10:30: la comprobación en Java no la ve
        CountDownLatch dentro = new CountDownLatch(1);
        CountDownLatch soltar = new CountDownLatch(1);
        Future<?> otra = mantenerTransaccion(insertCita("10:30", "CONFIRMADA"), dentro, soltar);
        assertThat(dentro.await(10, TimeUnit.SECONDS)).isTrue();

        // Su insert espera a la otra transacción y, cuando esta confirma, choca con ex_cita_solape
        Future<String> reserva = reservarEnOtroHilo("10:00", "611222333", new CountDownLatch(0));
        Thread.sleep(500);
        soltar.countDown();
        otra.get(10, TimeUnit.SECONDS);

        assertThat(reserva.get(10, TimeUnit.SECONDS)).isEqualTo("HORA_OCUPADA");
        assertThat(citas()).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM cliente", Integer.class)).isZero();
    }

    @Test
    void elMismoTelefonoEnElLimiteSoloConsigueUnaMas() throws Exception {
        reservar("10:00", "600111222");
        reservar("11:00", "600111222");

        CountDownLatch salida = new CountDownLatch(1);
        Future<String> a = reservarEnOtroHilo("12:00", "600111222", salida);
        Future<String> b = reservarEnOtroHilo("13:00", "600111222", salida);
        salida.countDown();

        assertThat(List.of(a.get(10, TimeUnit.SECONDS), b.get(10, TimeUnit.SECONDS)))
                .containsOnlyOnce("LIMITE_TELEFONO");
        assertThat(citas()).isEqualTo(3);
    }

    @Test
    void unTelefonoNuevoReservandoDosVecesALaVezEsUnSoloCliente() throws Exception {
        CountDownLatch salida = new CountDownLatch(1);
        Future<String> a = reservarEnOtroHilo("12:00", "622333444", salida);
        Future<String> b = reservarEnOtroHilo("13:00", "622333444", salida);
        salida.countDown();

        assertThat(List.of(a.get(10, TimeUnit.SECONDS), b.get(10, TimeUnit.SECONDS)))
                .noneMatch(r -> r.equals("LIMITE_TELEFONO") || r.equals("HORA_OCUPADA"));
        assertThat(citas()).isEqualTo(2);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM cliente", Integer.class)).isEqualTo(1);
    }

    @Test
    void reservarNoEsperaPorElNegocio() throws Exception {
        CountDownLatch dentro = new CountDownLatch(1);
        CountDownLatch soltar = new CountDownLatch(1);
        mantenerTransaccion("SELECT * FROM negocio WHERE id = 1 FOR UPDATE", dentro, soltar);
        assertThat(dentro.await(10, TimeUnit.SECONDS)).isTrue();

        try {
            Future<String> reserva = reservarEnOtroHilo("10:00", "600111222", new CountDownLatch(0));
            assertThat(reserva.get(5, TimeUnit.SECONDS)).hasSize(36);
        } finally {
            soltar.countDown();
        }
    }

    @Test
    void soloEsperanLasReservasDelMismoTelefono() throws Exception {
        CountDownLatch dentro = new CountDownLatch(1);
        CountDownLatch soltar = new CountDownLatch(1);
        mantenerTransaccion("SELECT pg_advisory_xact_lock(1, hashtext('+34600111222'))", dentro, soltar);
        assertThat(dentro.await(10, TimeUnit.SECONDS)).isTrue();

        List<Future<String>> reservas = new ArrayList<>();
        try {
            reservas.add(reservarEnOtroHilo("10:00", "600111222", new CountDownLatch(0)));
            // Otro teléfono no espera
            assertThat(reservarEnOtroHilo("12:00", "611222333", new CountDownLatch(0)).get(5, TimeUnit.SECONDS))
                    .hasSize(36);
            // El mismo teléfono sigue esperando
            assertThatThrownBy(() -> reservas.getFirst().get(1, TimeUnit.SECONDS))
                    .isInstanceOf(TimeoutException.class);
        } finally {
            soltar.countDown();
        }
        assertThat(reservas.getFirst().get(10, TimeUnit.SECONDS)).hasSize(36);
    }
}
