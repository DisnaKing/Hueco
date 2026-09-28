import { cn } from '@/lib/utils'

// Campo con su etiqueta, ayuda y error asociados (aria-describedby / aria-invalid)
export default function Campo({ id, etiqueta, opcional, ayuda, error, multilinea, className, ...props }) {
  const ayudaId = ayuda ? `${id}-ayuda` : null
  const errorId = error ? `${id}-error` : null
  const Control = multilinea ? 'textarea' : 'input'

  return (
    <div className={className}>
      <label htmlFor={id} className="block text-sm font-medium">
        {etiqueta} {opcional && <span className="font-normal text-muted">{opcional}</span>}
      </label>
      <Control
        id={id}
        aria-invalid={error ? true : undefined}
        aria-describedby={[errorId, ayudaId].filter(Boolean).join(' ') || undefined}
        className={cn(
          'mt-1.5 block w-full rounded-xl border-2 bg-card px-3 text-base outline-none transition-colors focus-visible:border-primary focus-visible:outline-none',
          multilinea ? 'min-h-24 py-2.5' : 'h-12',
          error ? 'border-destructive' : 'border-line',
        )}
        {...props}
      />
      {error && (
        <p id={errorId} className="mt-1.5 text-sm font-medium text-destructive">
          {error}
        </p>
      )}
      {ayuda && !error && (
        <p id={ayudaId} className="mt-1.5 text-sm text-muted">
          {ayuda}
        </p>
      )}
    </div>
  )
}
