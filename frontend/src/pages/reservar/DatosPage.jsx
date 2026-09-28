import { useState } from 'react'
import { Link, Navigate, useNavigate, useSearchParams } from 'react-router'
import { Phone } from 'lucide-react'
import business from '@/business.config'
import texts from '@/texts/es'
import { postJson } from '@/api/client'
import { useHuecos, useServicios } from '@/api/useFetch'
import { textoTotales } from '@/lib/format'
import { parseIds, serviciosElegidos } from '@/lib/seleccion'
import { borrarDatosGuardados, guardarDatos, leerDatosGuardados, MAX_EMAIL, MAX_NOMBRE, MAX_NOTAS, validarDatos } from '@/lib/datosReserva'
import { Button } from '@/components/ui/button'
import { Skeleton } from '@/components/ui/skeleton'
import PasoIndicador from '@/components/reservar/PasoIndicador'
import ErrorServicios from '@/components/reservar/ErrorServicios'
import ResumenCita from '@/components/reservar/ResumenCita'
import Campo from '@/components/reservar/Campo'

const CAMPOS = ['nombre', 'telefono', 'email', 'notas']
const telHref = `tel:${business.telefono.replace(/\s/g, '')}`

function enfocarPrimerError(errores) {
  const campo = CAMPOS.find((c) => errores[c])
  if (campo) requestAnimationFrame(() => document.getElementById(`dato-${campo}`)?.focus())
}

// Aviso cuando la reserva no se ha podido hacer, con la salida que corresponda
function Aviso({ estado, ids, fecha, hora }) {
  if (estado === 'ocupada') {
    return (
      <div role="alert" className="rounded-2xl border border-primary/30 bg-primary/5 p-4">
        <p className="font-medium">{texts.reservar.horaOcupada}</p>
        <Button asChild className="mt-3 h-11 rounded-full px-6">
          <Link to={`/reservar/horario?servicios=${ids}&fecha=${fecha}`}>{texts.reservar.elegirOtraHora}</Link>
        </Button>
      </div>
    )
  }
  if (estado === 'cita') {
    return (
      <div role="alert" className="rounded-2xl border border-primary/30 bg-primary/5 p-4">
        <p className="font-medium">{texts.reservar.errores.cita}</p>
        <Link
          to={`/reservar/horario?servicios=${ids}&fecha=${fecha}&hora=${hora}`}
          className="mt-2 inline-block font-medium text-primary underline underline-offset-4"
        >
          {texts.reservar.cambiarFecha}
        </Link>
      </div>
    )
  }
  const mensaje = {
    limiteTelefono: texts.reservar.limiteTelefono,
    limiteIp: texts.reservar.limiteIp,
    error: texts.reservar.errorEnvio,
  }[estado]
  if (!mensaje) return null
  return (
    <div role="alert" className="rounded-2xl border border-primary/30 bg-primary/5 p-4">
      <p className="font-medium">{mensaje}</p>
      <Button asChild variant="outline" className="mt-3 h-11 rounded-full px-6">
        <a href={telHref}>
          <Phone aria-hidden="true" />
          {texts.errores.llamar}
        </a>
      </Button>
    </div>
  )
}

// Paso 3: datos del cliente y confirmación. La reserva la crea el backend (POST /api/reservas),
// que vuelve a comprobar que la hora sigue libre.
export default function DatosPage() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const ids = parseIds(searchParams.get('servicios')).join(',')
  const fecha = searchParams.get('fecha')
  const hora = searchParams.get('hora')

  const servicios = useServicios()
  const huecos = useHuecos(ids || null)
  const elegidos = serviciosElegidos(parseIds(ids), servicios.data ?? [])

  const [datos, setDatos] = useState(leerDatosGuardados)
  const [website, setWebsite] = useState('')
  const [errores, setErrores] = useState({})
  // editando | enviando | ocupada | cita | limiteTelefono | limiteIp | error
  const [estado, setEstado] = useState('editando')

  if (!ids || (!servicios.loading && !servicios.error && elegidos.length === 0)) {
    return <Navigate to="/reservar" replace />
  }
  // Sin fecha u hora libres al entrar, de vuelta a elegirlas
  const libre = huecos.data?.some((d) => d.fecha === fecha && d.estado === 'LIBRE' && d.horas.includes(hora))
  if (huecos.data && !libre && estado === 'editando') {
    return <Navigate to={`/reservar/horario?servicios=${ids}`} replace />
  }

  function cambiar(campo) {
    return (event) => {
      const nuevos = { ...datos, [campo]: event.target.value }
      setDatos(nuevos)
      guardarDatos(nuevos)
      if (errores[campo]) setErrores({ ...errores, [campo]: undefined })
    }
  }

  async function enviar(event) {
    event.preventDefault()
    const invalidos = validarDatos(datos)
    if (Object.keys(invalidos).length > 0) {
      setErrores(invalidos)
      enfocarPrimerError(invalidos)
      return
    }

    setEstado('enviando')
    try {
      const respuesta = await postJson('/api/reservas', {
        servicios: parseIds(ids),
        fecha,
        hora,
        ...datos,
        website,
      })
      if (respuesta.status === 201) {
        borrarDatosGuardados()
        navigate(`/reservar/confirmada/${respuesta.data.token}`, { replace: true })
        return
      }
      if (respuesta.status === 400) {
        const delServidor = respuesta.data?.errores ?? {}
        const deCampos = Object.fromEntries(CAMPOS.filter((c) => delServidor[c]).map((c) => [c, delServidor[c]]))
        setErrores(deCampos)
        enfocarPrimerError(deCampos)
        setEstado(delServidor.fecha || delServidor.hora || delServidor.servicios ? 'cita' : 'editando')
        return
      }
      if (respuesta.status === 409) setEstado('ocupada')
      else if (respuesta.status === 429) {
        setEstado(respuesta.data?.motivo === 'LIMITE_TELEFONO' ? 'limiteTelefono' : 'limiteIp')
      } else setEstado('error')
    } catch {
      setEstado('error')
    }
  }

  const cargando = servicios.loading || huecos.loading
  const enviando = estado === 'enviando'

  return (
    <div className="mx-auto max-w-2xl px-4 pt-6 pb-10 md:pt-10">
      <PasoIndicador paso={3} />
      <h1 className="mt-6 font-display text-3xl font-semibold">{texts.reservar.tituloDatos}</h1>

      {cargando && <Skeleton className="mt-6 h-56 rounded-2xl" />}
      {!cargando && (servicios.error || huecos.error) && (
        <div className="mt-6">
          <ErrorServicios mensaje={texts.reservar.errorHuecos} />
        </div>
      )}

      {!cargando && huecos.data && elegidos.length > 0 && (
        <>
          <div className="mt-6">
            <ResumenCita
              fecha={fecha}
              hora={hora}
              servicios={elegidos}
              total={textoTotales(elegidos)}
              accion={
                <Link
                  to={`/reservar/horario?servicios=${ids}&fecha=${fecha}&hora=${hora}`}
                  className="shrink-0 text-sm font-medium text-primary underline underline-offset-4"
                >
                  {texts.reservar.cambiarFecha}
                </Link>
              }
            />
          </div>

          <form noValidate onSubmit={enviar} className="relative mt-8 flex flex-col gap-5">
            <Campo
              id="dato-nombre"
              etiqueta={texts.reservar.campos.nombre}
              autoComplete="name"
              maxLength={MAX_NOMBRE}
              value={datos.nombre}
              onChange={cambiar('nombre')}
              error={errores.nombre}
              required
            />
            <Campo
              id="dato-telefono"
              etiqueta={texts.reservar.campos.telefono}
              type="tel"
              inputMode="tel"
              autoComplete="tel"
              value={datos.telefono}
              onChange={cambiar('telefono')}
              error={errores.telefono}
              ayuda={texts.reservar.ayudaTelefono}
              required
            />
            <Campo
              id="dato-email"
              etiqueta={texts.reservar.campos.email}
              opcional={texts.reservar.campos.opcional}
              type="email"
              autoComplete="email"
              maxLength={MAX_EMAIL}
              value={datos.email}
              onChange={cambiar('email')}
              error={errores.email}
            />
            <Campo
              id="dato-notas"
              etiqueta={texts.reservar.campos.notas}
              opcional={texts.reservar.campos.opcional}
              multilinea
              maxLength={MAX_NOTAS}
              value={datos.notas}
              onChange={cambiar('notas')}
              error={errores.notas}
              ayuda={texts.reservar.ayudaNotas}
            />

            {/* Campo trampa: fuera de la vista y de los lectores de pantalla; solo lo rellena un bot */}
            <div aria-hidden="true" className="absolute -left-[9999px] size-px overflow-hidden">
              <label>
                Web
                <input
                  name="website"
                  tabIndex={-1}
                  autoComplete="off"
                  value={website}
                  onChange={(e) => setWebsite(e.target.value)}
                />
              </label>
            </div>

            <Aviso estado={estado} ids={ids} fecha={fecha} hora={hora} />

            <div>
              {/* Con la hora ocupada no tiene sentido reintentar: la salida es elegir otra */}
              {estado !== 'ocupada' && (
                <Button
                  type="submit"
                  disabled={enviando}
                  className="h-12 w-full rounded-full text-base md:w-auto md:px-10"
                >
                  {enviando ? texts.reservar.confirmando : texts.reservar.confirmar}
                </Button>
              )}
              <p className="mt-3 text-sm text-muted">
                {texts.reservar.privacidad}{' '}
                <Link to="/privacidad" className="font-medium text-primary underline underline-offset-4">
                  {texts.reservar.verPrivacidad}
                </Link>
              </p>
            </div>
          </form>
        </>
      )}
    </div>
  )
}
