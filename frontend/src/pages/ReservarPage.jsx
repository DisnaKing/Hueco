import { Link, useSearchParams } from 'react-router'
import { useServicios } from '@/api/useFetch'
import { calcularTotales, formatDuracion, formatPrecio } from '@/lib/format'
import texts from '@/texts/es'

// Provisional: el wizard de fecha y hora queda fuera del plan del home.
// Los totales son orientativos; al reservar, el backend deberá recalcularlos.
export default function ReservarPage() {
  const [searchParams] = useSearchParams()
  const ids = new Set((searchParams.get('servicios') ?? '').split(',').filter(Boolean).map(Number))
  const { data: servicios } = useServicios()
  const elegidos = (servicios ?? []).filter((s) => ids.has(s.id))
  const totales = calcularTotales(elegidos)

  return (
    <section className="mx-auto max-w-2xl px-4 py-10">
      <h1 className="font-display text-3xl">{texts.reservar.titulo}</h1>

      {ids.size === 0 && <p className="mt-4 text-muted">{texts.reservar.sinServicios}</p>}

      {elegidos.length > 0 && (
        <ul className="mt-6 divide-y divide-line rounded-2xl border border-line bg-card">
          {elegidos.map((s) => (
            <li key={s.id} className="flex items-baseline justify-between gap-4 px-4 py-3">
              <span>{s.nombre}</span>
              <span className="shrink-0 text-sm text-muted">
                {formatDuracion(s.duracionMinutos)} · {formatPrecio(s.precio)}
              </span>
            </li>
          ))}
          <li className="flex items-baseline justify-between gap-4 px-4 py-3 font-semibold">
            <span>{texts.reservar.total}</span>
            <span className="shrink-0">
              {formatDuracion(totales.duracionMinutos)} · {formatPrecio(totales.precio)}
            </span>
          </li>
        </ul>
      )}

      <p className="mt-8 font-medium">{texts.reservar.proximamente}</p>
      <Link to="/#servicios" className="mt-6 inline-block text-primary underline underline-offset-4">
        {texts.reservar.elegirServicios}
      </Link>
    </section>
  )
}
