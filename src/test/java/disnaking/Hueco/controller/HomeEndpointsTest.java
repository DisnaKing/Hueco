package disnaking.Hueco.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Datos propios en home-endpoints.sql: cambiar el seed de desarrollo no rompe estos tests
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql("/home-endpoints.sql")
class HomeEndpointsTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void serviciosSoloActivosYOrdenados() throws Exception {
        mockMvc.perform(get("/api/servicios"))
                .andExpect(status().isOk())
                // El 3 está inactivo; el 2 tiene orden 1
                .andExpect(jsonPath("$[*].id", contains(2, 1, 4)))
                .andExpect(jsonPath("$[0].nombre").value("Servicio A"))
                .andExpect(jsonPath("$[0].descripcion").value("Descripción A"))
                .andExpect(jsonPath("$[0].duracionMinutos").value(30))
                .andExpect(jsonPath("$[0].precio").value(15.0))
                .andExpect(jsonPath("$[0].categoria").value("Cat 1"))
                .andExpect(jsonPath("$[2].categoria").value(nullValue()))
                .andExpect(jsonPath("$[0].activo").doesNotExist())
                .andExpect(jsonPath("$[0].orden").doesNotExist());
    }

    @Test
    void negocioConLaFormaEsperada() throws Exception {
        mockMvc.perform(get("/api/negocio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eslogan").value("Eslogan de prueba"))
                .andExpect(jsonPath("$.sobreNosotros").value("Texto de prueba"))
                .andExpect(jsonPath("$.direccion").value("Calle Prueba 1"))
                .andExpect(jsonPath("$.telefono").value("+34 600 111 222"))
                .andExpect(jsonPath("$.email").value("prueba@example.com"))
                .andExpect(jsonPath("$.horario", hasSize(7)))
                .andExpect(jsonPath("$.horario[0].dia").value("MONDAY"))
                .andExpect(jsonPath("$.horario[0].tramos", hasSize(2)))
                // Los tramos salen ordenados aunque se insertaran al revés
                .andExpect(jsonPath("$.horario[0].tramos[0].apertura").value(startsWith("09:00")))
                .andExpect(jsonPath("$.horario[5].tramos", hasSize(1)))
                .andExpect(jsonPath("$.horario[6].dia").value("SUNDAY"))
                .andExpect(jsonPath("$.horario[6].tramos", empty()))
                .andExpect(jsonPath("$.redesSociales[0].tipo").value("INSTAGRAM"))
                .andExpect(jsonPath("$.testimonios[*].autor", contains("Primero", "Segundo", "Tercero")))
                .andExpect(jsonPath("$.estadoHoy.estado", in(new String[]{"ABIERTO", "ABRE_HOY", "CERRADO_HOY"})))
                .andExpect(jsonPath("$.estadoHoy.hora").isString())
                .andExpect(jsonPath("$.estadoHoy.dia").isString())
                .andExpect(jsonPath("$.hoy", in(new String[]{
                        "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"})));
    }
}
