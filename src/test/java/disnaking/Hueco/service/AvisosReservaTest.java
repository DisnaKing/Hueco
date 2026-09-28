package disnaking.Hueco.service;

import jakarta.mail.BodyPart;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// huecos.sql: el negocio tiene el email a@b.es y el servicio 1 es Corte, 30 min y 15 €
@SpringBootTest(properties = {
        "hueco.email.remitente=Peluquería Ejemplo <citas@peluqueria.test>",
        "hueco.email.nombre-comercio=Peluquería Ejemplo",
        "hueco.email.url-web=https://peluqueria.test/",
        "hueco.email.pausa-reintento=0s"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql({"/limpiar.sql", "/huecos.sql"})
class AvisosReservaTest {

    private static final LocalDate DIA = LocalDate.now(ZoneId.of("Europe/Madrid")).plusDays(2);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbc;

    @MockitoBean
    private JavaMailSender mail;

    @BeforeEach
    void mensajesReales() {
        when(mail.createMimeMessage()).thenAnswer(i -> new MimeMessage((Session) null));
    }

    private String reservar(String email) throws Exception {
        String campoEmail = email == null ? "" : "\"email\": \"%s\",".formatted(email);
        String respuesta = mockMvc.perform(post("/api/reservas").contentType(MediaType.APPLICATION_JSON).content("""
                        {"servicios": [1], "fecha": "%s", "hora": "10:00", "nombre": "Ana", "telefono": "600111222",
                         %s "notas": "Pelo largo"}""".formatted(DIA, campoEmail)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isString())
                .andReturn().getResponse().getContentAsString();
        return respuesta.replaceAll(".*\"token\"\\s*:\\s*\"([^\"]+)\".*", "$1");
    }

    private List<MimeMessage> enviados(int cuantos) {
        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mail, timeout(5000).times(cuantos)).send(captor.capture());
        // Y ninguno más
        verify(mail, after(300).times(cuantos)).send(any(MimeMessage.class));
        return captor.getAllValues();
    }

    private static String para(MimeMessage m) throws Exception {
        return ((InternetAddress) m.getAllRecipients()[0]).getAddress();
    }

    // Texto y adjuntos (por nombre de fichero) de un mensaje, recorriendo sus partes
    private static Map<String, String> partes(Part parte) throws Exception {
        Map<String, String> resultado = new HashMap<>();
        Object contenido = parte.getContent();
        if (contenido instanceof Multipart multi) {
            for (int i = 0; i < multi.getCount(); i++) {
                BodyPart hija = multi.getBodyPart(i);
                resultado.putAll(partes(hija));
            }
        } else if (parte.getFileName() != null) {
            try (InputStream in = parte.getInputStream()) {
                resultado.put(parte.getFileName(), new String(in.readAllBytes()));
            }
        } else {
            resultado.put("texto", contenido.toString());
        }
        return resultado;
    }

    @Test
    void confirmacionAlClienteYAvisoAlComercio() throws Exception {
        String token = reservar("ana@example.com");

        List<MimeMessage> mensajes = enviados(2);
        MimeMessage cliente = null;
        MimeMessage comercio = null;
        for (MimeMessage m : mensajes) {
            if (para(m).equals("ana@example.com")) cliente = m;
            if (para(m).equals("a@b.es")) comercio = m;
        }
        assertThat(cliente).isNotNull();
        assertThat(comercio).isNotNull();

        assertThat(cliente.getFrom()[0].toString()).contains("citas@peluqueria.test");
        assertThat(cliente.getSubject()).startsWith("Cita confirmada en Peluquería Ejemplo: ").endsWith(" a las 10:00");
        assertThat(((InternetAddress) cliente.getReplyTo()[0]).getAddress()).isEqualTo("a@b.es");
        Map<String, String> delCliente = partes(cliente);
        assertThat(delCliente.get("texto"))
                .contains("Hola, Ana:")
                .contains("Servicios: Corte")
                .contains("Duración: 30 min")
                .contains("https://peluqueria.test/reservar/confirmada/" + token)
                .contains("llama al +34 600 000 000")
                .doesNotContain("Pelo largo");
        assertThat(delCliente.get("cita.ics"))
                .contains("UID:" + token + "@hueco")
                .contains("SUMMARY:Cita en Peluquería Ejemplo");

        assertThat(comercio.getSubject()).startsWith("Nueva cita: Ana, ");
        assertThat(((InternetAddress) comercio.getReplyTo()[0]).getAddress()).isEqualTo("ana@example.com");
        Map<String, String> delComercio = partes(comercio);
        assertThat(delComercio.get("texto"))
                .contains("Teléfono: +34600111222")
                .contains("Email: ana@example.com")
                .contains("Notas: Pelo largo")
                .contains("https://peluqueria.test/agenda");
        assertThat(delComercio).doesNotContainKey("cita.ics");
    }

    @Test
    void sinEmailDelClienteSoloSeAvisaAlComercio() throws Exception {
        reservar(null);

        List<MimeMessage> mensajes = enviados(1);
        assertThat(para(mensajes.getFirst())).isEqualTo("a@b.es");
        assertThat(Arrays.stream(mensajes.getFirst().getReplyTo()).map(Object::toString))
                .noneMatch(d -> d.contains("@example.com"));
    }

    @Test
    void siElEnvioFallaSeReintentaYLaReservaNoFalla() throws Exception {
        doThrow(new MailSendException("servidor caído")).when(mail).send(any(MimeMessage.class));

        reservar("ana@example.com");

        // Dos emails, tres intentos cada uno
        verify(mail, timeout(5000).times(6)).send(any(MimeMessage.class));
        verify(mail, after(300).times(6)).send(any(MimeMessage.class));
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM cita", Integer.class)).isEqualTo(1);
    }

    @Test
    void elCampoTrampaNoMandaNada() throws Exception {
        mockMvc.perform(post("/api/reservas").contentType(MediaType.APPLICATION_JSON).content("""
                        {"servicios": [1], "fecha": "%s", "hora": "10:00", "nombre": "Bot", "telefono": "600111222",
                         "email": "bot@example.com", "website": "http://spam"}""".formatted(DIA)))
                .andExpect(status().isCreated());

        verify(mail, after(500).never()).send(any(MimeMessage.class));
    }
}
