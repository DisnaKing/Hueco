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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql({"/limpiar.sql", "/huecos.sql"})
class HuecosEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void devuelveUnDiaPorCadaDiaDelPlazo() throws Exception {
        LocalDate hoy = LocalDate.now(ZoneId.of("Europe/Madrid"));

        mockMvc.perform(get("/api/huecos").param("servicios", "1,abc,2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(30)))
                .andExpect(jsonPath("$[0].fecha").value(hoy.toString()))
                .andExpect(jsonPath("$[0].estado", in(new String[]{"LIBRE", "COMPLETO"})))
                // Mañana hay cierre puntual
                .andExpect(jsonPath("$[1].estado").value("CERRADO"))
                .andExpect(jsonPath("$[1].horas", empty()))
                .andExpect(jsonPath("$[2].estado").value("LIBRE"))
                .andExpect(jsonPath("$[2].horas[0]").value("09:00"))
                .andExpect(jsonPath("$[29].fecha").value(hoy.plusDays(29).toString()));
    }

    @Test
    void sinNingunServicioValidoResponde400() throws Exception {
        for (String servicios : new String[]{"", "abc", "999", "2"}) {
            mockMvc.perform(get("/api/huecos").param("servicios", servicios))
                    .andExpect(status().isBadRequest());
        }
    }
}
