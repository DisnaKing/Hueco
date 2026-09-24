import { useEffect, useRef, useState } from 'react'
import texts from '@/texts/es'
import { scrollToId } from '@/lib/scroll'
import { cn } from '@/lib/utils'

// Parte de arriba de la pantalla tapada por el header (64 px) y los chips: una categoría
// cuenta como "la que se está viendo" cuando su bloque cruza la franja justo debajo.
const MARGEN_OBSERVADOR = '-130px 0px -55% 0px'
// Tras tocar un chip, el scroll suave no debe cambiar el activo hasta llegar
const BLOQUEO_TRAS_TOQUE_MS = 1000

// Chips fijos bajo el header para saltar entre categorías. El activo sigue al scroll.
export default function ChipsCategorias({ categorias }) {
  const [activa, setActiva] = useState(categorias[0]?.ancla)
  const chips = useRef({})
  const bloqueadoHasta = useRef(0)

  useEffect(() => {
    const visibles = new Set()
    const observer = new IntersectionObserver(
      (entradas) => {
        for (const e of entradas) {
          if (e.isIntersecting) visibles.add(e.target.id)
          else visibles.delete(e.target.id)
        }
        if (performance.now() < bloqueadoHasta.current) return
        // La primera, en el orden de la página, de las que están en la franja
        const primera = categorias.find((c) => visibles.has(c.ancla))
        if (primera) setActiva(primera.ancla)
      },
      { rootMargin: MARGEN_OBSERVADOR },
    )
    for (const c of categorias) {
      const el = document.getElementById(c.ancla)
      if (el) observer.observe(el)
    }

    // La última categoría puede no llegar nunca a la franja: al final de la página, es la activa
    function alFinal() {
      if (performance.now() < bloqueadoHasta.current) return
      const final = window.innerHeight + window.scrollY >= document.documentElement.scrollHeight - 2
      if (final) setActiva(categorias.at(-1).ancla)
    }
    window.addEventListener('scroll', alFinal, { passive: true })

    return () => {
      observer.disconnect()
      window.removeEventListener('scroll', alFinal)
    }
  }, [categorias])

  // Mantiene el chip activo a la vista cuando no caben todos
  useEffect(() => {
    chips.current[activa]?.scrollIntoView({ block: 'nearest', inline: 'nearest' })
  }, [activa])

  function ir(event, ancla) {
    event.preventDefault()
    // event.timeStamp va en la misma escala que performance.now()
    bloqueadoHasta.current = event.timeStamp + BLOQUEO_TRAS_TOQUE_MS
    setActiva(ancla)
    scrollToId(ancla)
    // Para teclado y lectores de pantalla: el foco pasa al título de la categoría
    document.getElementById(`${ancla}-titulo`)?.focus({ preventScroll: true })
  }

  return (
    <nav
      aria-label={texts.reservar.categorias}
      className="sticky top-16 z-20 -mx-4 border-b border-line bg-surface/95 backdrop-blur supports-[backdrop-filter]:bg-surface/80"
    >
      <ul className="flex gap-2 overflow-x-auto px-4 py-2.5 [scrollbar-width:none]">
        {categorias.map((c) => (
          <li key={c.ancla} className="shrink-0">
            <a
              ref={(el) => {
                chips.current[c.ancla] = el
              }}
              href={`#${c.ancla}`}
              onClick={(event) => ir(event, c.ancla)}
              aria-current={activa === c.ancla ? 'true' : undefined}
              className={cn(
                'inline-flex h-9 items-center rounded-full border px-4 text-sm font-medium transition-colors',
                activa === c.ancla
                  ? 'border-primary bg-primary text-primary-contrast'
                  : 'border-line bg-card hover:border-ink/25',
              )}
            >
              {c.nombre}
            </a>
          </li>
        ))}
      </ul>
    </nav>
  )
}
