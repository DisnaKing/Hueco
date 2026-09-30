# Documentación de Hueco

Hueco es una web de reservas para un pequeño comercio con cita previa (peluquería, barbería, estética…).
El cliente reserva como invitado, sin crear cuenta, en tres pasos: servicios, fecha y hora, y sus datos.
El comercio ve sus citas en `/agenda`, protegida con usuario y clave.

Cada instalación sirve a **un solo comercio**. En la base de datos, el negocio es una única fila con `id = 1`.

## Índice

| Documento | Contenido |
|---|---|
| [Arquitectura](arquitectura.md) | Tecnologías, estructura de carpetas, capas y cómo se conectan frontend y backend |
| [Flujo de reserva](flujo-reserva.md) | Paso a paso de una reserva, del navegador a la base de datos, con cada respuesta de error |
| [Reglas y concurrencia](reglas-y-concurrencia.md) | Cálculo de horas libres, cómo se evita que dos citas cojan la misma hora y que un mismo cliente acapare citas |
| [API](api.md) | Todos los endpoints, con sus cuerpos, respuestas y permisos |
| [Modelo de datos](modelo-datos.md) | Entidades, relaciones y tablas (diagrama ER), estados de una cita y qué se guarda de cada cliente |
| [Comercio: agenda, emails y cancelación](comercio.md) | Agenda del comercio, emails de cada reserva y cancelación desde el enlace |
| [Configuración y despliegue](configuracion.md) | Todas las propiedades `hueco.*`, perfiles, clave de la agenda y SMTP |
| [Despliegue](despliegue.md) | Producción con `compose.prod.yaml`: Postgres, backend, Caddy con HTTPS, copias nocturnas y cómo restaurarlas |
| [Migraciones](migraciones.md) | Cómo se crea y evoluciona el esquema con Flyway, y reglas para escribir migraciones |
| [Limitaciones conocidas](limitaciones.md) | Lo que todavía no está resuelto y conviene saber antes de ir a producción |

## Arranque rápido

Hace falta Docker: el backend levanta su PostgreSQL con `compose.yaml`.

```bash
# Backend (puerto 8080, perfil dev con la peluquería de ejemplo; arranca Postgres solo)
./mvnw spring-boot:run

# Frontend (puerto 5173; el proxy de Vite manda /api al 8080)
cd frontend
npm install
npm run dev
```

- Web pública: <http://localhost:5173>
- Agenda del comercio: <http://localhost:5173/agenda>, con usuario `comercio` y clave `hueco-dev`.

## Pruebas

```bash
./mvnw test                          # backend (JUnit + MockMvc, Postgres con Testcontainers: hace falta Docker)
cd frontend && npm test              # frontend (Vitest + Testing Library)
cd frontend && npm run lint          # oxlint
cd frontend && npm run build         # build de producción
```

La CI (`.github/workflows/ci.yml`, GitHub Actions) ejecuta lo mismo en cada PR y en cada push a `main`:
`./mvnw verify` en un job y `npm ci`, lint, tests y build del frontend en otro. Que un PR no se pueda fusionar
sin pasarla se configura en GitHub, con la protección de la rama `main`.
