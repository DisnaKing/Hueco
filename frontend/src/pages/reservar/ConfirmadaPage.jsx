import { useParams } from 'react-router'
import { CalendarPlus, CircleCheck, ExternalLink, MapPin } from 'lucide-react'
import business from '@/business.config'
import texts from '@/texts/es'
import { useFetch, useNegocio } from '@/api/useFetch'
import { formatDuracion, formatPrecio } from '@/lib/format'
import { Button } from '@/components/ui/button'
import { Skeleton } from '@/components/ui/skeleton'
import ResumenCita from '@/components/reservar/ResumenCita'
import ErrorServicios from '@/components/reservar/ErrorServicios'
import NotFoundPage from '@/pages/NotFoundPage'

// Se puede guardar en favoritos: el token no deja adivinar otras citas y el resumen no lleva datos personales
export default function ConfirmadaPage() {
  const { token } = useParams()
  const reserva = useFetch(`/api/reservas/${encodeURIComponent(token)}`)
  const negocio = useNegocio()

  if (reserva.error?.status === 404) return <NotFoundPage />

  const cita = reserva.data
  const telefono = negocio.data?.telefono ?? business.telefono
  const direccion = negocio.data?.direccion
  const ics = `/api/reservas/${encodeURIComponent(token)}/cita.ics?nombre=${encodeURIComponent(business.nombre)}`

  return (
    <div className="mx-auto max-w-2xl px-4 pt-8 pb-12 md:pt-12">
      {reserva.loading && <Skeleton className="h-72 rounded-2xl" />}
      {reserva.error && <ErrorServicios mensaje={texts.reservar.errorEnvio} />}

      {cita && (
        <>
          <div className="flex flex-col items-start gap-3">
            <CircleCheck className="size-12 text-success" aria-hidden="true" />
            <h1 className="font-display text-3xl font-semibold">{texts.reservar.confirmada}</h1>
            <p className="text-muted">{texts.reservar.confirmadaTexto}</p>
          </div>

          {cita.estado === 'CANCELADA' && (
            <p role="status" className="mt-6 rounded-xl border border-destructive/30 bg-destructive/5 p-4 font-medium">
              {texts.reservar.cancelada}
            </p>
          )}

          <div className="mt-6">
            <ResumenCita
              fecha={cita.fecha}
              hora={cita.hora}
              servicios={cita.servicios}
              total={`${formatDuracion(cita.duracionMinutos)} · ${formatPrecio(cita.precioTotal)}`}
            />
          </div>

          <div className="mt-6 flex flex-col gap-3 sm:flex-row">
            <Button asChild className="h-12 rounded-full px-6 text-base">
              <a href={ics} download="cita.ics">
                <CalendarPlus aria-hidden="true" />
                {texts.reservar.anadirCalendario}
              </a>
            </Button>
            {direccion && (
              <Button asChild variant="outline" className="h-12 rounded-full px-6 text-base">
                <a
                  href={`https://www.google.com/maps/dir/?api=1&destination=${encodeURIComponent(direccion)}`}
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  <MapPin aria-hidden="true" />
                  {texts.contacto.comoLlegar}
                  <ExternalLink aria-hidden="true" />
                </a>
              </Button>
            )}
          </div>

          {direccion && <p className="mt-4 text-sm text-muted">{direccion}</p>}
          <p className="mt-6 text-sm">
            {texts.reservar.cambiarLlamando}{' '}
            <a href={`tel:${telefono.replace(/\s/g, '')}`} className="font-medium text-primary underline underline-offset-4">
              {telefono}
            </a>
            .
          </p>
        </>
      )}
    </div>
  )
}
