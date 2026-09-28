package disnaking.Hueco.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

// Los emails de la reserva se mandan en segundo plano: la respuesta al cliente no espera al servidor SMTP
@Configuration
@EnableAsync
public class AsyncConfig {
}
