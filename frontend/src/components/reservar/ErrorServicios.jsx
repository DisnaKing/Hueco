import { Phone } from 'lucide-react'
import business from '@/business.config'
import texts from '@/texts/es'
import { Button } from '@/components/ui/button'

// Cuando no se puede seguir reservando en la web: se ofrece llamar
export default function ErrorServicios({ mensaje = texts.errores.servicios }) {
  return (
    <div role="alert" className="rounded-2xl border border-line bg-card p-6 text-center">
      <p className="font-medium">{mensaje}</p>
      <Button asChild className="mt-4 h-11 rounded-full px-6">
        <a href={`tel:${business.telefono.replace(/\s/g, '')}`}>
          <Phone aria-hidden="true" />
          {texts.errores.llamar}
        </a>
      </Button>
    </div>
  )
}
