import { textoEstadoHoy } from '@/lib/format'
import { cn } from '@/lib/utils'

export default function EstadoHoy({ estadoHoy, className }) {
  const texto = textoEstadoHoy(estadoHoy)
  if (!texto) return null
  const abierto = estadoHoy.estado === 'ABIERTO'

  return (
    <p className={cn('inline-flex items-center gap-2 text-sm font-medium text-muted', className)}>
      <span
        aria-hidden="true"
        className={cn('size-2.5 rounded-full', abierto ? 'bg-success' : 'bg-current opacity-50')}
      />
      {texto}
    </p>
  )
}
