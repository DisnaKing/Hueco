package disnaking.Hueco.controller;

import static disnaking.Hueco.Credenciales.COMERCIO;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Restricciones de las migraciones V3 a V6. Usa los servicios de controladores.sql:
// Corte (id 1, 30 min) y Tinte (id 2, 60 min)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql({"/limpiar.sql", "/controladores.sql"})
class RestriccionesBaseDeDatosTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbc;

    private ResultActions crear(String hora, String estado) throws Exception {
        return mockMvc.perform(post("/api/citas/create").with(COMERCIO)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fecha\": \"2026-10-05\", \"hora\": \"" + hora + "\", \"estado\": \"" + estado
                        + "\", \"servicios\": [1]}"));
    }

    private long id(ResultActions creada) throws Exception {
        String location = creada.andReturn().getResponse().getHeader("Location");
        return Long.parseLong(location.substring(location.lastIndexOf('/') + 1));
    }

    @Test
    void laGestionNoPuedeCrearDosCitasSolapadas() throws Exception {
        crear("09:00", "CONFIRMADA").andExpect(status().isCreated());

        // 9:15 cae dentro de la de 9:00 a 9:30
        crear("09:15", "PENDIENTE")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.motivo").value("HORA_OCUPADA"));
    }

    @Test
    void seguidasSinMargenSiCaben() throws Exception {
        // El margen lo pone CalculadoraHuecos; la base de datos solo impide que se pisen
        crear("09:00", "CONFIRMADA").andExpect(status().isCreated());
        crear("09:30", "CONFIRMADA").andExpect(status().isCreated());
    }

    @Test
    void unaCitaCanceladaNoOcupa() throws Exception {
        crear("09:00", "CANCELADA").andExpect(status().isCreated());
        crear("09:00", "CONFIRMADA").andExpect(status().isCreated());
    }

    @Test
    void moverUnaCitaEncimaDeOtraResponde409() throws Exception {
        crear("09:00", "CONFIRMADA").andExpect(status().isCreated());
        long segunda = id(crear("10:00", "CONFIRMADA").andExpect(status().isCreated()));

        mockMvc.perform(patch("/api/citas/" + segunda).with(COMERCIO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"hora\": \"09:15\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.motivo").value("HORA_OCUPADA"));
    }

    @Test
    void borrarUnaCitaBorraSusServicios() throws Exception {
        long cita = id(crear("09:00", "CONFIRMADA"));

        mockMvc.perform(delete("/api/citas/" + cita + "/delete").with(COMERCIO)).andExpect(status().isNoContent());

        assertThat(jdbc.queryForObject("SELECT count(*) FROM cita_servicio WHERE cita_id = ?", Long.class, cita)).isZero();
    }

    @Test
    void unServicioConCitasNoSePuedeBorrar() throws Exception {
        crear("09:00", "CONFIRMADA").andExpect(status().isCreated());

        assertThatThrownBy(() -> jdbc.update("DELETE FROM servicio WHERE id = 1"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void rechazaValoresQueElDominioNoAdmite() {
        jdbc.update("INSERT INTO negocio (id) VALUES (1)");
        for (String sql : new String[]{
                "UPDATE servicio SET duracion_minutos = 0 WHERE id = 1",
                "UPDATE servicio SET precio = -1 WHERE id = 1",
                "UPDATE servicio SET nombre = NULL WHERE id = 1",
                "INSERT INTO negocio_horario (negocio_id, dia_semana, apertura, cierre) VALUES (1, 'MONDAY', '14:00', '09:00')",
                "INSERT INTO negocio_cierre (negocio_id, desde, hasta) VALUES (1, '2026-10-10', '2026-10-09')"
        }) {
            assertThatThrownBy(() -> jdbc.update(sql)).as(sql).isInstanceOf(DataIntegrityViolationException.class);
        }
    }
}
