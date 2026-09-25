package disnaking.Hueco.controller;

import static disnaking.Hueco.Credenciales.COMERCIO;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Usa los servicios de controladores.sql: Corte (30 min, 15 €) y Tinte (60 min, 32 €)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql({"/limpiar.sql", "/controladores.sql"})
class CitaTotalesTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbc;

    private String crear(String servicios) throws Exception {
        return mockMvc.perform(post("/api/citas/create").with(COMERCIO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fecha\": \"2026-10-05\", \"hora\": \"09:00\", \"servicios\": " + servicios + "}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
    }

    @Test
    void guardaLaSumaDeDuracionYPrecio() throws Exception {
        String location = crear("[1, 2]");

        mockMvc.perform(get(location).with(COMERCIO))
                .andExpect(jsonPath("$.duracionMinutos").value(90))
                .andExpect(jsonPath("$.precioTotal").value(47.0))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void cambiarUnServicioDespuesNoCambiaLaCita() throws Exception {
        String location = crear("[1, 2]");

        jdbc.update("UPDATE servicio SET duracion_minutos = 999, precio = 999 WHERE id = 1");

        mockMvc.perform(get(location).with(COMERCIO))
                .andExpect(jsonPath("$.duracionMinutos").value(90))
                .andExpect(jsonPath("$.precioTotal").value(47.0));
    }

    @Test
    void sinServiciosOConUnoQueNoExisteResponde400() throws Exception {
        for (String servicios : new String[]{"[]", "[1, 999]"}) {
            mockMvc.perform(post("/api/citas/create").with(COMERCIO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"fecha\": \"2026-10-05\", \"hora\": \"09:00\", \"servicios\": " + servicios + "}"))
                    .andExpect(status().isBadRequest());
        }
    }
}
