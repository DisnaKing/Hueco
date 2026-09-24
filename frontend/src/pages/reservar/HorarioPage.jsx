import { Link, Navigate, useSearchParams } from 'react-router'
import { CalendarClock } from 'lucide-react'
import texts from '@/texts/es'
import { useServicios } from '@/api/useFetch'
import { calcularTotales, formatDuracion, formatPrecio } from '@/lib/format'
import { parseIds, serviciosElegidos } from '@/lib/seleccion'
import { Skeleton } from '@/components/ui/skeleton'
import PasoIndicador from '@/components/reservar/PasoIndicador'
import ErrorServicios from '@/components/reservar/ErrorServicios'

// Paso 2, provisional: resumen de lo elegido hasta que exista la elección de fecha y hora
export default function HorarioPage() {
  const [searchParams] = useSearchParams()
  const { data, loading, error } = useServicios()
  const elegidos = serviciosElegidos(parseIds(searchParams.get('servicios')), data ?? [])

  // Sin ningún servicio válido no hay nada que reservar: de vuelta al paso 1
  if (!loading && !error && elegidos.length === 0) {
    return <Navigate to="/reservar" replace />
  }

  const totales = calcularTotales(elegidos)
  const ids = elegidos.map((s) => s.id).join(',')

  return (
    <div className="mx-auto max-w-2xl px-4 pt-6 pb-10 md:pt-10">
      <PasoIndicador paso={2} />
      <h1 className="mt-6 font-display text-3xl font-semibold">{texts.reservar.tituloHorario}</h1>

      {loading && <Skeleton className="mt-6 h-40 rounded-2xl" />}
      {error && (
        <div className="mt-6">
          <ErrorServicios />
        </div>
      )}

      {elegidos.length > 0 && (
        <>
          <section aria-labelledby="tus-servicios" className="mt-6 rounded-2xl border border-line bg-card">
            <div className="flex items-center justify-between gap-4 px-4 pt-4">
              <h2 id="tus-servicios" className="font-semibold">
                {texts.reservar.tusServicios}
              </h2>
              <Link
                to={`/reservar?servicios=${ids}`}
                className="text-sm font-medium text-primary underline underline-offset-4"
              >
                {texts.reservar.cambiarServicios}
              </Link>
            </div>
            <ul className="mt-2 divide-y divide-line">
              {elegidos.map((s) => (
                <li key={s.id} className="flex items-baseline justify-between gap-4 px-4 py-3">
                  <span>{s.nombre}</span>
                  <span className="shrink-0 text-sm text-muted tabular-nums">
                    {formatDuracion(s.duracionMinutos)} · {formatPrecio(s.precio)}
                  </span>
                </li>
              ))}
            </ul>
            <p className="flex items-baseline justify-between gap-4 border-t border-line px-4 py-3 font-semibold">
              <span>{texts.reservar.total}</span>
              <span className="shrink-0 tabular-nums">
                {formatDuracion(totales.duracionMinutos)} · {formatPrecio(totales.precio)}
              </span>
            </p>
          </section>

          <div className="mt-6 flex items-center gap-3 rounded-2xl border border-dashed border-line p-5 text-muted">
            <CalendarClock className="size-6 shrink-0" aria-hidden="true" />
            <p className="font-medium">{texts.reservar.proximamente}</p>
          </div>
        </>
      )}
    </div>
  )
}
