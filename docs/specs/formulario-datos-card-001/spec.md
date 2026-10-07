# Formulario de datos y resumen de la cita con Card

## Objetivo
El formulario del paso 3 es una lista de campos sin contenedor. No se ve como un bloque con principio y fin,
y el resumen de la cita es una tarjeta hecha a mano. Pasar a la Card de shadcn/ui da una sola pieza de
tarjeta para el paso 3 y la confirmación, con el aspecto de la "libreta de citas".

## Alcance
- Incluye:
  - Añadir el componente Card de shadcn/ui a `../../../frontend/src/components/ui`.
  - Paso 3 (`DatosPage`): meter el formulario de datos del cliente en una Card con cabecera, contenido y pie.
  - `ResumenCita`: construirlo sobre la Card. El cambio llega al paso 3 y a la página de confirmación.
  - Página de confirmación (`ConfirmadaPage`): usa la Card a través de `ResumenCita`.
- No incluye:
  - Cambios en la validación, el envío, los estados de error o el guardado de datos en `sessionStorage`.
  - Cambios en el backend ni en la API.
  - Los pasos 1 y 2, la agenda y el resto de páginas.
  - Cambiar de Radix a Base UI ni de estilo shadcn (sigue `new-york`).

## Requisitos funcionales
1. Existe `frontend/src/components/ui/card.jsx`, con las piezas de shadcn/ui: Card, CardHeader, CardTitle,
   CardDescription, CardAction, CardContent y CardFooter.
2. La Card no añade dependencias nuevas de ejecución al frontend.
3. La Card usa los tokens del proyecto: fondo `card`, borde `line` y los radios pequeños de la identidad.
   No lleva la sombra por defecto de shadcn.
4. En el paso 3, el formulario entero queda dentro de una sola Card de tamaño `default`:
   - Cabecera: solo el título "¿A nombre de quién?", sin descripción.
   - Contenido: los campos nombre, teléfono, email y notas, y el aviso de error (`Aviso`) cuando lo haya.
   - Pie: el botón "Confirmar cita" y el texto de privacidad con su enlace.
5. El título de la cabecera es un encabezado `h2` y el formulario queda etiquetado por él (nombre accesible).
6. El título nuevo está en `../../../frontend/src/texts/es.js`, no en el componente.
7. El campo teléfono deja de mostrar la ayuda "Te llamaremos solo si hay algún cambio en tu cita.". El
   texto `ayudaTelefono` se borra de `es.js`, porque ya no lo usa nadie. El campo notas mantiene su ayuda.
8. El `h1` de la página ("Tus datos"), el indicador de pasos y el resumen de la cita quedan fuera de la Card
   del formulario y en el mismo orden que ahora.
9. El campo trampa (`website`) sigue fuera de la vista y de los lectores de pantalla, y no altera el
   aspecto de la Card.
10. `ResumenCita` usa la Card de tamaño `sm` (el resumen es una lista compacta, como ahora). Muestra lo
    mismo que ahora: cuándo, la acción opcional (p. ej. "Cambiar fecha"), la lista de servicios con
    duración y precio, y el total. Su encabezado oculto (`sr-only`) se mantiene.
11. En la confirmación, solo el resumen usa la Card. El resto de bloques (acciones, pregunta de cancelar)
    no cambia.
12. Los esqueletos de carga del paso 3 y de la confirmación conservan la forma de la tarjeta que sustituyen
    (mismo radio).

## Requisitos no funcionales
- Accesibilidad: se mantienen las etiquetas, `aria-invalid`, `aria-describedby`, el foco en el primer
  campo con error y `role="alert"` en los avisos.
- Responsive: a 360 px de ancho no hay scroll horizontal. El botón ocupa todo el ancho en móvil y se
  ajusta a su contenido desde `md`, como ahora.
- Aspecto: la Card del formulario y la del resumen se ven de la misma familia (borde, fondo, radio).

## Comportamiento esperado
1. El cliente llega al paso 3 con servicios, fecha y hora válidos.
2. Ve el indicador de pasos, el título "Tus datos" y el resumen de la cita en su propia Card.
3. Debajo hay una segunda Card con el título "¿A nombre de quién?", los campos y, en el pie, el botón
   "Confirmar cita" y la nota de privacidad. El teléfono ya no muestra texto de ayuda.
4. Si envía con errores, los mensajes salen bajo cada campo y el foco va al primero con error, todo dentro
   de la Card.
5. Si la reserva falla (hora ocupada, cita no válida, límite por teléfono o por IP, error genérico), el
   aviso sale en el contenido de la Card, encima del pie.
6. Con la hora ocupada se oculta el botón, como ahora. El pie conserva la nota de privacidad.
7. Al confirmar, la página de confirmación muestra el resumen con la misma Card.
8. Mientras carga, sale un esqueleto con la forma de la tarjeta. Si falla la carga, sale el error de
   siempre (`ErrorServicios`), fuera de cualquier Card.

## Modelo de datos / interfaces
- Componente nuevo `Card` y sus piezas en `@/components/ui/card`, con la API de shadcn/ui
  (`className` en todas; `size` `default` | `sm` en Card).
- El código de partida de `card.jsx` es la versión Radix de la web de shadcn/ui. Lo facilita el usuario:
  hay que pedírselo antes de crear las tareas.
- `ResumenCita` conserva sus props: `fecha`, `hora`, `servicios`, `total`, `accion`.
- `es.js`: un texto nuevo en `reservar` para el título de la Card del formulario. Se borra `ayudaTelefono`.
- Sin cambios en entidades, endpoints ni contratos del backend.

## Criterios de aceptación
- Existe `components/ui/card.jsx` y `package.json` no gana dependencias de ejecución.
- En el paso 3, los cuatro campos, el aviso y el botón están dentro de una sola Card.
- El formulario tiene como nombre accesible el título de la Card (un `h2`).
- El título sale de `es.js` y la cabecera no tiene descripción.
- El campo teléfono no muestra ayuda y `es.js` ya no tiene `ayudaTelefono`.
- `ResumenCita` se ve igual en contenido en el paso 3 y en la confirmación, ahora sobre la Card.
- La Card no tiene sombra y usa el borde, el fondo y el radio de la identidad del proyecto.
- Siguen en verde los tests de `DatosPage`, `ConfirmadaPage` y el resto del frontend (`npm test`).
- `npm run lint` y `npm run build` pasan sin errores.
- Comprobado en el navegador a 360 px y en escritorio: sin scroll horizontal y con el mismo orden de lectura.

## Preguntas abiertas
- Ninguna.

## Decisiones
- [2026-10-07] Cabecera del formulario: solo el título "¿A nombre de quién?". Sin descripción y sin la ayuda
  del teléfono, que decían lo mismo.
- [2026-10-07] Alcance: paso 3 + `ResumenCita`. En la confirmación solo cambia el resumen.
- [2026-10-07] Tamaño: `default` para el formulario y `sm` para el resumen, que ya es más compacto.
