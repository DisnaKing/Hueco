package disnaking.Hueco.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Fuera del perfil dev no se carga la peluquería de ejemplo
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("prod")
class ProdSinSeedTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void sinNegocioResponde404() throws Exception {
        mockMvc.perform(get("/api/negocio")).andExpect(status().isNotFound());
    }

    @Test
    void sinServicios() throws Exception {
        mockMvc.perform(get("/api/servicios"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
