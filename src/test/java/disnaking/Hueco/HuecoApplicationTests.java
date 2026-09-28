package disnaking.Hueco;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

// Perfil dev por defecto: las migraciones y la peluquería de ejemplo cargan en un Postgres vacío
@SpringBootTest
@Import(PostgresPropio.class)
class HuecoApplicationTests {

	@Autowired
	private JdbcTemplate jdbc;

	@Test
	void cargaLaPeluqueriaDeEjemplo() {
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM negocio", Integer.class)).isEqualTo(1);
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM servicio", Integer.class)).isEqualTo(8);
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM cita", Integer.class)).isEqualTo(9);
	}

}
