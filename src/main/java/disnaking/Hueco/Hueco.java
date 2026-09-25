package disnaking.Hueco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Hueco {

	public static void main(String[] args) {
		// --hash=<clave> imprime el hash bcrypt para hueco.agenda.clave-hash, sin arrancar la aplicación
		for (String arg : args) {
			if (arg.startsWith("--hash=")) {
				System.out.println(new BCryptPasswordEncoder().encode(arg.substring("--hash=".length())));
				return;
			}
		}
		SpringApplication.run(Hueco.class, args);
	}

}
