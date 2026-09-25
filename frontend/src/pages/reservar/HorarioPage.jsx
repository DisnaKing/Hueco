import { useEffect } from 'react'
import { Navigate, useNavigate, useSearchParams } from 'react-router'
import texts from '@/texts/es'
import { useHuecos, useNegocio, useServicios } from '@/api/useFetch'
import { formatFechaCorta, formatHora, textoTotales } from '@/lib/format'
import { parseIds, serviciosElegidos } from '@/lib/seleccion'
import { scrollToId } from '@/lib/scroll'
import { Skeleton } from '@/components/ui/skeleton'
import PasoIndicador from '@/components/reservar/PasoIndicador'
import ResumenServicios from '@/components/reservar/ResumenServicios'
import CalendarioHuecos from '@/components/reservar/CalendarioHuecos'
import HorasDia from '@/components/reservar/HorasDia'
import BarraReserva from '@/components/reservar/BarraReserva'
import ErrorServicios from '@/components/reservar/ErrorServicios'

// En móvil las horas quedan debajo del calendario: al tocar un día se baja hasta ellas
function bajarAHorasEnMovil() {
  if (window.matchMedia('(min-width: 768px)').matches) return
  requestAnimationFrame(() => {
    const titulo = document.getElementById('horas-titulo')
    if (titulo && titulo.getBoundingClientRect().top > window.innerHeight - 120) scrollToId('horas-titulo')
  })
}

// Paso 2. La fecha y la hora viven en la URL (?servicios=&fecha=&hora=), como la selección del paso 1.
export default function HorarioPage() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const ids = parseIds(searchParams.get('servicios')).join(',')
  const fechaUrl = searchParams.get('fecha')
  const horaUrl = searchParams.get('hora')

  const servicios = useServicios()
  const negocio = useNegocio()
  const huecos = useHuecos(ids || null)

  const elegidos = serviciosElegidos(parseIds(ids), servicios.data ?? [])
  const dias = huecos.data ?? []
  const libres = dias.filter((d) => d.estado === 'LIBRE')

  // Una fecha u hora que ya no está libre (enlace antiguo, recarga) se quita sin avisar
  const diaUrl = libres.find((d) => d.fecha === fechaUrl)
  const horaValida = diaUrl?.horas.includes(horaUrl) ? horaUrl : null
  // Sin fecha válida en la URL se muestra el primer día con huecos; la hora nunca se elige sola
  const dia = diaUrl ?? libres[0]

  const urlInvalida = huecos.data && ((fechaUrl && !diaUrl) || (horaUrl && !horaValida))
  useEffect(() => {
    if (!urlInvalida) return
    const params = new URLSearchParams({ servicios: ids })
    if (diaUrl) params.set('fecha', diaUrl.fecha)
    navigate({ search: `?${params.toString().replace(/%2C/g, ',')}` }, { replace: true })
  }, [urlInvalida, ids, diaUrl, navigate])

  function irA(fecha, hora) {
    const search = `?servicios=${ids}&fecha=${fecha}${hora ? `&hora=${hora}` : ''}`
    navigate({ search }, { replace: true })
  }

  function elegirDia(fecha) {
    if (fecha === dia?.fecha) return
    irA(fecha, null)
    bajarAHorasEnMovil()
  }

  if (!ids || (!servicios.loading && !servicios.error && elegidos.length === 0)) {
    return <Navigate to="/reservar" replace />
  }

  const tramos = dia && negocio.data?.horario.find((h) => h.dia === diaSemana(dia.fecha))?.tramos
  const cargando = huecos.loading || servicios.loading

  return (
    <>
      <div className="mx-auto max-w-5xl px-4 pt-6 pb-10 md:pt-10">
        <PasoIndicador paso={2} />
        <h1 className="mt-6 font-display text-3xl font-semibold">{texts.reservar.tituloHorario}</h1>
        {elegidos.length > 0 && <ResumenServicios elegidos={elegidos} />}

        <div className="mt-6">
          {cargando && (
            <div className="grid gap-8 md:grid-cols-[auto_1fr]" aria-busy="true">
              <Skeleton className="h-80 w-full rounded-2xl md:w-80" />
              <Skeleton className="h-48 rounded-2xl" />
            </div>
          )}

          {!cargando && (huecos.error || servicios.error) && <ErrorServicios mensaje={texts.reservar.errorHuecos} />}

          {!cargando && huecos.data && !dia && <ErrorServicios mensaje={texts.reservar.sinHuecos(dias.length)} />}

          {!cargando && huecos.data && dia && (
            <div className="grid gap-8 md:grid-cols-[auto_1fr] md:gap-12">
              <CalendarioHuecos dias={dias} fechaElegida={dia.fecha} onElegir={elegirDia} />
              <HorasDia
                dia={dia}
                tramos={tramos}
                horaElegida={dia === diaUrl ? horaValida : null}
                onElegir={(hora) => irA(dia.fecha, hora)}
              />
            </div>
          )}
        </div>
      </div>

      <BarraReserva
        visible={Boolean(diaUrl && horaValida)}
        titulo={diaUrl && horaValida ? `${formatFechaCorta(diaUrl.fecha)} · ${formatHora(horaValida)}` : ''}
        detalle={`${texts.barra.servicios(elegidos.length)} · ${textoTotales(elegidos)}`}
        to={`/reservar/datos?servicios=${ids}&fecha=${diaUrl?.fecha}&hora=${horaValida}`}
      />
    </>
  )
}

// "2026-10-02" → "FRIDAY", como el horario del backend
function diaSemana(iso) {
  const [y, m, d] = iso.split('-').map(Number)
  return ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'][new Date(y, m - 1, d).getDay()]
}
