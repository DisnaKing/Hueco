# Limitaciones conocidas

Lo que hoy no está resuelto, ordenado por importancia.

## Los endpoints de gestión no aplican las reglas

`POST /api/citas/create` y `PATCH /api/citas/{id}` no comprueban horario, cierres ni margen, y no usan el bloqueo.
El comercio podría crear una cita fuera de horario o pegada a otra sin margen. Lo que **no** puede es dejar dos citas
vivas solapadas: la base de datos lo impide (`ex_cita_solape`) y responde `409 HORA_OCUPADA`. Hoy no tienen interfaz,
pero si se construye un panel de gestión deberían pasar por `CalculadoraHuecos` y por el mismo bloqueo que
`ReservaService`.

## Límite por IP

- **En memoria**: se reinicia con el servidor y no se comparte si hay varias instancias.
- **Ignora proxies**: usa `request.getRemoteAddr()`. Detrás de un proxy inverso o balanceador, todas las peticiones
  tienen la IP del proxy y el límite de 10 por hora sería para **todo el mundo junto**. Solución: configurar
  `server.forward-headers-strategy=native` (o `framework`) y confiar solo en el proxy propio.
- Una IP compartida (una oficina, un móvil con CGNAT) comparte límite.

## El comercio no se entera de las cancelaciones

Cuando un cliente cancela desde la web no se manda ningún email al comercio. Lo ve en la agenda, marcada como cancelada.

## Textos que todavía dicen "llama para cancelar"

El email de confirmación dice "Para cambiarla o cancelarla, llama al …" y la descripción del `.ics` "Para cambiarla o cancelarla: <teléfono>", aunque ya se
puede cancelar desde el enlace de la cita (con más de 24 h de antelación).

## Sin reprogramar

El cliente no puede cambiar la hora de su cita: tiene que cancelar y reservar otra, o llamar.

## Agenda de solo lectura

El comercio ve sus citas pero no puede crear, mover, cancelar ni marcar como completadas desde la interfaz. Los
servicios, el horario y los cierres se cambian directamente en la base de datos.

## Sin recordatorios

No se manda recordatorio antes de la cita.

## Pendiente de decisión humana

- Texto definitivo de la página de privacidad.
- Clave de la agenda en producción.
