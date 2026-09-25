import { useEffect } from 'react'
import { Link, useOutletContext } from 'react-router'
import texts from '@/texts/es'
import { cn } from '@/lib/utils'

// Barra fija de cada paso: qué llevas elegido y Continuar
export default function BarraReserva({ visible, titulo, detalle, to }) {
  // Layout deja sitio debajo del pie mientras la barra está a la vista
  const setBarraVisible = useOutletContext()?.setBarraVisible
  useEffect(() => {
    setBarraVisible?.(visible)
  }, [visible, setBarraVisible])
  useEffect(() => () => setBarraVisible?.(false), [setBarraVisible])

  return (
    <div
      inert={!visible}
      data-testid="barra-reserva"
      data-visible={visible}
      className={cn(
        'fixed inset-x-0 bottom-0 z-30 bg-primary pb-[env(safe-area-inset-bottom)] text-primary-contrast shadow-[0_-8px_24px] shadow-ink/10',
        'transition-[translate,visibility] duration-300 ease-out motion-reduce:transition-none',
        visible ? 'visible translate-y-0' : 'invisible translate-y-full',
      )}
    >
      <div className="mx-auto flex h-20 max-w-5xl items-center justify-between gap-4 px-4">
        <p aria-live="polite" className="min-w-0 text-sm leading-snug">
          <span className="block font-semibold">{titulo}</span>
          <span className="opacity-90">{detalle}</span>
        </p>
        <Link
          to={to}
          className="inline-flex h-11 shrink-0 items-center rounded-full bg-primary-contrast px-6 font-semibold text-primary"
        >
          {texts.barra.continuar}
        </Link>
      </div>
    </div>
  )
}
