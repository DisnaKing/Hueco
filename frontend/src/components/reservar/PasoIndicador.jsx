import texts from '@/texts/es'
import { cn } from '@/lib/utils'

// "Paso 1 de 3 · Servicios" con una raya por paso
export default function PasoIndicador({ paso }) {
  const { pasos } = texts.reservar
  return (
    <div>
      <p className="text-sm font-medium text-muted">
        {texts.reservar.paso(paso, pasos.length)} · <span className="text-ink">{pasos[paso - 1]}</span>
      </p>
      <ol className="mt-2 flex gap-1.5" aria-hidden="true">
        {pasos.map((nombre, i) => (
          <li key={nombre} className={cn('h-1 flex-1 rounded-full', i < paso ? 'bg-primary' : 'bg-line')} />
        ))}
      </ol>
    </div>
  )
}
