# Plan: Formulario de datos y resumen de la cita con Card

Spec: `spec.md`

## Resumen del enfoque

Se añade `card.jsx` partiendo del código Radix (new-york) de la web de shadcn/ui, que facilita el usuario.
Se adapta a los tokens del proyecto: sin sombra, borde `line`, fondo `card` y el radio de las tarjetas
actuales. Después se usa en dos sitios. Primero en `ResumenCita`, que ya es una tarjeta y cambia de
aspecto en el paso 3 y en la confirmación sin tocar esas páginas. Luego en el formulario de `DatosPage`.
Es un cambio solo de frontend y de presentación: la lógica de envío, validación y errores no se toca.

## Archivos afectados

- `frontend/src/components/ui/card.jsx`: nuevo. Card y sus piezas, adaptadas a los tokens.
- `../../../frontend/src/components/reservar/ResumenCita.jsx`: modificado. Pasa a usar Card `sm` con las mismas props.
- `../../../frontend/src/pages/reservar/DatosPage.jsx`: modificado. El formulario se reparte en cabecera, contenido y
  pie de una Card, y el teléfono pierde su ayuda.
- `../../../frontend/src/texts/es.js`: modificado. Se añade `reservar.tituloFormulario` ("¿A nombre de quién?") y se
  borra `reservar.ayudaTelefono`.
- `../../../frontend/src/pages/reservar/DatosPage.test.jsx`: modificado. Casos nuevos para el nombre accesible del
  formulario y la ausencia de ayuda en el teléfono.
- `ConfirmadaPage.jsx`: no cambia. Recibe el cambio a través de `ResumenCita`.
- `../..`: no cambia. Ningún documento describe estos textos ni el aspecto del paso 3 (comprobado con grep).

## Pasos

1. **Añadir el componente Card**
    - Pedir al usuario el código Radix de la Card y guardarlo en `components/ui/card.jsx`, con el estilo
      de `button.jsx` (`cn`, `data-slot`).
    - Adaptar las clases: quitar `shadow-sm` y usar el radio de las tarjetas actuales (`rounded-2xl`).
      Borde y fondo con los tokens `border-line` y `bg-card`. El texto usa `text-ink` en lugar de
      `text-card-foreground`, porque ese token no existe en `index.css`.
    - Comprobar que el código trae `size` (`default` | `sm`). Si no lo trae, añadirlo con los
      espaciados de la web.
    - Archivos: `frontend/src/components/ui/card.jsx`.
    - Comprobación: `npm run lint` y `npm run build` pasan, y `package.json` no cambia.

2. **`ResumenCita` sobre la Card `sm`**
    - Sustituir la `section` hecha a mano por Card `size="sm"`. Se mantienen `aria-labelledby` con el `h2`
      `sr-only`, la fila "Cuándo" con su acción, la lista de servicios y el total.
    - Las líneas divisorias de la lista y del total siguen de borde a borde. Para eso, esa parte no lleva
      el relleno lateral de la Card.
    - Archivos: `../../../frontend/src/components/reservar/ResumenCita.jsx`.
    - Comprobación: los tests de `DatosPage` y `ConfirmadaPage` siguen en verde. En el navegador, el
      resumen muestra lo mismo en el paso 3 y en la confirmación.

3. **Textos**
    - Añadir `reservar.tituloFormulario` y borrar `reservar.ayudaTelefono`.
    - Archivos: `../../../frontend/src/texts/es.js`.
    - Comprobación: grep sin resultados de `ayudaTelefono` en `../../../frontend/src` (después del paso 4).

4. **Formulario del paso 3 dentro de la Card**
    - El `form` envuelve la Card: el `form` sigue siendo el elemento semántico y la Card es solo visual.
    - `CardHeader` con `CardTitle` que contiene un `h2` con id. El `form` lleva `aria-labelledby` a ese id.
      No hay `CardDescription`.
    - `CardContent`: los cuatro `Campo` y `Aviso`. El teléfono ya no recibe `ayuda`.
    - `CardFooter`: el botón (todo el ancho en móvil y ajustado desde `md`) y el texto de privacidad. Se
      mantiene la regla de ocultar el botón con la hora ocupada.
    - El campo trampa se queda dentro del `form` pero fuera de la Card, con su posición absoluta respecto
      al `form`.
    - El `h1`, el `PasoIndicador`, el resumen, el esqueleto y `ErrorServicios` siguen fuera, en el mismo orden.
    - Archivos: `../../../frontend/src/pages/reservar/DatosPage.jsx`.
    - Comprobación: tests del paso 5 y `npm run lint`.

5. **Tests del paso 3**
    - Añadir dos casos:
      - Existe un formulario con nombre accesible "¿A nombre de quién?" (`getByRole('form', { name })`)
        con los cuatro campos y el botón dentro.
      - El teléfono no tiene `aria-describedby` (no hay ayuda) y la ayuda de notas sigue.
    - Archivos: `../../../frontend/src/pages/reservar/DatosPage.test.jsx`.
    - Comprobación: `npm test` en verde.

6. **Verificación final**
    - Ejecutar `npm test`, `npm run lint` y `npm run build`.
    - Revisar en el navegador con el backend arrancado, a 360 px y en escritorio: no hay scroll
      horizontal, el orden de lectura es el mismo, el error de validación sale con foco, el aviso de hora
      ocupada oculta el botón y la confirmación muestra el resumen.
    - Comprobar que las dos Cards (resumen y formulario) se ven de la misma familia.
    - Actualizar `../../../MEMORY.md`.

## Dependencias entre pasos

- 1 bloquea a 2 y 4.
- 2 y 3 son independientes entre sí y pueden hacerse en paralelo tras el 1.
- 4 depende de 1 y 3.
- 5 depende de 4.
- 6 va al final.

## Riesgos y decisiones técnicas

- **La identidad "libreta" de `../../../MEMORY.md` no está en el código.** `index.css` no tiene verde ni Schibsted
  Grotesk ni token de radio, y las tarjetas usan `rounded-2xl`. Se manda el código: la Card toma
  `rounded-2xl`. Así cumple "misma familia" y "mismo radio que el esqueleto" sin tocar los esqueletos. Si
  el rediseño se recupera, basta con cambiar el radio en `card.jsx`. Hay que corregir `../../../MEMORY.md`.
- **El `form` envuelve la Card, y no al revés.** Meter el `form` dentro de la Card rompería el `gap` entre
  cabecera, contenido y pie. Usar `display: contents` en el `form` se descarta por el riesgo de perder su
  rol accesible.
- **`CardTitle` de shadcn es un `div`.** Para tener un `h2` real, el `h2` va dentro de `CardTitle`. Se
  descarta cambiar `CardTitle` en `card.jsx` para no alejarse del código de shadcn.
- **Relleno de la Card frente a las líneas de `ResumenCita`.** Las líneas de la lista van de borde a
  borde. Si la Card `sm` aplica relleno lateral a todo, esa zona necesita anularlo. Hay que revisarlo en
  el navegador.
- **Código de la Card.** Depende del que facilite el usuario. Si no trae `size` o `CardAction`, se
  completa a partir de la web, sin dependencias nuevas.

## Estrategia de pruebas

- **Unitarias / componente (Vitest + Testing Library), en `DatosPage.test.jsx`:**
  - El formulario tiene nombre accesible "¿A nombre de quién?" y contiene los cuatro campos y el botón.
  - El teléfono no tiene ayuda; las notas sí.
  - Los tests actuales (validación, foco en el primer error, 409, 429, 400, éxito) siguen en verde sin
    cambios: prueban que la lógica no se ha tocado.
- **`ConfirmadaPage.test.jsx`:** sin cambios. Debe seguir en verde con el nuevo `ResumenCita`.
- **Estáticas:** `npm run lint` y `npm run build`. `package.json` sin dependencias nuevas.
- **Manual en navegador:** a 360 px y en escritorio, sin scroll horizontal. Hay que ver la sombra, el
  radio y el borde, el orden de lectura, el esqueleto y los estados de error.

## Cobertura de la spec

| Requisito de la spec | Paso |
|---|---|
| RF1 `card.jsx` con todas las piezas | 1 |
| RF2 sin dependencias nuevas | 1, 6 |
| RF3 tokens del proyecto, sin sombra | 1, 6 |
| RF4 formulario en una Card `default` (cabecera, contenido, pie) | 4 |
| RF5 título `h2` que da nombre al formulario | 4, 5 |
| RF6 título en `es.js` | 3 |
| RF7 teléfono sin ayuda y `ayudaTelefono` borrado; notas con ayuda | 3, 4, 5 |
| RF8 `h1`, indicador y resumen fuera y en el mismo orden | 4, 6 |
| RF9 campo trampa oculto y sin afectar a la Card | 4 |
| RF10 `ResumenCita` sobre Card `sm`, mismo contenido | 2 |
| RF11 en la confirmación solo cambia el resumen | 2, 6 |
| RF12 esqueletos con el mismo radio | 1 (`rounded-2xl`), 6 |
| RNF accesibilidad | 4, 5 |
| RNF responsive | 4, 6 |
| RNF misma familia visual | 1, 2, 6 |
| CA tests, lint y build en verde | 5, 6 |
| CA comprobado en navegador | 6 |
