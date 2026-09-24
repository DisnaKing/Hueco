import { Phone } from 'lucide-react'
import business from '@/business.config'
import texts from '@/texts/es'
import { Button } from '@/components/ui/button'

// Sin servicios no se puede reservar en la web: se ofrece llamar
export default function ErrorServicios() {
  return (
    <div role="alert" className="rounded-2xl border border-line bg-card p-6 text-center">
      <p className="font-medium">{texts.errores.servicios}</p>
      <Button asChild className="mt-4 h-11 rounded-full px-6">
        <a href={`tel:${business.telefono.replace(/\s/g, '')}`}>
          <Phone aria-hidden="true" />
          {texts.errores.llamar}
        </a>
      </Button>
    </div>
  )
}
