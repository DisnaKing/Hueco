import { Link, useSearchParams } from 'react-router'
import texts from '@/texts/es'

// Provisional: el wizard de fecha y hora queda fuera del plan del home
export default function ReservarPage() {
  const [searchParams] = useSearchParams()
  const ids = (searchParams.get('servicios') ?? '').split(',').filter(Boolean)

  return (
    <section className="mx-auto max-w-2xl px-4 py-10">
      <h1 className="font-display text-3xl">{texts.reservar.titulo}</h1>
      {ids.length === 0 ? (
        <p className="mt-4 text-muted">{texts.reservar.sinServicios}</p>
      ) : (
        <p className="mt-4 text-muted">{ids.join(', ')}</p>
      )}
      <p className="mt-8 font-medium">{texts.reservar.proximamente}</p>
      <Link to="/#servicios" className="mt-6 inline-block text-primary underline">
        {texts.reservar.elegirServicios}
      </Link>
    </section>
  )
}
