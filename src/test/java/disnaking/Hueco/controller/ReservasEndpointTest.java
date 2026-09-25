package disnaking.Hueco.controller;

import disnaking.Hueco.DTO.Reserva.reservaCrearDTO;
import disnaking.Hueco.Exception.Reserva.ReservaRechazadaException;
import disnaking.Hueco.service.ReservaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// huecos.sql: se trabaja todos los días de 9:00 a 20:00 salvo mañana (cierre); servicio 1 = Corte 30 min 15 €,
// el 2 está inactivo. Las reservas van a pasado mañana para no depender de la hora a la que corre el test.
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql({"/limpiar.sql", "/huecos.sql"})
class ReservasEndpointTest {

    private static final ZoneId MADRID = ZoneId.of("Europe/Madrid");
    private static final LocalDate DIA = LocalDate.now(MADRID).plusDays(2);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private ReservaService reservaService;

    private static String cuerpo(String hora, String telefono, String nombre) {
        return """
                {"servicios": [1], "fecha": "%s", "hora": "%s", "nombre": "%s", "telefono": "%s",
                 "email": "ana@example.com", "notas": "Pelo largo"}""".formatted(DIA, hora, nombre, telefono);
    }

    private String reservar(String hora, String telefono) throws Exception {
        String respuesta = mockMvc.perform(post("/api/reservas").contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(hora, telefono, "Ana")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isString())
                .andReturn().getResponse().getContentAsString();
        return respuesta.replaceAll(".*\"token\"\\s*:\\s*\"([^\"]+)\".*", "$1");
    }

    @Test
    void reservaYResumenSinDatosPersonales() throws Exception {
        String token = reservar("10:00", "600 111 222");

        mockMvc.perform(get("/api/reservas/" + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fecha").value(DIA.toString()))
                .andExpect(jsonPath("$.hora").value(startsWith("10:00")))
                .andExpect(jsonPath("$.duracionMinutos").value(30))
                .andExpect(jsonPath("$.precioTotal").value(15.0))
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"))
                .andExpect(jsonPath("$.servicios[0].nombre").value("Corte"))
                .andExpect(jsonPath("$.nombre").doesNotExist())
                .andExpect(jsonPath("$.telefono").doesNotExist())
                .andExpect(jsonPath("$.notas").doesNotExist());

        assertThat(jdbc.queryForObject("SELECT telefono FROM cliente", String.class)).isEqualTo("+34600111222");
        assertThat(jdbc.queryForObject("SELECT notas FROM cita", String.class)).isEqualTo("Pelo largo");
    }

    @Test
    void cadaCampoInvalidoTraeSuError() throws Exception {
        mockMvc.perform(post("/api/reservas").contentType(MediaType.APPLICATION_JSON).content("""
                        {"servicios": [2], "fecha": "mañana", "nombre": " ", "telefono": "12345",
                         "email": "ana", "notas": "%s"}""".formatted("x".repeat(301))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores", allOf(
                        hasKey("nombre"), hasKey("telefono"), hasKey("email"), hasKey("notas"),
                        hasKey("servicios"), hasKey("fecha"), hasKey("hora"))));
    }

    @Test
    void unaHoraYaOcupadaDa409() throws Exception {
        reservar("10:00", "600111222");

        mockMvc.perform(post("/api/reservas").contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo("10:00", "611222333", "Luis")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.motivo").value("HORA_OCUPADA"));
    }

    @Test
    void laCuartaCitaDelMismoTelefonoDa429() throws Exception {
        reservar("10:00", "600111222");
        reservar("11:00", "600111222");
        reservar("12:00", "600111222");

        mockMvc.perform(post("/api/reservas").contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo("13:00", "600111222", "Ana")))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.motivo").value("LIMITE_TELEFONO"));
    }

    @Test
    void elCampoTrampaRespondeBienSinGuardar() throws Exception {
        mockMvc.perform(post("/api/reservas").contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo("10:00", "600111222", "Bot").replace("}", ", \"website\": \"http://spam\"}")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isString());

        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM cita", Integer.class)).isZero();
    }

    @Test
    void elMismoTelefonoEsElMismoCliente() throws Exception {
        reservar("10:00", "600 111 222");
        mockMvc.perform(post("/api/reservas").contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo("11:00", "+34600111222", "Ana María")))
                .andExpect(status().isCreated());

        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM cliente", Integer.class)).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT name FROM cliente", String.class)).isEqualTo("Ana María");
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM cita", Integer.class)).isEqualTo(2);
    }

    @Test
    void dosReservasSimultaneasALaMismaHora() throws Exception {
        CountDownLatch salida = new CountDownLatch(1);
        ExecutorService hilos = Executors.newFixedThreadPool(2);
        List<Future<String>> resultados = new ArrayList<>();
        for (String telefono : new String[]{"600111222", "611222333"}) {
            reservaCrearDTO datos = new reservaCrearDTO();
            datos.setServicios(List.of(1L));
            datos.setFecha(DIA.toString());
            datos.setHora("10:00");
            datos.setNombre("Cliente " + telefono);
            datos.setTelefono(telefono);
            resultados.add(hilos.submit(() -> {
                salida.await();
                try {
                    return reservaService.reservar(datos, "10.0.0." + telefono.charAt(1));
                } catch (ReservaRechazadaException e) {
                    return e.getMotivo().name();
                }
            }));
        }
        salida.countDown();
        List<String> finales = new ArrayList<>();
        for (Future<String> r : resultados) finales.add(r.get(10, TimeUnit.SECONDS));
        hilos.shutdown();

        assertThat(finales).containsOnlyOnce("HORA_OCUPADA");
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM cita", Integer.class)).isEqualTo(1);
    }

    @Test
    void elIcsLlevaLaHoraEnUtc() throws Exception {
        String token = reservar("10:00", "600111222");
        String inicioUtc = DIA.atTime(10, 0).atZone(MADRID).withZoneSameInstant(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"));

        String ics = mockMvc.perform(get("/api/reservas/" + token + "/cita.ics").param("nombre", "Peluquería Ejemplo"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/calendar"))
                .andExpect(header().string("Content-Disposition", containsString("cita.ics")))
                .andReturn().getResponse().getContentAsString();

        assertThat(ics).startsWith("BEGIN:VCALENDAR\r\n").endsWith("END:VCALENDAR\r\n");
        assertThat(ics).contains("DTSTART:" + inicioUtc + "\r\n");
        assertThat(ics).contains("SUMMARY:Cita en Peluquería Ejemplo\r\n");
        assertThat(ics).contains("UID:" + token + "@hueco");
        assertThat(ics).contains("LOCATION:Calle");
    }

    @Test
    void unTokenQueNoExisteDa404() throws Exception {
        mockMvc.perform(get("/api/reservas/no-existe")).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/reservas/no-existe/cita.ics")).andExpect(status().isNotFound());
    }
}
