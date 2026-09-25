import { Link, Navigate, useSearchParams } from 'react-router'
import { UserRoundPen } from 'lucide-react'
import texts from '@/texts/es'
import { useHuecos, useServicios } from '@/api/useFetch'
import { formatDuracion, formatFechaCorta, formatHora, formatPrecio, textoTotales } from '@/lib/format'
import { parseIds, serviciosElegidos } from '@/lib/seleccion'
import { Skeleton } from '@/components/ui/skeleton'
import PasoIndicador from '@/components/reservar/PasoIndicador'
import ErrorServicios from '@/components/reservar/ErrorServicios'

// Paso 3, provisional: resumen de servicios, fecha y hora hasta que existan los datos y la confirmación
export default function DatosPage() {
  const [searchParams] = useSearchParams()
  const ids = parseIds(searchParams.get('servicios')).join(',')
  const fecha = searchParams.get('fecha')
  const hora = searchParams.get('hora')

  const servicios = useServicios()
  const huecos = useHuecos(ids || null)
  const elegidos = serviciosElegidos(parseIds(ids), servicios.data ?? [])

  if (!ids || (!servicios.loading && !servicios.error && elegidos.length === 0)) {
    return <Navigate to="/reservar" replace />
  }
  // Sin fecha u hora libres, de vuelta a elegirlas
  const libre = huecos.data?.some((d) => d.fecha === fecha && d.estado === 'LIBRE' && d.horas.includes(hora))
  if (huecos.data && !libre) {
    return <Navigate to={`/reservar/horario?servicios=${ids}`} replace />
  }

  const cargando = servicios.loading || huecos.loading

  return (
    <div className="mx-auto max-w-2xl px-4 pt-6 pb-10 md:pt-10">
      <PasoIndicador paso={3} />
      <h1 className="mt-6 font-display text-3xl font-semibold">{texts.reservar.tituloDatos}</h1>

      {cargando && <Skeleton className="mt-6 h-56 rounded-2xl" />}
      {!cargando && (servicios.error || huecos.error) && (
        <div className="mt-6">
          <ErrorServicios mensaje={texts.reservar.errorHuecos} />
        </div>
      )}

      {!cargando && libre && elegidos.length > 0 && (
        <>
          <section aria-labelledby="resumen" className="mt-6 rounded-2xl border border-line bg-card">
            <h2 id="resumen" className="sr-only">
              {texts.reservar.tusServicios}
            </h2>
            <div className="flex items-baseline justify-between gap-4 px-4 pt-4">
              <p>
                <span className="text-sm text-muted">{texts.reservar.cuando}: </span>
                <span className="font-semibold">
                  {formatFechaCorta(fecha)} · {formatHora(hora)}
                </span>
              </p>
              <Link
                to={`/reservar/horario?servicios=${ids}&fecha=${fecha}&hora=${hora}`}
                className="shrink-0 text-sm font-medium text-primary underline underline-offset-4"
              >
                {texts.reservar.cambiarFecha}
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
              <span className="shrink-0 tabular-nums">{textoTotales(elegidos)}</span>
            </p>
          </section>

          <div className="mt-6 flex items-center gap-3 rounded-2xl border border-dashed border-line p-5 text-muted">
            <UserRoundPen className="size-6 shrink-0" aria-hidden="true" />
            <p className="font-medium">{texts.reservar.proximamenteDatos}</p>
          </div>
        </>
      )}
    </div>
  )
}
