Salon Booking API

A backend system for managing appointments at hair salons, built with Spring Boot. Each appointment can group multiple services (haircut, coloring, treatment), and is linked to a client and a status.

The project follows a clean layered architecture (controller, service, repository, model, DTO) to keep business logic decoupled from persistence and API concerns.

Tech stack: Java, Spring Boot

Status: Work in progress — core appointment domain modeled, currently expanding entity logic.

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
