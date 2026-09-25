package disnaking.Hueco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Hueco {

	public static void main(String[] args) {
		SpringApplication.run(Hueco.class, args);
	}

}
