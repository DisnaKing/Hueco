import { ChevronDown, Phone } from 'lucide-react'
import business from '@/business.config'
import texts from '@/texts/es'
import { agruparPorCategoria } from '@/lib/format'
import { Button } from '@/components/ui/button'
import { Skeleton } from '@/components/ui/skeleton'
import ServicioCard from '@/components/home/ServicioCard'

// Con más servicios que esto, solo empieza abierta la primera categoría
const MAX_SERVICIOS_TODO_ABIERTO = 10

function Tarjetas({ servicios, seleccion, onToggle }) {
  return (
    <ul className="grid gap-3 md:grid-cols-2">
      {servicios.map((servicio) => (
        <li key={servicio.id}>
          <ServicioCard servicio={servicio} elegido={seleccion.has(servicio.id)} onToggle={onToggle} />
        </li>
      ))}
    </ul>
  )
}

export default function SelectorServicios({ servicios, loading, error, seleccion, onToggle }) {
  if (loading) {
    return (
      <div className="grid gap-3 md:grid-cols-2" aria-busy="true">
        {Array.from({ length: 4 }, (_, i) => (
          <Skeleton key={i} className="h-28 rounded-2xl" />
        ))}
      </div>
    )
  }

  if (error) {
    return (
      <div role="alert" className="rounded-2xl border border-line bg-card p-6 text-center">
        <p className="font-medium">{texts.errores.servicios}</p>
        <Button asChild className="mt-4 h-11 rounded-full px-6">
          <a href={`tel:${business.telefono.replace(/\s/g, '')}`}>
            <Phone aria-hidden="true" />
            {texts.errores.llamar}
          </a>
        </Button>
      </div>
    )
  }

  const grupos = agruparPorCategoria(servicios)
  if (grupos.length === 1 && grupos[0].categoria === null) {
    return <Tarjetas servicios={servicios} seleccion={seleccion} onToggle={onToggle} />
  }

  const todoAbierto = servicios.length <= MAX_SERVICIOS_TODO_ABIERTO
  return (
    <div className="flex flex-col gap-4">
      {grupos.map((grupo, i) => (
        <details key={grupo.categoria ?? ''} open={todoAbierto || i === 0} className="group">
          <summary className="flex cursor-pointer list-none items-center justify-between rounded-lg py-2 [&::-webkit-details-marker]:hidden">
            <h3 className="font-display text-xl font-semibold">{grupo.categoria ?? texts.hero.otros}</h3>
            <ChevronDown
              aria-hidden="true"
              className="size-5 text-muted transition-transform group-open:rotate-180"
            />
          </summary>
          <div className="pt-2">
            <Tarjetas servicios={grupo.servicios} seleccion={seleccion} onToggle={onToggle} />
          </div>
        </details>
      ))}
    </div>
  )
}
