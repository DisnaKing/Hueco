import { Link } from 'react-router'
import texts from '@/texts/es'
import { textoTotales } from '@/lib/format'

// "2 servicios · 1 h 15 min · 40,00 € · Cambiar": el detalle vuelve a salir al confirmar
export default function ResumenServicios({ elegidos }) {
  const ids = elegidos.map((s) => s.id).join(',')
  return (
    <p className="mt-2 text-sm text-muted">
      {texts.barra.servicios(elegidos.length)} · {textoTotales(elegidos)} ·{' '}
      <Link to={`/reservar?servicios=${ids}`} className="font-medium text-primary underline underline-offset-4">
        {texts.reservar.cambiar}
      </Link>
    </p>
  )
}
