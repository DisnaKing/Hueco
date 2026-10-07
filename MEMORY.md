# MEMORY.md (máx ~50 líneas; consolidar antes de añadir)

## Estado actual
- [2026-10-07] El rediseño "libreta de citas" NO está en el código: `index.css` sigue con la identidad
  anterior (granate, serif, `rounded-2xl`) y `DiasTira.jsx` (sin trackear) no se usa. ¿Se perdió? Preguntar.
- [2026-10-07] Spec `docs/specs/formulario-datos-card-001` (Card en el paso 3 y en `ResumenCita`):
  T1–T12 hechas y verificadas (tests, lint, build, navegador). Sin commitear: falta la revisión del usuario.
- `npx shadcn add` instala el paquete `cn` e importa desde `"cn"`: desinstalarlo y usar `@/lib/utils`.
- `npm install` avisa de 1 vulnerabilidad alta, sin investigar.
- Hecho: reserva pública en 3 pasos, agenda de solo lectura, emails, cancelación por enlace, despliegue con
  Docker Compose y copias nocturnas.
- PRs apiladas sin fusionar (fusionar en orden, cada una tiene base la anterior): #50 Postgres+Flyway (#46),
  #51 restricciones del esquema (#47), #52 despliegue prod (#48), #53 CI (#49, en verde),
  #54 concurrencia sin bloquear el negocio (V7 margen en `ex_cita_solape` + bloqueo por teléfono, en verde).
- `docs/modelo-datos.md` (cambios del usuario sin commitear) aún menciona `findByIdParaReservar` y le falta
  `cita.margen_minutos`.
- Pendiente: comprobar que ningún log escribe datos personales (principio 4 de la constitución).
- Pendiente: protección de rama `main` con checks Backend y Frontend (a mano en GitHub). Resto: `docs/limitaciones.md`.

## Decisiones
- [2026-10-07] `docs/constitution.md`: 6 principios, redactados por Claude y pendientes de revisión del usuario (los 10 anteriores no llegaron a guardarse); prevalecen sobre AGENTS.md y `docs/`.
- [2026-09-30] AGENTS.md solo tiene reglas e índice; el detalle vive en `docs/`, porque duplicarlo lo desfasa.
- [2026-09-30] Cada cita guarda su margen (`margen_minutos`): la restricción no puede leer la config. Gestión guarda 0.

## Problemas conocidos
- Emails y `.ics` dicen "llama para cancelar" aunque ya se puede cancelar por enlace.
- El comercio no recibe aviso al cancelar un cliente.

## Dudas abiertas (para el usuario)
- Texto definitivo de privacidad.
- Clave de la agenda en producción.
- AGENTS.md: cambiar "dentro del bloqueo del negocio" por "dentro del bloqueo por teléfono; la hora la garantiza
  `ex_cita_solape`" (propuesto, sin aplicar).