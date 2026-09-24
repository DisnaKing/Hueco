import texts from '@/texts/es'
import { Skeleton } from '@/components/ui/skeleton'
import EstadoHoy from '@/components/home/EstadoHoy'
import SelectorServicios from '@/components/home/SelectorServicios'

// El hero es la sección Servicios: el selector está aquí mismo
export default function Hero({ negocio, servicios, seleccion, onToggle }) {
  return (
    <section id="servicios" aria-labelledby="hero-titulo" className="scroll-mt-16">
      <div className="mx-auto max-w-5xl px-4 pt-6 pb-10 md:pt-12">
        {negocio.loading ? (
          <>
            <Skeleton className="h-9 w-3/4 max-w-md" />
            <Skeleton className="mt-3 h-5 w-56" />
          </>
        ) : (
          <>
            <h1 id="hero-titulo" className="font-display text-3xl leading-tight font-semibold text-balance md:text-5xl">
              {negocio.data?.eslogan ?? texts.hero.esloganPorDefecto}
            </h1>
            {negocio.data && (
              <div className="mt-2">
                <EstadoHoy estadoHoy={negocio.data.estadoHoy} />
              </div>
            )}
          </>
        )}

        <h2 className="mt-6 text-lg font-semibold md:mt-10">{texts.hero.tituloServicios}</h2>
        <p className="mb-4 text-sm text-muted">{texts.hero.ayudaServicios}</p>

        <SelectorServicios
          servicios={servicios.data ?? []}
          loading={servicios.loading}
          error={servicios.error}
          seleccion={seleccion}
          onToggle={onToggle}
        />
      </div>
    </section>
  )
}
