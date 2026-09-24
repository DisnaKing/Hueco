import { Link } from 'react-router'
import { Phone } from 'lucide-react'
import business from '@/business.config'
import texts from '@/texts/es'
import { cn } from '@/lib/utils'
import { Button } from '@/components/ui/button'
import { Skeleton } from '@/components/ui/skeleton'
import EstadoHoy from '@/components/home/EstadoHoy'

const telHref = `tel:${business.telefono.replace(/\s/g, '')}`

// Móvil: la foto de fondo con un degradado y el texto encima.
// Escritorio: dos columnas, texto a la izquierda y foto a la derecha.
// Sin foto configurada, solo texto.
export default function Hero({ negocio }) {
  const foto = business.fotoHero
  // Sobre la foto (solo en móvil) el texto va en claro
  const claro = foto ? 'text-surface md:text-ink' : ''

  return (
    <section
      aria-labelledby="hero-titulo"
      className={cn(
        'relative isolate flex min-h-[70svh] items-end overflow-hidden md:min-h-[70vh] md:items-center',
        !foto && 'items-center',
      )}
    >
      {foto && (
        <>
          <img
            src={foto.src}
            alt={foto.alt}
            width={foto.width}
            height={foto.height}
            fetchPriority="high"
            style={{ objectPosition: foto.posicion }}
            className="absolute inset-0 -z-10 size-full object-cover md:hidden"
          />
          <div
            aria-hidden="true"
            className="absolute inset-0 -z-10 bg-gradient-to-t from-ink/90 via-ink/50 to-ink/10 md:hidden"
          />
        </>
      )}

      <div
        className={cn(
          'mx-auto grid w-full max-w-5xl gap-10 px-4 pt-16 pb-10 md:items-center md:py-12',
          foto && 'md:grid-cols-2',
        )}
      >
        <div className={claro}>
          {negocio.loading ? (
            <>
              <Skeleton className="h-10 w-3/4 max-w-md" />
              <Skeleton className="mt-3 h-5 w-56" />
            </>
          ) : (
            <>
              <h1
                id="hero-titulo"
                className="font-display text-4xl leading-tight font-semibold text-balance md:text-5xl"
              >
                {negocio.data?.eslogan ?? texts.hero.esloganPorDefecto}
              </h1>
              {negocio.data && (
                <div className="mt-3">
                  <EstadoHoy estadoHoy={negocio.data.estadoHoy} className={foto ? 'text-surface/90 md:text-muted' : ''} />
                </div>
              )}
            </>
          )}

          <div className="mt-8 flex flex-wrap gap-3">
            <Button asChild className="h-12 rounded-full px-7 text-base">
              <Link to="/reservar">{texts.hero.reservarCita}</Link>
            </Button>
            <Button
              asChild
              variant="outline"
              className={cn(
                'h-12 rounded-full px-6 text-base',
                foto && 'border-surface/60 bg-transparent text-surface hover:bg-surface/10 hover:text-surface md:border-line md:bg-card md:text-ink md:hover:bg-accent md:hover:text-ink',
              )}
            >
              <a href={telHref}>
                <Phone aria-hidden="true" />
                {texts.hero.llamar}
              </a>
            </Button>
          </div>
        </div>

        {foto && (
          // Misma foto que la de fondo; solo una de las dos se muestra (y se anuncia) según el ancho
          <img
            src={foto.src}
            alt={foto.alt}
            width={foto.width}
            height={foto.height}
            style={{ objectPosition: foto.posicion }}
            className="hidden aspect-[4/3] w-full rounded-3xl object-cover md:block"
          />
        )}
      </div>
    </section>
  )
}
