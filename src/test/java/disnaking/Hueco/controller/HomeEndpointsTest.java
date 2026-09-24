package disnaking.Hueco.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Usa el seed de data.sql
@SpringBootTest
@AutoConfigureMockMvc
class HomeEndpointsTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void serviciosSoloActivosYOrdenados() throws Exception {
        mockMvc.perform(get("/api/servicios"))
                .andExpect(status().isOk())
                // El 6 está inactivo; el 2 tiene orden 1
                .andExpect(jsonPath("$[*].id", contains(2, 1, 3, 4, 5, 7, 8)))
                .andExpect(jsonPath("$[0].nombre").value("Corte hombre"))
                .andExpect(jsonPath("$[0].descripcion").isString())
                .andExpect(jsonPath("$[0].duracionMinutos").value(30))
                .andExpect(jsonPath("$[0].precio").value(15.0))
                .andExpect(jsonPath("$[0].categoria").value("Corte"))
                .andExpect(jsonPath("$[0].activo").doesNotExist())
                .andExpect(jsonPath("$[0].orden").doesNotExist());
    }

    @Test
    void negocioConLaFormaEsperada() throws Exception {
        mockMvc.perform(get("/api/negocio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eslogan").isString())
                .andExpect(jsonPath("$.sobreNosotros").isString())
                .andExpect(jsonPath("$.direccion").isString())
                .andExpect(jsonPath("$.telefono").isString())
                .andExpect(jsonPath("$.email").isString())
                .andExpect(jsonPath("$.horario", hasSize(7)))
                .andExpect(jsonPath("$.horario[0].dia").value("MONDAY"))
                .andExpect(jsonPath("$.horario[0].tramos", hasSize(2)))
                .andExpect(jsonPath("$.horario[0].tramos[0].apertura").value(startsWith("09:00")))
                .andExpect(jsonPath("$.horario[5].tramos", hasSize(1)))
                .andExpect(jsonPath("$.horario[6].dia").value("SUNDAY"))
                .andExpect(jsonPath("$.horario[6].tramos", empty()))
                .andExpect(jsonPath("$.redesSociales[0].tipo").value("INSTAGRAM"))
                .andExpect(jsonPath("$.testimonios[*].autor", contains("Lucía M.", "Javier R.", "Marta G.")))
                .andExpect(jsonPath("$.estadoHoy.estado", in(new String[]{"ABIERTO", "ABRE_HOY", "CERRADO_HOY"})))
                .andExpect(jsonPath("$.estadoHoy.hora").isString())
                .andExpect(jsonPath("$.estadoHoy.dia").isString());
    }
}
