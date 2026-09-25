package disnaking.Hueco.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

// Acceso del comercio a /agenda y a los endpoints de gestión (hueco.agenda.*)
@ConfigurationProperties(prefix = "hueco.agenda")
public record AgendaProperties(
        String usuario,
        // Hash bcrypt de la clave; se genera con: ./mvnw spring-boot:run -Dspring-boot.run.arguments=--hash=<clave>
        String claveHash
) {
}
