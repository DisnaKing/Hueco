package disnaking.Hueco.controller;

import static disnaking.Hueco.Credenciales.COMERCIO;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql({"/limpiar.sql", "/controladores.sql"})
class ControladoresTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listarClientesMuestraLosNombresDeLosServicios() throws Exception {
        mockMvc.perform(get("/api/clientes").with(COMERCIO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Ana"))
                .andExpect(jsonPath("$[0].citas[0].name", allOf(containsString("Corte"), containsString("Tinte"))))
                .andExpect(jsonPath("$[0].citas[0].name", not(containsString("java.util"))));
    }

    @Test
    void patchDeCitaGuardaLosCambios() throws Exception {
        mockMvc.perform(patch("/api/citas/101").with(COMERCIO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\": \"CONFIRMADA\", \"hora\": \"12:30\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"));

        mockMvc.perform(get("/api/citas/101").with(COMERCIO))
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"))
                .andExpect(jsonPath("$.hora").value(startsWith("12:30")))
                // Lo que no viene en el PATCH no cambia
                .andExpect(jsonPath("$.fecha").value("2026-10-02"));
    }

    @Test
    void crearCitaDevuelveSuLocation() throws Exception {
        mockMvc.perform(post("/api/citas/create").with(COMERCIO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fecha\": \"2026-10-05\", \"hora\": \"09:00\", \"servicios\": [1]}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern("/api/citas/\\d+")));
    }

    @Test
    void crearServicioDevuelveSuLocation() throws Exception {
        mockMvc.perform(post("/api/servicios/create").with(COMERCIO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern("/api/servicios/\\d+")));
    }

    @Test
    void crearClienteDevuelveSuLocation() throws Exception {
        mockMvc.perform(post("/api/clientes/create").with(COMERCIO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Luis\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern("/api/clientes/\\d+")));
    }
}
