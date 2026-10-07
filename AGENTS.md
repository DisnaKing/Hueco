# AGENTS.md

Hueco: web de reservas para un comercio con cita previa. Un comercio por instalación.
Documentación completa en `docs/` (empieza por `docs/README.md`). No la dupliques aquí.
Principios innegociables en `docs/constitution.md`: prevalecen sobre este archivo.

## Stack
Java 25, Spring Boot 4.1, PostgreSQL 17 + Flyway. Frontend en `frontend/`: React 19, Vite, Tailwind, shadcn/ui.

## Comandos
- Backend: `./mvnw spring-boot:run` (perfil dev, levanta Postgres con Docker)
- Tests backend: `./mvnw test` (Testcontainers, requiere Docker)
- Frontend: `cd frontend && npm run dev | npm test | npm run lint | npm run build`

## Reglas que no se rompen
- **Migraciones**: nunca edites una ya aplicada; crea `V<n>__descripcion.sql`. Todo cambio de entidad lleva su
  migración en el mismo commit. Sin rollback (ver `docs/migraciones.md`).
- **Reservas**: la lógica vive en `ReservaService` (dentro del bloqueo del negocio) y `CalculadoraHuecos`
  (sin acceso a BD). No compruebes disponibilidad por otro camino.
- **Reloj**: usa siempre el `Clock` inyectado (zona del comercio), nunca `LocalDate.now()` ni la zona del servidor.
- **Precio y duración** de la cita se calculan en el backend; nunca se fía del cliente.
- **Datos personales**: no salen por endpoints públicos (`/api/reservas/{token}` es un resumen sin datos personales).
- Los endpoints de gestión (`/api/citas`, `/api/clientes`) **no aplican las reglas de reserva**. No los uses de
  modelo para código nuevo (ver `docs/limitaciones.md`).
- **Textos** de la interfaz: solo en `frontend/src/texts/es.js`. Datos del comercio: `business.config.js`.
- Nombres de restricciones explícitos (`pk_`, `uk_`, `fk_`, `ck_`, `ex_`, `ix_`).
- Rarezas heredadas: `cliente.cliente_id` y `cliente.name` (no `id`/`nombre`). No renombrar sin migración.

## Antes de dar algo por terminado
- Ejecuta los tests que toquen tu cambio (backend y/o frontend) y `npm run lint` si tocaste el frontend.
- Si cambias comportamiento, actualiza el `.md` correspondiente de `docs/`.

## Memoria
- Al empezar, lee `MEMORY.md`. Al terminar una tarea, actualízalo.
- Máximo ~50 líneas: consolida y borra lo obsoleto antes de añadir.
- Solo apunta: estado de la tarea en curso, decisiones (con su porqué), problemas conocidos, dudas para mí.
- Si algo parece una regla permanente, no lo añadas aquí por tu cuenta: propónmelo.
- Si `MEMORY.md` contradice el código, manda el código; corrige la memoria.