import texts from '@/texts/es'

// Se oculta mientras carga, si falla o si no hay testimonios
export default function Opiniones({ testimonios }) {
  if (!testimonios?.length) return null

  return (
    <section aria-labelledby="opiniones-titulo" className="border-t border-line">
      <div className="mx-auto max-w-5xl py-12 md:px-4 md:py-16">
        <h2 id="opiniones-titulo" className="px-4 font-display text-2xl font-semibold md:px-0 md:text-3xl">
          {texts.opiniones.titulo}
        </h2>
        {/* Móvil: deslizamiento horizontal. Escritorio: cuadrícula */}
        <ul
          tabIndex={0}
          aria-labelledby="opiniones-titulo"
          className="mt-6 flex snap-x snap-mandatory scroll-px-4 gap-4 overflow-x-auto px-4 pb-2 md:grid md:grid-cols-3 md:overflow-visible md:px-0"
        >
          {testimonios.map((testimonio) => (
            <li key={testimonio.autor} className="w-[85%] shrink-0 snap-start md:w-auto">
              <figure className="flex h-full flex-col gap-4 rounded-2xl border border-line bg-card p-5">
                <blockquote className="flex-1 leading-relaxed">“{testimonio.texto}”</blockquote>
                <figcaption className="text-sm font-semibold text-muted">{testimonio.autor}</figcaption>
              </figure>
            </li>
          ))}
        </ul>
      </div>
    </section>
  )
}
