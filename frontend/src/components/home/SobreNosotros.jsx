import business from '@/business.config'
import texts from '@/texts/es'
import { cn } from '@/lib/utils'
import { Skeleton } from '@/components/ui/skeleton'

export default function SobreNosotros({ negocio }) {
  const fotos = business.fotosSobreNosotros.slice(0, 2)

  return (
    <section id="sobre-nosotros" aria-labelledby="sobre-nosotros-titulo" className="scroll-mt-16 border-t border-line bg-card">
      <div className="mx-auto grid max-w-5xl gap-8 px-4 py-12 md:grid-cols-2 md:items-center md:py-16">
        <div>
          <h2 id="sobre-nosotros-titulo" className="font-display text-2xl font-semibold md:text-3xl">
            {texts.sobreNosotros.titulo}
          </h2>
          {negocio.loading && (
            <div className="mt-4 flex flex-col gap-2">
              <Skeleton className="h-4 w-full" />
              <Skeleton className="h-4 w-full" />
              <Skeleton className="h-4 w-2/3" />
            </div>
          )}
          {negocio.data?.sobreNosotros && (
            <p className="mt-4 leading-relaxed whitespace-pre-line text-ink/85">{negocio.data.sobreNosotros}</p>
          )}
        </div>

        {fotos.length > 0 && (
          <div className="grid grid-cols-2 gap-3">
            {fotos.map((foto) => (
              <img
                key={foto.src}
                src={foto.src}
                alt={foto.alt}
                width={foto.width}
                height={foto.height}
                loading="lazy"
                decoding="async"
                className={cn(
                  'w-full rounded-2xl object-cover',
                  fotos.length === 1 ? 'col-span-2 aspect-[4/3]' : 'aspect-[4/5]',
                )}
              />
            ))}
          </div>
        )}
      </div>
    </section>
  )
}
