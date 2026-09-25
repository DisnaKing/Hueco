package disnaking.Hueco.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.ZoneId;

import static disnaking.Hueco.Credenciales.COMERCIO;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql({"/limpiar.sql", "/huecos.sql", "/agenda.sql"})
class AgendaEndpointTest {

    private static final LocalDate HOY = LocalDate.now(ZoneId.of("Europe/Madrid"));

    @Autowired
    private MockMvc mockMvc;

    @Test
    void sinCredencialDa401() throws Exception {
        mockMvc.perform(get("/api/agenda")).andExpect(status().isUnauthorized());
    }

    @Test
    void sinDesdeEmpiezaHoyYDuraSieteDias() throws Exception {
        mockMvc.perform(get("/api/agenda").with(COMERCIO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(7)))
                .andExpect(jsonPath("$[0].fecha").value(HOY.toString()))
                .andExpect(jsonPath("$[6].fecha").value(HOY.plusDays(6).toString()));
    }

    @Test
    void semanaConCierreYCitasOrdenadas() throws Exception {
        // Desde mañana: el día 0 es el cierre y el día 1 tiene las citas
        mockMvc.perform(get("/api/agenda").param("desde", HOY.plusDays(1).toString()).with(COMERCIO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cerrado").value(true))
                .andExpect(jsonPath("$[0].motivoCierre").value("Festivo"))
                .andExpect(jsonPath("$[1].cerrado").value(false))
                .andExpect(jsonPath("$[1].motivoCierre").value(nullValue()))
                .andExpect(jsonPath("$[1].citas[*].inicio", contains(startsWith("09:30"), startsWith("12:00"), startsWith("17:00"))))
                // Todas las citas, con su estado; el fin es inicio + duración
                .andExpect(jsonPath("$[1].citas[0].estado").value("CANCELADA"))
                .andExpect(jsonPath("$[1].citas[1].fin").value(startsWith("13:00")))
                .andExpect(jsonPath("$[1].citas[1].servicios[0]").value("Corte"))
                .andExpect(jsonPath("$[1].citas[1].total").value(32.0))
                .andExpect(jsonPath("$[1].citas[1].cliente.nombre").value("Ana"))
                .andExpect(jsonPath("$[1].citas[1].cliente.telefono").value("+34600111222"))
                .andExpect(jsonPath("$[1].citas[1].cliente.email").value("ana@example.com"))
                .andExpect(jsonPath("$[1].citas[1].notas").value("Pelo largo"))
                .andExpect(jsonPath("$[1].citas[2].cliente").value(nullValue()))
                .andExpect(jsonPath("$[2].citas", empty()));
    }

    @Test
    void unaFechaMalEscritaDa400() throws Exception {
        mockMvc.perform(get("/api/agenda").param("desde", "ayer").with(COMERCIO)).andExpect(status().isBadRequest());
    }
}
