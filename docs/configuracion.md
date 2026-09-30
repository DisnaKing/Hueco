# Configuración y despliegue

Todo se configura en `src/main/resources/application.properties` o, en producción, con variables de entorno
(Spring convierte `hueco.max-citas-por-telefono` en `HUECO_MAXCITASPORTELEFONO`: puntos a guion bajo, sin guiones, en mayúsculas).

## Perfiles

| Perfil | Cuándo | Qué hace |
|---|---|---|
| `dev` | Activo por defecto (`spring.profiles.active=dev`) | Levanta Postgres con `compose.yaml`, carga la peluquería de ejemplo (`db/dev/R__datos_dev.sql`) y fija la clave de la agenda `hueco-dev` (`application-dev.properties`) |
| `prod` | `SPRING_PROFILES_ACTIVE=prod` (lo pone `compose.prod.yaml`) | No carga datos de ejemplo. Hay que dar la clave de la agenda. Toma la IP del cliente de `X-Forwarded-For` cuando llega desde una red interna (`application-prod.properties`) |
| `test` | Tests | Postgres de Testcontainers compartido por todas las clases. Sube el límite por IP a 1000 para que los tests no choquen con él |

## Base de datos

PostgreSQL 17. Las tablas las crean las migraciones de Flyway al arrancar (ver [migraciones](migraciones.md)) y
Hibernate solo comprueba que las entidades cuadran con ellas (`spring.jpa.hibernate.ddl-auto=validate`).

| Dónde | Cómo se conecta |
|---|---|
| Desarrollo | `./mvnw spring-boot:run` levanta el servicio `postgres` de `compose.yaml` (base, usuario y clave `hueco`) y se conecta solo. Hace falta Docker. Los datos se guardan en el volumen `hueco-datos` y sobreviven a reinicios; para empezar de cero, `docker compose down -v` |
| Tests | `jdbc:tc:postgresql:17-alpine:///hueco` en `application-test.properties`: Testcontainers crea un contenedor para toda la ejecución |
| Producción | `SPRING_DATASOURCE_URL` (p. ej. `jdbc:postgresql://db:5432/hueco`), `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD`. Sin ellas la aplicación **no arranca**: no hay base por defecto. `compose.prod.yaml` las pone solo, a partir de `POSTGRES_PASSWORD` en `.env` (ver [despliegue](despliegue.md)) |

## Propiedades `hueco.*`

### General

| Propiedad | Por defecto | |
|---|---|---|
| `hueco.zona-horaria` | `Europe/Madrid` | Zona del comercio. Define "hoy" y "ahora" en todo el backend |

### Reglas de reserva

| Propiedad | Por defecto | |
|---|---|---|
| `hueco.paso-minutos` | `15` | Cada cuántos minutos se ofrece una hora de inicio |
| `hueco.margen-minutos` | `5` | Tiempo tras cada cita antes de la siguiente |
| `hueco.dias-vista` | `30` | Días que se pueden reservar, contando hoy |
| `hueco.antelacion-minutos` | `120` | Antelación mínima para reservar |

### Reserva pública

| Propiedad | Por defecto | |
|---|---|---|
| `hueco.prefijo-telefono` | `+34` | Prefijo con el que se guardan los teléfonos |
| `hueco.max-citas-por-telefono` | `3` | Citas vivas de hoy en adelante por teléfono |
| `hueco.max-reservas-por-ip-hora` | `10` | Reservas por IP en la última hora |
| `hueco.cancelacion-horas-antes` | `24` | Hasta cuántas horas antes se cancela desde la web |

### Emails

| Propiedad | Por defecto | |
|---|---|---|
| `hueco.email.remitente` | vacío | Remitente verificado en el proveedor, p. ej. `Peluquería Ejemplo <citas@peluqueria.es>`. Vacío: no se manda nada |
| `hueco.email.nombre-comercio` | vacío | Nombre en asunto, texto y `.ics` |
| `hueco.email.url-web` | vacío | URL pública de la web, para los enlaces a la cita y a la agenda |
| `hueco.email.aviso-comercio` | vacío | Quién recibe el aviso de cita nueva. Vacío: el email del negocio |
| `hueco.email.intentos` | `3` | Intentos de envío |
| `hueco.email.pausa-reintento` | `30s` | Pausa antes del primer reintento; se duplica en cada uno |

Y el servidor SMTP, con las propiedades estándar de Spring (cualquier proveedor: Brevo, Amazon SES, Gmail…):

```properties
spring.mail.host=smtp-relay.brevo.com
spring.mail.port=587
spring.mail.username=...
spring.mail.password=...          # mejor como SPRING_MAIL_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Agenda

| Propiedad | Por defecto | |
|---|---|---|
| `hueco.agenda.usuario` | `comercio` | Usuario de la agenda |
| `hueco.agenda.clave-hash` | **ninguno** | Hash bcrypt de la clave. Sin él la aplicación no arranca |

## Clave de la agenda

No hay clave por defecto: si falta `hueco.agenda.clave-hash` (variable `HUECO_AGENDA_CLAVE_HASH`), la aplicación
**no arranca** y dice cómo generarla. Es mejor eso que una clave por defecto olvidada en producción.

Para generar el hash de una clave, sin arrancar el servidor:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--hash=<clave>
```

Imprime algo como `$2a$10$...`. Ese valor va en `HUECO_AGENDA_CLAVE_HASH`. La clave en claro no se guarda en ningún sitio.

## Frontend

`frontend/src/business.config.js` fija lo que no viene de la API: nombre del comercio, teléfono de respaldo (para
cuando la API no responde), logo y fotos. Todos los textos están en `frontend/src/texts/es.js`.

Build de producción: `cd frontend && npm run build` deja la web estática en `frontend/dist`. Debe servirse de modo
que `/api/...` llegue al backend (mismo dominio o proxy inverso) y que las rutas del frontend (`/reservar/...`,
`/agenda`) devuelvan `index.html`. Con `compose.prod.yaml` lo hace Caddy (`deploy/Caddyfile`).

## Antes de ir a producción

El procedimiento completo está en [despliegue](despliegue.md). Lista rápida:

- [ ] Dominio apuntando al servidor y puertos 80 y 443 abiertos (Caddy pone el HTTPS solo).
- [ ] `.env` a partir de `.env.example`: `DOMINIO`, `POSTGRES_PASSWORD` y `HUECO_AGENDA_CLAVE_HASH` con una clave nueva.
- [ ] Dar de alta el comercio con [`alta-comercio.sql`](alta-comercio.sql): fila del negocio (`id = 1`), horario y servicios.
- [ ] SMTP y `hueco.email.*` (remitente, nombre) en `.env`. La URL pública la pone `compose.prod.yaml` con `DOMINIO`.
- [ ] Copiar las copias de `./backups` fuera del servidor con regularidad.
- [ ] Texto de privacidad definitivo.
