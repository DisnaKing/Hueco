import { Link } from 'react-router'
import texts from '@/texts/es'
import { calcularTotales, formatDuracion, formatPrecio } from '@/lib/format'
import { cn } from '@/lib/utils'

// Los totales son orientativos; en /reservar el backend deberá recalcularlos
export default function BarraReserva({ elegidos }) {
  const visible = elegidos.length > 0
  const { duracionMinutos, precio } = calcularTotales(elegidos)
  const ids = elegidos.map((s) => s.id).join(',')

  return (
    <div
      inert={!visible}
      className={cn(
        'fixed inset-x-0 bottom-0 z-30 bg-primary pb-[env(safe-area-inset-bottom)] text-primary-contrast shadow-[0_-8px_24px_rgb(0_0_0/0.12)]',
        'transition-transform duration-300 ease-out motion-reduce:transition-none',
        visible ? 'translate-y-0' : 'translate-y-full',
      )}
    >
      <div className="mx-auto flex h-20 max-w-5xl items-center justify-between gap-4 px-4">
        <p aria-live="polite" className="min-w-0 text-sm leading-snug">
          <span className="block font-semibold">{texts.barra.servicios(elegidos.length)}</span>
          <span className="opacity-90">
            {formatDuracion(duracionMinutos)} · {formatPrecio(precio)}
          </span>
        </p>
        <Link
          to={`/reservar?servicios=${ids}`}
          className="inline-flex h-11 shrink-0 items-center rounded-full bg-primary-contrast px-6 font-semibold text-primary"
        >
          {texts.barra.verHorarios}
        </Link>
      </div>
    </div>
  )
}
