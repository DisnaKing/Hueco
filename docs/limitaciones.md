# Limitaciones conocidas

Lo que hoy no está resuelto, ordenado por importancia.

## Base de datos en memoria

No hay datasource configurado, así que Spring Boot usa **H2 en memoria**. Al reiniciar el servidor se pierden todas
las citas y clientes. En producción hay que configurar una base de datos persistente (PostgreSQL, MySQL o H2 en
fichero) y, a ser posible, migraciones (Flyway o Liquibase) en lugar de que Hibernate cree las tablas.

El bloqueo de reservas (`SELECT ... FOR UPDATE`) funciona igual en PostgreSQL y MySQL.

## Los endpoints de gestión no aplican las reglas

`POST /api/citas/create` y `PATCH /api/citas/{id}` guardan lo que reciben: no comprueban horario, cierres, hueco
libre ni capacidad, y no usan el bloqueo. El comercio podría crear dos citas a la misma hora o fuera de horario.
Hoy no tienen interfaz, pero si se construye un panel de gestión deberían pasar por `CalculadoraHuecos` y por el
mismo bloqueo que `ReservaService`.

## Mismo teléfono, citas solapadas con capacidad > 1

Con `hueco.capacidad=1` es imposible que coincidan dos citas. Con capacidad 2 o más, un mismo teléfono puede
reservar citas que se solapan (en sillones distintos), siempre dentro del límite de 3 citas vivas. Para impedirlo
habría que, dentro del bloqueo, buscar citas vivas de ese teléfono cuyo intervalo se cruce con el de la nueva.

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
