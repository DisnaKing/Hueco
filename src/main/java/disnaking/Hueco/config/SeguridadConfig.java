package disnaking.Hueco.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

// La web pública (negocio, servicios, huecos y reservas) es abierta. Lo que expone o cambia datos
// de clientes y citas pide la credencial del comercio por HTTP Basic, sin sesión ni cookie:
// al no haber cookie tampoco hace falta CSRF.
@Configuration
public class SeguridadConfig {

    @Bean
    public SecurityFilterChain seguridad(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/citas/**", "/api/clientes/**", "/api/agenda/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/servicios/create").authenticated()
                        .anyRequest().permitAll())
                // 401 sin la cabecera WWW-Authenticate: el login lo pinta el frontend, no el navegador
                .httpBasic(basic -> basic.authenticationEntryPoint((request, response, e) ->
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED)));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Sin clave configurada la aplicación no arranca: mejor eso que una clave por defecto olvidada
    @Bean
    public UserDetailsService comercio(AgendaProperties agenda) {
        if (agenda.claveHash() == null || agenda.claveHash().isBlank()) {
            throw new IllegalStateException("""
                    Falta hueco.agenda.clave-hash (variable HUECO_AGENDA_CLAVE_HASH).
                    Genera el hash de la clave con: ./mvnw spring-boot:run -Dspring-boot.run.arguments=--hash=<clave>""");
        }
        return new InMemoryUserDetailsManager(User.withUsername(agenda.usuario())
                .password(agenda.claveHash())
                .roles("COMERCIO")
                .build());
    }
}
