package disnaking.Hueco.controller;

import disnaking.Hueco.Hueco;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static disnaking.Hueco.Credenciales.COMERCIO;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql({"/limpiar.sql", "/huecos.sql"})
class SeguridadTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void losEndpointsDeGestionPidenLaCredencial() throws Exception {
        for (String ruta : new String[]{"/api/clientes", "/api/citas", "/api/agenda"}) {
            mockMvc.perform(get(ruta))
                    .andExpect(status().isUnauthorized())
                    // Sin WWW-Authenticate el navegador no abre su propio diálogo de login
                    .andExpect(header().doesNotExist("WWW-Authenticate"));
        }
        mockMvc.perform(post("/api/servicios/create").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void conLaCredencialDelComercioSePuedeEntrar() throws Exception {
        mockMvc.perform(get("/api/clientes").with(COMERCIO)).andExpect(status().isOk());
        mockMvc.perform(get("/api/clientes").with(httpBasic("comercio", "otra"))).andExpect(status().isUnauthorized());
    }

    @Test
    void laWebPublicaSigueAbierta() throws Exception {
        mockMvc.perform(get("/api/negocio")).andExpect(status().isOk());
        mockMvc.perform(get("/api/servicios")).andExpect(status().isOk());
        mockMvc.perform(get("/api/servicios/1")).andExpect(status().isOk());
        mockMvc.perform(get("/api/huecos").param("servicios", "1")).andExpect(status().isOk());
        // /api/reservas llega en #30; aquí solo se comprueba que no pide credencial
        mockMvc.perform(get("/api/reservas/no-existe")).andExpect(status().is(not(401)));
    }

    @Test
    void sinClaveDeAgendaLaAplicacionNoArranca() {
        // Como SPRING_PROFILES_ACTIVE=prod en producción: sustituye a dev, que es el perfil por defecto
        assertThatThrownBy(() -> new SpringApplicationBuilder(Hueco.class)
                .run("--spring.profiles.active=prod", "--server.port=0")
                .close())
                .rootCause()
                .hasMessageContaining("hueco.agenda.clave-hash");
    }
}
