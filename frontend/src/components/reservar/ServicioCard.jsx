import { Check } from 'lucide-react'
import { formatDuracion, formatPrecio } from '@/lib/format'
import { cn } from '@/lib/utils'

export default function ServicioCard({ servicio, elegido, onToggle }) {
  return (
    <button
      type="button"
      aria-pressed={elegido}
      onClick={() => onToggle(servicio.id)}
      className={cn(
        'flex w-full flex-col gap-1 rounded-2xl border-2 bg-card p-4 text-left transition-colors',
        elegido ? 'border-primary' : 'border-line hover:border-ink/25',
      )}
    >
      <span className="flex items-start justify-between gap-3">
        <span className="font-semibold">{servicio.nombre}</span>
        <span
          aria-hidden="true"
          className={cn(
            'flex size-6 shrink-0 items-center justify-center rounded-full border-2 transition-colors',
            elegido ? 'border-primary bg-primary text-primary-contrast' : 'border-line',
          )}
        >
          {elegido && <Check className="size-4" strokeWidth={3} />}
        </span>
      </span>
      {servicio.descripcion && <span className="text-sm text-muted">{servicio.descripcion}</span>}
      <span className="mt-1 text-sm font-medium">
        {formatDuracion(servicio.duracionMinutos)} · {formatPrecio(servicio.precio)}
      </span>
    </button>
  )
}
