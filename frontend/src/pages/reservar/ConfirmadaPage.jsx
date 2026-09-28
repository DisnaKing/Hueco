import { useEffect, useRef, useState } from 'react'
import { Link, useParams } from 'react-router'
import { CalendarPlus, CircleCheck, CircleX, ExternalLink, MapPin } from 'lucide-react'
import business from '@/business.config'
import texts from '@/texts/es'
import { postJson } from '@/api/client'
import { useFetch, useNegocio } from '@/api/useFetch'
import { formatDuracion, formatFechaCorta, formatHora, formatPrecio } from '@/lib/format'
import { Button } from '@/components/ui/button'
import { Skeleton } from '@/components/ui/skeleton'
import ResumenCita from '@/components/reservar/ResumenCita'
import ErrorServicios from '@/components/reservar/ErrorServicios'
import NotFoundPage from '@/pages/NotFoundPage'

function Telefono({ telefono }) {
  return (
    <a href={`tel:${telefono.replace(/\s/g, '')}`} className="font-medium text-primary underline underline-offset-4">
      {telefono}
    </a>
  )
}

// Cancelar desde la web, con confirmación. Fuera de plazo (409), la salida es llamar.
function Cancelar({ token, cita, telefono, onCancelada }) {
  // cerrado | preguntando | enviando | fueraDePlazo | error
  const [paso, setPaso] = useState('cerrado')
  const pregunta = useRef(null)

  useEffect(() => {
    if (paso === 'preguntando') pregunta.current?.focus()
  }, [paso])

  async function cancelar() {
    setPaso('enviando')
    try {
      const respuesta = await postJson(`/api/reservas/${encodeURIComponent(token)}/cancelar`)
      if (respuesta.ok) onCancelada(respuesta.data)
      else setPaso(respuesta.status === 409 ? 'fueraDePlazo' : 'error')
    } catch {
      setPaso('error')
    }
  }

  if (!cita.cancelable || paso === 'fueraDePlazo') {
    return (
      <div className="mt-6">
        {paso === 'fueraDePlazo' && (
          <p role="alert" className="mb-3 rounded-xl border border-primary/30 bg-primary/5 p-4 font-medium">
            {texts.reservar.cancelarFueraDePlazo}
          </p>
        )}
        <p className="text-sm">
          {texts.reservar.cambiarLlamando} <Telefono telefono={telefono} />.
        </p>
      </div>
    )
  }

  if (paso === 'cerrado') {
    return (
      <div className="mt-6 flex flex-col items-start gap-3">
        <Button variant="outline" className="h-11 rounded-full px-6" onClick={() => setPaso('preguntando')}>
          {texts.reservar.cancelarCita}
        </Button>
        <p className="text-sm">
          {texts.reservar.cambiarSoloLlamando} <Telefono telefono={telefono} />.
        </p>
      </div>
    )
  }

  const enviando = paso === 'enviando'
  return (
    <section
      aria-labelledby="pregunta-cancelar"
      className="mt-6 rounded-2xl border border-destructive/30 bg-destructive/5 p-4"
    >
      <h2 id="pregunta-cancelar" ref={pregunta} tabIndex={-1} className="font-semibold outline-none">
        {texts.reservar.cancelarPregunta(formatFechaCorta(cita.fecha), formatHora(cita.hora))}
      </h2>
      <p className="mt-1 text-sm text-muted">{texts.reservar.cancelarAviso}</p>
      {paso === 'error' && (
        <p role="alert" className="mt-3 text-sm font-medium text-destructive">
          {texts.reservar.cancelarError}
        </p>
      )}
      <div className="mt-4 flex flex-col gap-3 sm:flex-row">
        <Button variant="destructive" className="h-11 rounded-full px-6" disabled={enviando} onClick={cancelar}>
          {enviando ? texts.reservar.cancelando : texts.reservar.cancelarSi}
        </Button>
        <Button variant="outline" className="h-11 rounded-full px-6" disabled={enviando} onClick={() => setPaso('cerrado')}>
          {texts.reservar.cancelarNo}
        </Button>
      </div>
    </section>
  )
}

// Se puede guardar en favoritos: el token no deja adivinar otras citas y el resumen no lleva datos personales
export default function ConfirmadaPage() {
  const { token } = useParams()
  const reserva = useFetch(`/api/reservas/${encodeURIComponent(token)}`)
  const negocio = useNegocio()
  // Resumen que devuelve el backend al cancelar
  const [cancelada, setCancelada] = useState(null)

  if (reserva.error?.status === 404) return <NotFoundPage />

  const cita = cancelada ?? reserva.data
  const estaCancelada = cita?.estado === 'CANCELADA'
  const telefono = negocio.data?.telefono ?? business.telefono
  const direccion = negocio.data?.direccion
  const ics = `/api/reservas/${encodeURIComponent(token)}/cita.ics?nombre=${encodeURIComponent(business.nombre)}`

  return (
    <div className="mx-auto max-w-2xl px-4 pt-8 pb-12 md:pt-12">
      {reserva.loading && <Skeleton className="h-72 rounded-2xl" />}
      {reserva.error && <ErrorServicios mensaje={texts.reservar.errorEnvio} />}

      {cita && (
        <>
          {/* role="status": al cancelar, el cambio de título se anuncia */}
          <div role="status" className="flex flex-col items-start gap-3">
            {estaCancelada ? (
              <CircleX className="size-12 text-destructive" aria-hidden="true" />
            ) : (
              <CircleCheck className="size-12 text-success" aria-hidden="true" />
            )}
            <h1 className="font-display text-3xl font-semibold">
              {estaCancelada ? texts.reservar.citaCancelada : texts.reservar.confirmada}
            </h1>
            <p className="text-muted">{estaCancelada ? texts.reservar.canceladaTexto : texts.reservar.confirmadaTexto}</p>
          </div>

          <div className="mt-6">
            <ResumenCita
              fecha={cita.fecha}
              hora={cita.hora}
              servicios={cita.servicios}
              total={`${formatDuracion(cita.duracionMinutos)} · ${formatPrecio(cita.precioTotal)}`}
            />
          </div>

          {estaCancelada ? (
            <Button asChild className="mt-6 h-12 rounded-full px-6 text-base">
              <Link to="/reservar">{texts.reservar.reservarOtra}</Link>
            </Button>
          ) : (
            <>
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
              <Cancelar token={token} cita={cita} telefono={telefono} onCancelada={setCancelada} />
            </>
          )}
        </>
      )}
    </div>
  )
}
