import { textoEstadoHoy } from '@/lib/format'
import { cn } from '@/lib/utils'

export default function EstadoHoy({ estadoHoy }) {
  const texto = textoEstadoHoy(estadoHoy)
  if (!texto) return null
  const abierto = estadoHoy.estado === 'ABIERTO'

  return (
    <p className="inline-flex items-center gap-2 text-sm font-medium text-muted">
      <span
        aria-hidden="true"
        className={cn('size-2.5 rounded-full', abierto ? 'bg-emerald-500' : 'bg-muted/50')}
      />
      {texto}
    </p>
  )
}
