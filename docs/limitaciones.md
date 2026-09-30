# Limitaciones conocidas

Lo que hoy no está resuelto, ordenado por importancia.

## Los endpoints de gestión no aplican las reglas

`POST /api/citas/create` y `PATCH /api/citas/{id}` no comprueban horario, cierres, margen ni límites, y no usan el
bloqueo por teléfono. Sus citas se guardan con `margen_minutos = 0`, así que el comercio podría crear una cita fuera
de horario o pegada a otra sin margen. Lo que **no** puede es dejar dos citas vivas solapadas: la base de datos lo
impide (`ex_cita_solape`) y responde `409 HORA_OCUPADA`. Hoy no tienen interfaz, pero si se construye un panel de
gestión deberían pasar por `CalculadoraHuecos` y guardar el margen, como `ReservaService`.

## Cambiar el margen no afecta a las citas ya guardadas

Cada cita guarda en `margen_minutos` el margen con el que se reservó, y `ex_cita_solape` usa ese valor. Si se cambia
`hueco.margen-minutos`, `CalculadoraHuecos` aplica el nuevo a todas las citas al ofrecer horas, pero la base de datos
sigue aplicando el antiguo a las ya guardadas. Al **subirlo** no pasa nada: la comprobación en Java es más estricta.
Al **bajarlo**, la base de datos podría rechazar una hora que la web ofrece pegada a una cita antigua; el cliente
recibiría `409 HORA_OCUPADA`. Si hiciera falta, se puede actualizar a mano el margen de las citas futuras.

## Límite por IP

- **En memoria**: se reinicia con el servidor y no se comparte si hay varias instancias.
- **Detrás de otro proxy**: con `compose.prod.yaml` la IP real llega desde Caddy (`server.forward-headers-strategy=native`
  en el perfil `prod`, ver [despliegue](despliegue.md#ip-del-cliente)). Si se pone delante otro proxy o un CDN
  (Cloudflare…), Caddy vería la IP de ese proxy y el límite de 10 por hora sería para **todo el mundo junto**. Habría
  que declararlo en `trusted_proxies` del `Caddyfile`.
- Una IP compartida (una oficina, un móvil con CGNAT) comparte límite.

## Copias de seguridad en el mismo servidor

El servicio `backup` deja las copias en `./backups`, en el mismo disco que la base de datos. Sacarlas del servidor
es hoy un paso manual (ver [despliegue](despliegue.md#las-copias-tienen-que-salir-del-servidor)). Mejora pendiente:
que el propio servicio las suba con `rclone` a un almacenamiento externo (S3, Backblaze B2, Google Drive…) después
de cada copia.

## Un solo servidor

Todo corre en una máquina, con una instancia del backend. Actualizar deja unos segundos sin servicio mientras
arranca la nueva versión, y si cae el servidor cae la web.

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
