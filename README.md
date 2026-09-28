Salon Booking API

A backend system for managing appointments at hair salons, built with Spring Boot. Each appointment can group multiple services (haircut, coloring, treatment), and is linked to a client and a status.

The project follows a clean layered architecture (controller, service, repository, model, DTO) to keep business logic decoupled from persistence and API concerns.

Tech stack: Java, Spring Boot

Status: Work in progress — core appointment domain modeled, currently expanding entity logic.

## Documentación

La documentación completa está en [`docs/`](docs/README.md): arquitectura, flujo de reserva, reglas de reserva y
concurrencia (cómo se evita que dos citas cojan la misma hora), API, modelo de datos, agenda y emails del comercio,
configuración y limitaciones conocidas.

## Desarrollo

- Backend: `./mvnw spring-boot:run` (perfil `dev` por defecto, con la peluquería de ejemplo).
- Frontend: `cd frontend && npm install && npm run dev` (el proxy de Vite manda `/api` al 8080).

## Agenda del comercio

`/agenda` y la gestión de citas y clientes piden usuario y clave (HTTP Basic).

- En desarrollo: usuario `comercio`, clave `hueco-dev`.
- En producción hay que arrancar con `SPRING_PROFILES_ACTIVE=prod` y dar la clave como hash bcrypt en
  `HUECO_AGENDA_CLAVE_HASH` (y, si se quiere otro usuario, `HUECO_AGENDA_USUARIO`). Sin el hash la aplicación no arranca.
- Para generar el hash de una clave:

  ```
  ./mvnw -q spring-boot:run -Dspring-boot.run.arguments=--hash=<clave>
  ```

## Emails de las reservas

Con cada cita nueva se manda la confirmación al cliente (si dejó su email, con el enlace a la cita y el `.ics`)
y un aviso al comercio. Van por SMTP, así que vale cualquier proveedor (Brevo, Amazon SES, Gmail…).
Sin configurar no se manda nada y la reserva funciona igual. Por comercio:

- `SPRING_MAIL_HOST`, `SPRING_MAIL_PORT`, `SPRING_MAIL_USERNAME` y `SPRING_MAIL_PASSWORD`, más
  `SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true` y `SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true`.
- `HUECO_EMAIL_REMITENTE`: un remitente verificado en el proveedor, por ejemplo `Peluquería Ejemplo <citas@peluqueria.es>`.
- `HUECO_EMAIL_NOMBRE_COMERCIO` y `HUECO_EMAIL_URL_WEB` (`https://peluqueria.es`, para los enlaces).
- `HUECO_EMAIL_AVISO_COMERCIO`, opcional: a quién avisar; si falta, al email del negocio.

Si un envío falla se reintenta (`hueco.email.intentos`, 3 por defecto) y al final queda en el log con nivel `ERROR`.
