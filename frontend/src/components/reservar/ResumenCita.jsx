import texts from '@/texts/es'
import { formatDuracion, formatFechaCorta, formatHora, formatPrecio } from '@/lib/format'

// Cuándo, servicios y total: lo mismo en el paso 3 y en la confirmación
export default function ResumenCita({ fecha, hora, servicios, total, accion }) {
  return (
    <section aria-labelledby="resumen-cita" className="rounded-2xl border border-line bg-card">
      <h2 id="resumen-cita" className="sr-only">
        {texts.reservar.tusServicios}
      </h2>
      <div className="flex items-baseline justify-between gap-4 px-4 pt-4">
        <p>
          <span className="text-sm text-muted">{texts.reservar.cuando}: </span>
          <span className="font-semibold">
            {formatFechaCorta(fecha)} · {formatHora(hora)}
          </span>
        </p>
        {accion}
      </div>
      <ul className="mt-2 divide-y divide-line">
        {servicios.map((s) => (
          <li key={s.nombre} className="flex items-baseline justify-between gap-4 px-4 py-3">
            <span>{s.nombre}</span>
            <span className="shrink-0 text-sm text-muted tabular-nums">
              {formatDuracion(s.duracionMinutos)} · {formatPrecio(s.precio)}
            </span>
          </li>
        ))}
      </ul>
      <p className="flex items-baseline justify-between gap-4 border-t border-line px-4 py-3 font-semibold">
        <span>{texts.reservar.total}</span>
        <span className="shrink-0 tabular-nums">{total}</span>
      </p>
    </section>
  )
}
