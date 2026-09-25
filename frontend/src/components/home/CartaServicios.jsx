import { Link } from 'react-router'
import texts from '@/texts/es'
import { agruparPorCategoria, formatDuracion, formatPrecio } from '@/lib/format'
import { Skeleton } from '@/components/ui/skeleton'

function Fila({ servicio }) {
  return (
    <li>
      <Link
        to={`/reservar?servicios=${servicio.id}`}
        className="-mx-3 block rounded-xl px-3 py-3 transition-colors hover:bg-accent"
      >
        <span className="flex items-baseline gap-2">
          <span className="font-medium">{servicio.nombre}</span>
          <span aria-hidden="true" className="flex-1 translate-y-[-0.3em] border-b-2 border-dotted border-line" />
          <span className="shrink-0 text-sm font-medium tabular-nums">
            {formatDuracion(servicio.duracionMinutos)} · {formatPrecio(servicio.precio)}
          </span>
        </span>
        {servicio.descripcion && <span className="mt-0.5 block text-sm text-muted">{servicio.descripcion}</span>}
      </Link>
    </li>
  )
}

// Carta de precios del home. Cada fila empieza una reserva con ese servicio ya elegido.
// Si falla la carga no se muestra: el hero ya ofrece reservar y llamar.
export default function CartaServicios({ servicios }) {
  if (servicios.error) return null
  const grupos = agruparPorCategoria(servicios.data ?? [])

  return (
    <section id="servicios" aria-labelledby="servicios-titulo" className="scroll-mt-16">
      <div className="mx-auto max-w-5xl px-4 pt-8 pb-12 md:pt-12 md:pb-16">
        <div className="max-w-3xl">
          <h2 id="servicios-titulo" className="font-display text-2xl font-semibold md:text-3xl">
            {texts.carta.titulo}
          </h2>
          <p className="mt-1 text-sm text-muted">{texts.carta.ayuda}</p>

          {servicios.loading ? (
            <div className="mt-6 flex flex-col gap-4" aria-busy="true">
              {Array.from({ length: 5 }, (_, i) => (
                <Skeleton key={i} className="h-12" />
              ))}
            </div>
          ) : (
            <div className="mt-6 flex flex-col gap-8">
              {grupos.map((grupo) => (
                <div key={grupo.categoria ?? ''}>
                  {grupo.categoria && (
                    <h3 className="mb-1 text-sm font-semibold tracking-wide text-muted uppercase">{grupo.categoria}</h3>
                  )}
                  <ul className="divide-y divide-line">
                    {grupo.servicios.map((servicio) => (
                      <Fila key={servicio.id} servicio={servicio} />
                    ))}
                  </ul>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </section>
  )
}
