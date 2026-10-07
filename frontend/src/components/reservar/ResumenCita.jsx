import { Card, CardContent } from '@/components/ui/card'
import texts from '@/texts/es'
import { formatDuracion, formatFechaCorta, formatHora, formatPrecio } from '@/lib/format'

// Cuándo, servicios y total: lo mismo en el paso 3 y en la confirmación
export default function ResumenCita({ fecha, hora, servicios, total, accion }) {
  // Sin gap ni relleno inferior: la lista y el total llevan su propio relleno y sus líneas van de borde a borde
  return (
    <Card size="sm" role="region" aria-labelledby="resumen-cita" className="gap-0 pb-0">
      <h2 id="resumen-cita" className="sr-only">
        {texts.reservar.tusServicios}
      </h2>
      <CardContent className="flex items-baseline justify-between gap-4">
        <p>
          <span className="text-sm text-muted">{texts.reservar.cuando}: </span>
          <span className="font-semibold">
            {formatFechaCorta(fecha)} · {formatHora(hora)}
          </span>
        </p>
        {accion}
      </CardContent>
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
    </Card>
  )
}
