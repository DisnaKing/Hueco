import { useMemo } from 'react'
import { useNavigate, useSearchParams } from 'react-router'
import texts from '@/texts/es'
import { useServicios } from '@/api/useFetch'
import { agruparPorCategoria } from '@/lib/format'
import { anclaCategoria, parseIds, serviciosElegidos } from '@/lib/seleccion'
import { Skeleton } from '@/components/ui/skeleton'
import PasoIndicador from '@/components/reservar/PasoIndicador'
import ChipsCategorias from '@/components/reservar/ChipsCategorias'
import SelectorServicios from '@/components/reservar/SelectorServicios'
import BarraReserva from '@/components/reservar/BarraReserva'
import ErrorServicios from '@/components/reservar/ErrorServicios'

// Paso 1. La selección vive en ?servicios=, así que volver atrás desde el paso 2 la conserva.
export default function ServiciosPage() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const { data, loading, error } = useServicios()

  const servicios = useMemo(() => data ?? [], [data])
  const grupos = useMemo(
    () => agruparPorCategoria(servicios).map((g) => ({ ...g, ancla: anclaCategoria(g.categoria) })),
    [servicios],
  )
  const categorias = useMemo(
    () => grupos.map((g) => ({ ancla: g.ancla, nombre: g.categoria ?? texts.reservar.otros })),
    [grupos],
  )

  // Los ids de la URL que no son servicios activos se descartan sin avisar
  const elegidos = serviciosElegidos(parseIds(searchParams.get('servicios')), servicios)
  const seleccion = new Set(elegidos.map((s) => s.id))
  const idsElegidos = elegidos.map((s) => s.id).join(',')

  function toggle(id) {
    const ids = seleccion.has(id) ? [...seleccion].filter((x) => x !== id) : [...seleccion, id]
    const valor = serviciosElegidos(ids, servicios)
      .map((s) => s.id)
      .join(',')
    // Con navigate y el search escrito a mano la coma no se codifica (?servicios=2,5 y no 2%2C5)
    navigate({ search: valor ? `?servicios=${valor}` : '' }, { replace: true })
  }

  return (
    <>
      <div className="mx-auto max-w-5xl px-4 pt-6 pb-10 md:pt-10">
        <PasoIndicador paso={1} />
        <h1 className="mt-6 font-display text-3xl font-semibold">{texts.reservar.tituloServicios}</h1>
        <p className="mt-1 mb-4 text-sm text-muted">{texts.reservar.ayudaServicios}</p>

        {categorias.length >= 2 && <ChipsCategorias categorias={categorias} />}

        <div className="mt-6">
          {loading && (
            <div className="grid gap-3 md:grid-cols-2" aria-busy="true">
              {Array.from({ length: 4 }, (_, i) => (
                <Skeleton key={i} className="h-28 rounded-2xl" />
              ))}
            </div>
          )}
          {error && <ErrorServicios />}
          {!loading && !error && <SelectorServicios grupos={grupos} seleccion={seleccion} onToggle={toggle} />}
        </div>
      </div>

      <BarraReserva elegidos={elegidos} to={`/reservar/horario?servicios=${idsElegidos}`} />
    </>
  )
}
