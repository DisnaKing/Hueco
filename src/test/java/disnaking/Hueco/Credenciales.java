package disnaking.Hueco;

import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

// Credencial del comercio en los tests (application-test.properties: clave hueco-dev)
public final class Credenciales {

    public static final RequestPostProcessor COMERCIO = httpBasic("comercio", "hueco-dev");

    private Credenciales() {
    }
}
