# API

Todas las rutas cuelgan de `/api` y hablan JSON. Fechas en ISO (`2026-10-02`), horas `HH:mm` o `HH:mm:ss`, siempre
en la zona del comercio. Los importes son números decimales en euros.

## Permisos

| Rutas | Acceso |
|---|---|
| `GET /api/negocio`, `GET /api/servicios`, `GET /api/servicios/{id}`, `GET /api/huecos`, `/api/reservas/**` | Público |
| `/api/agenda/**`, `/api/citas/**`, `/api/clientes/**`, `POST /api/servicios/create` | HTTP Basic con la credencial del comercio |

Sin credencial, o con una incorrecta, la respuesta es `401` **sin** cabecera `WWW-Authenticate`, para que el navegador
no abra su propia ventana de login y sea el frontend quien pida la clave. No hay sesión ni cookie: la credencial
va en cada petición, por eso CSRF está desactivado.

## Web pública

### `GET /api/negocio`

Contenido del comercio para la portada. `404` si no hay negocio (instalación sin datos).

```json
{
  "eslogan": "...",
  "sobreNosotros": "...",
  "direccion": "Calle Mayor 1, Madrid",
  "telefono": "910000000",
  "email": "hola@ejemplo.es",
  "horario": [ { "dia": "MONDAY", "tramos": [ { "apertura": "09:00", "cierre": "14:00" } ] } ],
  "redesSociales": [ { "tipo": "INSTAGRAM", "url": "..." } ],
  "testimonios": [ { "autor": "...", "texto": "..." } ],
  "estadoHoy": { "estado": "ABIERTO", "hora": "14:00", "dia": "MONDAY", "fecha": "2026-09-28" },
  "hoy": "MONDAY",
  "cierres": [ { "desde": "2026-10-12", "hasta": "2026-10-12", "motivo": "Festivo" } ]
}
```

`estadoHoy.estado`:

- `ABIERTO`: ahora está dentro de un tramo; `hora` es cuándo cierra.
- `ABRE_HOY`: hoy abre más tarde; `hora` es cuándo abre.
- `CERRADO_HOY`: hoy ya no abre; `dia`, `fecha` y `hora` son la próxima apertura (se salta los cierres puntuales).

`cierres` solo incluye los que tocan el plazo de reserva.

### `GET /api/servicios`

Servicios **activos**, ordenados por `orden`.

```json
[ { "id": 1, "nombre": "Corte", "descripcion": "...", "duracionMinutos": 30, "precio": 15.00, "categoria": "Pelo" } ]
```

`categoria` puede ser `null`: los comercios sin categorías ven una lista simple.

### `GET /api/huecos?servicios=1,3`

Horas libres para la suma de duraciones de esos servicios. Lo que no sea un número se ignora, y los ids repetidos
cuentan una vez. `400` si no queda ningún servicio activo.

```json
[ { "fecha": "2026-09-29", "estado": "LIBRE", "horas": ["09:00", "09:15"] } ]
```

Un elemento por día, desde hoy, durante `hueco.dias-vista` días. `estado`: `LIBRE`, `COMPLETO` o `CERRADO`.
Ver [el cálculo](reglas-y-concurrencia.md#cálculo-de-horas-libres).

### `POST /api/reservas`

Crea una cita como invitado. Cuerpo y proceso en [Flujo de reserva](flujo-reserva.md#paso-3-datos-y-confirmación-datospage).

| Respuesta | Cuerpo |
|---|---|
| `201` | `{ "token": "<uuid>" }` |
| `400` | `{ "errores": { "telefono": "Escribe un teléfono de 9 cifras", ... } }` con claves `nombre`, `telefono`, `email`, `notas`, `servicios`, `fecha`, `hora` |
| `409` | `{ "motivo": "HORA_OCUPADA" }` |
| `429` | `{ "motivo": "LIMITE_TELEFONO" }` o `{ "motivo": "LIMITE_IP" }` |
| `404` | No hay negocio |

### `GET /api/reservas/{token}`

Resumen de la cita, **sin datos personales**. `404` si el token no existe.

```json
{
  "fecha": "2026-10-02",
  "hora": "10:15:00",
  "duracionMinutos": 50,
  "precioTotal": 23.00,
  "estado": "CONFIRMADA",
  "cancelable": true,
  "servicios": [ { "nombre": "Corte", "duracionMinutos": 30, "precio": 15.00 } ]
}
```

### `POST /api/reservas/{token}/cancelar`

Sin cuerpo. `200` con el resumen ya cancelado; `409 { "motivo": "FUERA_DE_PLAZO" }` o
`409 { "motivo": "NO_CANCELABLE" }`; `404` si el token no existe. Ver [reglas](reglas-y-concurrencia.md#cancelación).

### `GET /api/reservas/{token}/cita.ics?nombre=<comercio>`

Descarga `cita.ics` (`text/calendar`), un evento con la hora en UTC, la duración, los servicios y la dirección.
`nombre` es el nombre del comercio para el título, porque el nombre vive en el frontend (`business.config.js`).

## Comercio (con credencial)

### `GET /api/agenda?desde=2026-10-01`

Siete días desde `desde` (sin él, desde hoy). Incluye citas en **todos** los estados, ordenadas por hora.
`400` si la fecha está mal escrita.

```json
[
  {
    "fecha": "2026-10-01",
    "cerrado": false,
    "motivoCierre": null,
    "citas": [
      {
        "inicio": "10:00:00",
        "fin": "10:30:00",
        "estado": "CONFIRMADA",
        "servicios": ["Corte"],
        "total": 15.00,
        "cliente": { "nombre": "Ana", "telefono": "+34600111222", "email": "ana@example.com" },
        "notas": "Pelo largo"
      }
    ]
  }
]
```

`cerrado` es `true` si ese día de la semana no tiene tramos o hay un cierre puntual; en ese caso `motivoCierre` es el
motivo del cierre, o `null` si simplemente no se trabaja. `cliente` es `null` en citas creadas sin cliente.

### Gestión (sin interfaz todavía)

Endpoints heredados de la primera versión. No tienen pantalla en el frontend y **no aplican las reglas de reserva**
(ni horario, ni hueco libre, ni límites):

| Método y ruta | Qué hace |
|---|---|
| `GET /api/citas` | Todas las citas con id, fecha, hora, estado y cliente |
| `GET /api/citas/{id}` | Una cita |
| `POST /api/citas/create` | Crea una cita: `{ fecha, hora, estado, servicios: [ids], clienteId }`. Duración y precio se calculan de los servicios. `400` si falta algún servicio |
| `PATCH /api/citas/{id}` | Cambia `fecha`, `hora` y/o `estado` |
| `DELETE /api/citas/{id}/delete` | Borra la cita |
| `GET /api/clientes` | Clientes con sus citas |
| `GET /api/clientes/{id}` | Un cliente |
| `POST /api/clientes/create` | Crea un cliente |
| `POST /api/servicios/create` | Crea un servicio |
