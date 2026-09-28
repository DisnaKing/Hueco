package disnaking.Hueco;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;

// Un Postgres recién creado solo para la clase que lo importa, fuera del compartido
// del perfil test: para comprobar qué deja cada perfil en una base vacía.
@TestConfiguration(proxyBeanMethods = false)
public class PostgresPropio {

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgres() {
        return new PostgreSQLContainer("postgres:17-alpine");
    }
}
