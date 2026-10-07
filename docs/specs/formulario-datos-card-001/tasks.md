# Tareas: Formulario de datos y resumen de la cita con Card

Spec: `docs/specs/formulario-datos-card-001/spec.md`
Plan: `docs/specs/formulario-datos-card-001/plan.md`

## Tareas

- [x] **T1 — Generar `card.jsx` con el CLI de shadcn**
  - Qué: ejecutar `npx shadcn add card` desde `frontend/`, con el `components.json` del proyecto
    (new-york, JSX). Si el CLI propone sobrescribir otros archivos o tocar `index.css`, decir que no.
  - Archivos: `frontend/src/components/ui/card.jsx`.
  - Depende de: —
  - Hecho cuando: `card.jsx` existe y exporta las siete piezas (Card, CardHeader, CardTitle,
    CardDescription, CardAction, CardContent, CardFooter). El CLI no ha cambiado ningún otro archivo
    (`git status`) y `../../../frontend/package.json` no gana dependencias.

- [x] **T2 — Adaptar la Card a los tokens del proyecto**
  - Qué: quitar la sombra y usar `rounded-2xl`, `border-line`, `bg-card` y `text-ink`. Si el código de T1
    no trae `size` (`default` | `sm`), añadirlo con los espaciados de la web.
  - Archivos: `frontend/src/components/ui/card.jsx`.
  - Depende de: T1
  - Hecho cuando: `card.jsx` no tiene `shadow`, `rounded-xl` ni `text-card-foreground`, acepta
    `size="sm"`, y `npm run lint` y `npm run build` pasan.

- [x] **T3 — `ResumenCita` sobre Card `sm`**
  - Qué: sustituir la `section` hecha a mano por Card `size="sm"` con las mismas props, el `h2` `sr-only`
    y las líneas de la lista y del total de borde a borde.
  - Archivos: `../../../frontend/src/components/reservar/ResumenCita.jsx`.
  - Depende de: T2
  - Hecho cuando: los tests de `DatosPage.test.jsx` y `ConfirmadaPage.test.jsx` siguen en verde sin
    cambiarlos.

- [x] **T4 — Texto del título del formulario**
  - Qué: añadir `reservar.tituloFormulario` con "¿A nombre de quién?".
  - Archivos: `../../../frontend/src/texts/es.js`.
  - Depende de: —
  - Hecho cuando: `es.js` exporta el texto nuevo y `npm run lint` pasa.

- [x] **T5 — Quitar la ayuda del teléfono**
  - Qué: el `Campo` del teléfono deja de recibir `ayuda` y se borra `reservar.ayudaTelefono`. La ayuda de
    notas se queda.
  - Archivos: `../../../frontend/src/pages/reservar/DatosPage.jsx`, `frontend/src/texts/es.js`.
  - Depende de: —
  - Hecho cuando: grep de `ayudaTelefono` en `../../../frontend/src` sin resultados y `npm test` en verde.

- [x] **T6 — Repartir el formulario en las piezas de la Card**
  - Qué: el `form` envuelve una Card `default`. Los cuatro campos y `Aviso` van en `CardContent`. El botón
    (con su regla de hora ocupada) y la privacidad van en `CardFooter`. El campo trampa queda en el `form`,
    fuera de la Card.
  - Archivos: `../../../frontend/src/pages/reservar/DatosPage.jsx`.
  - Depende de: T2
  - Hecho cuando: `h1`, indicador, resumen, esqueleto y `ErrorServicios` siguen fuera y en el mismo orden,
    y los tests actuales de `DatosPage.test.jsx` siguen en verde.

- [x] **T7 — Cabecera con `h2` que da nombre al formulario**
  - Qué: `CardHeader` con `CardTitle` que contiene un `h2` con id y el texto de T4. El `form` lleva
    `aria-labelledby` a ese id. No hay `CardDescription`.
  - Archivos: `../../../frontend/src/pages/reservar/DatosPage.jsx`.
  - Depende de: T4, T6
  - Hecho cuando: en el DOM el `form` tiene nombre accesible "¿A nombre de quién?" y la cabecera no
    tiene descripción.

- [x] **T8 — Test: formulario con nombre accesible y su contenido**
  - Qué: caso nuevo que localiza el formulario por rol y nombre "¿A nombre de quién?". Comprueba que
    dentro están los cuatro campos y el botón "Confirmar cita".
  - Archivos: `../../../frontend/src/pages/reservar/DatosPage.test.jsx`.
  - Depende de: T7
  - Hecho cuando: el caso pasa con `npm test`.

- [x] **T9 — Test: teléfono sin ayuda, notas con ayuda**
  - Qué: caso nuevo que comprueba que el teléfono no tiene `aria-describedby` y que notas sigue con su ayuda.
  - Archivos: `../../../frontend/src/pages/reservar/DatosPage.test.jsx`.
  - Depende de: T5
  - Hecho cuando: el caso pasa con `npm test`.

- [x] **T10 — Verificación automática**
  - Qué: ejecutar `npm test`, `npm run lint` y `npm run build`, y revisar el diff de `package.json`.
  - Archivos: —
  - Depende de: T3, T8, T9
  - Hecho cuando: los tres comandos pasan y `package.json` no tiene dependencias nuevas.

- [x] **T11 — Revisión en el navegador**
  - Qué: con el backend arrancado, recorrer el paso 3 y la confirmación a 360 px y en escritorio.
    Comprobar:
    - sin scroll horizontal y con el mismo orden de lectura;
    - foco en el primer error;
    - aviso de hora ocupada sin botón;
    - esqueleto con el mismo radio;
    - las dos Cards de la misma familia y sin sombra.
  - Archivos: —
  - Depende de: T10
  - Hecho cuando: todos los puntos se cumplen. Si alguno falla, apuntarlo y no dar la tarea por hecha.

- [x] **T12 — Actualizar `../../../MEMORY.md`**
  - Qué: dejar el estado de la spec (implementada o qué falta) y quitar lo obsoleto.
  - Archivos: `../../../MEMORY.md`.
  - Depende de: T11
  - Hecho cuando: `../../../MEMORY.md` refleja el estado real y no pasa de ~50 líneas.

## Orden recomendado

1. T1 → T2.
2. Después en paralelo:
   - T3;
   - T4 y T5, que no dependen de nada y pueden empezar en cualquier momento;
   - T6, que depende de T2.
3. T7, tras T4 y T6.
4. T8 y T9, en paralelo. T9 puede ir justo después de T5.
5. T10 → T11 → T12.

## Cobertura

| Spec | Tareas |
|---|---|
| RF1 piezas de la Card | T1 |
| RF2 sin dependencias nuevas | T1, T10 |
| RF3 tokens, sin sombra | T2, T11 |
| RF4 formulario en Card `default` (cabecera, contenido, pie) | T6, T7 |
| RF5 `h2` que da nombre al formulario | T7, T8 |
| RF6 título en `es.js` | T4 |
| RF7 teléfono sin ayuda, `ayudaTelefono` borrado, notas con ayuda | T5, T9 |
| RF8 `h1`, indicador y resumen fuera y en orden | T6, T11 |
| RF9 campo trampa fuera de la Card | T6 |
| RF10 `ResumenCita` sobre Card `sm` | T3 |
| RF11 en la confirmación solo cambia el resumen | T3, T11 |
| RF12 esqueletos con el mismo radio | T2, T11 |
| RNF accesibilidad | T6, T7, T8, T9 |
| RNF responsive | T6, T11 |
| RNF misma familia visual | T2, T3, T11 |
| CA tests, lint y build | T10 |
| CA navegador | T11 |

Los pasos 1 a 6 del plan quedan cubiertos por T1–T2, T3, T4–T5, T6–T7, T8–T9 y T10–T12.
