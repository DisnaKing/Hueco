import { useCallback, useEffect, useState } from 'react'
import { ChevronLeft, ChevronRight, LogOut, Mail, Phone } from 'lucide-react'
import business from '@/business.config'
import texts from '@/texts/es'
import { borrarCredencial, crearCredencial, guardarCredencial, leerCredencial, pedirSemana } from '@/api/agenda'
import { formatDiaMes, formatFechaLarga, formatHora, formatPrecio, sumarDias, toIso } from '@/lib/format'
import { cn } from '@/lib/utils'
import { Button } from '@/components/ui/button'
import { Skeleton } from '@/components/ui/skeleton'

const t = texts.agenda
const INACTIVAS = ['CANCELADA', 'NO_SHOW']
const mayuscula = (texto) => texto.charAt(0).toUpperCase() + texto.slice(1)
// "+34611000001" → "+34 611 000 001"
const formatTelefono = (tel) => tel.replace(/^(\+\d{2})(\d{3})(\d{3})(\d{3})$/, '$1 $2 $3 $4')

const claseInput =
  'mt-1.5 block h-12 w-full rounded-xl border-2 bg-card px-3 text-base outline-none transition-colors focus-visible:border-primary focus-visible:outline-none'

// Usuario y clave del comercio. Se comprueban pidiendo la agenda: un 401 es que no son correctos.
function Acceso({ onEntrar }) {
  const [usuario, setUsuario] = useState('')
  const [clave, setClave] = useState('')
  // editando | enviando | incorrecta | error
  const [estado, setEstado] = useState('editando')

  async function entrar(event) {
    event.preventDefault()
    setEstado('enviando')
    const credencial = crearCredencial(usuario.trim(), clave)
    try {
      const { status } = await pedirSemana(credencial, null)
      if (status === 200) onEntrar(credencial)
      else setEstado(status === 401 ? 'incorrecta' : 'error')
    } catch {
      setEstado('error')
    }
  }

  const incorrecta = estado === 'incorrecta'
  const mensaje = { incorrecta: t.claveIncorrecta, error: t.errorConexion }[estado]

  return (
    <form onSubmit={entrar} className="mx-auto flex max-w-sm flex-col gap-5 px-4 pt-10 pb-16">
      <h1 className="font-display text-3xl font-semibold">{t.titulo}</h1>
      <div>
        <label htmlFor="agenda-usuario" className="block text-sm font-medium">
          {t.usuario}
        </label>
        <input
          id="agenda-usuario"
          autoComplete="username"
          autoCapitalize="none"
          required
          value={usuario}
          onChange={(e) => setUsuario(e.target.value)}
          aria-invalid={incorrecta || undefined}
          aria-describedby={mensaje ? 'agenda-error' : undefined}
          className={cn(claseInput, incorrecta ? 'border-destructive' : 'border-line')}
        />
      </div>
      <div>
        <label htmlFor="agenda-clave" className="block text-sm font-medium">
          {t.clave}
        </label>
        <input
          id="agenda-clave"
          type="password"
          autoComplete="current-password"
          required
          value={clave}
          onChange={(e) => setClave(e.target.value)}
          aria-invalid={incorrecta || undefined}
          aria-describedby={mensaje ? 'agenda-error' : undefined}
          className={cn(claseInput, incorrecta ? 'border-destructive' : 'border-line')}
        />
      </div>
      {mensaje && (
        <p id="agenda-error" role="alert" className="text-sm font-medium text-destructive">
          {mensaje}
        </p>
      )}
      <Button type="submit" disabled={estado === 'enviando'} className="h-12 w-full rounded-full text-base">
        {estado === 'enviando' ? t.entrando : t.entrar}
      </Button>
    </form>
  )
}

function Cita({ cita }) {
  const { inicio, fin, estado, servicios, total, cliente, notas } = cita
  const inactiva = INACTIVAS.includes(estado)
  return (
    <li className={cn('flex flex-col gap-1 px-4 py-3', inactiva && 'text-muted')}>
      <div className="flex items-baseline justify-between gap-3">
        <p className={cn('text-lg font-semibold tabular-nums', inactiva && 'line-through')}>
          {formatHora(inicio)}–{formatHora(fin)}
        </p>
        <span
          className={cn(
            'shrink-0 rounded-full px-2.5 py-0.5 text-xs font-medium',
            estado === 'PENDIENTE' ? 'bg-primary/10 text-primary' : 'bg-secondary text-ink/80',
          )}
        >
          {t.estados[estado] ?? estado}
        </span>
      </div>
      <p>
        {servicios.join(', ')} <span className="text-sm text-muted tabular-nums">· {formatPrecio(total)}</span>
      </p>
      {cliente ? (
        <>
          <p className="font-medium">{cliente.nombre}</p>
          <div className="flex flex-wrap gap-x-4 gap-y-1 text-sm">
            {cliente.telefono && (
              <a href={`tel:${cliente.telefono}`} className="inline-flex items-center gap-1.5 text-primary underline-offset-4 hover:underline">
                <Phone className="size-4" aria-hidden="true" />
                {formatTelefono(cliente.telefono)}
              </a>
            )}
            {cliente.email && (
              <a
                href={`mailto:${cliente.email}`}
                className="inline-flex min-w-0 items-center gap-1.5 text-primary underline-offset-4 hover:underline"
              >
                <Mail className="size-4 shrink-0" aria-hidden="true" />
                <span className="truncate">{cliente.email}</span>
              </a>
            )}
          </div>
        </>
      ) : (
        <p className="text-sm text-muted">{t.sinCliente}</p>
      )}
      {notas && <p className="mt-1 rounded-lg bg-secondary px-3 py-2 text-sm">{notas}</p>}
    </li>
  )
}

function Dia({ dia, hoy }) {
  const { fecha, cerrado, motivoCierre, citas } = dia
  return (
    <section aria-labelledby={`dia-${fecha}`} className="rounded-2xl border border-line bg-card">
      <h2 id={`dia-${fecha}`} className="flex items-center gap-2 border-b border-line px-4 py-3 font-semibold">
        {mayuscula(formatFechaLarga(fecha))}
        {fecha === hoy && (
          <span className="rounded-full bg-primary px-2 py-0.5 text-xs font-medium text-primary-contrast">{t.esHoy}</span>
        )}
      </h2>
      {cerrado && (
        <p className="px-4 py-3 text-muted">
          {t.cerrado}
          {motivoCierre && ` · ${motivoCierre}`}
        </p>
      )}
      {citas.length > 0 ? (
        <ul className="divide-y divide-line">
          {citas.map((cita) => (
            <Cita key={`${cita.inicio}-${cita.estado}-${cita.cliente?.telefono}`} cita={cita} />
          ))}
        </ul>
      ) : (
        !cerrado && <p className="px-4 py-3 text-muted">{t.sinCitas}</p>
      )}
    </section>
  )
}

// Siete días desde `desde` (null: desde hoy, en la zona del comercio). Las flechas mueven una semana
// a partir del primer día recibido, así no depende del reloj del navegador.
function Semana({ credencial, onNoAutorizado }) {
  const [desde, setDesde] = useState(null)
  const [resultado, setResultado] = useState({ desde: undefined, dias: null, error: false })
  const cargando = resultado.desde !== desde

  useEffect(() => {
    const controller = new AbortController()
    pedirSemana(credencial, desde, { signal: controller.signal })
      .then(({ status, data }) => {
        if (status === 401) return onNoAutorizado()
        setResultado((r) => (data ? { desde, dias: data, error: false } : { ...r, desde, error: true }))
      })
      .catch(() => {
        if (!controller.signal.aborted) setResultado((r) => ({ ...r, desde, error: true }))
      })
    return () => controller.abort()
  }, [credencial, desde, onNoAutorizado])

  const { dias, error } = resultado
  const primero = dias?.[0]?.fecha
  const ultimo = dias?.at(-1)?.fecha
  const hoy = toIso(new Date())
  const mover = (n) => setDesde(sumarDias(primero, n))

  return (
    <div className="mx-auto max-w-2xl px-4 pt-6 pb-16">
      <div className="flex items-center justify-between gap-2">
        <h1 className="font-display text-2xl font-semibold">
          {primero ? `${formatDiaMes(primero)} – ${formatDiaMes(ultimo)}` : t.titulo}
        </h1>
        <div className="flex shrink-0 items-center gap-1">
          <Button
            variant="outline"
            size="icon"
            className="size-10 rounded-full"
            aria-label={t.anterior}
            disabled={!primero || cargando}
            onClick={() => mover(-7)}
          >
            <ChevronLeft aria-hidden="true" />
          </Button>
          <Button
            variant="outline"
            className="h-10 rounded-full px-4"
            disabled={desde === null || cargando}
            onClick={() => setDesde(null)}
          >
            {t.hoy}
          </Button>
          <Button
            variant="outline"
            size="icon"
            className="size-10 rounded-full"
            aria-label={t.siguiente}
            disabled={!primero || cargando}
            onClick={() => mover(7)}
          >
            <ChevronRight aria-hidden="true" />
          </Button>
        </div>
      </div>

      {error && !cargando && (
        <p role="alert" className="mt-4 rounded-xl border border-destructive/30 bg-destructive/5 p-4 font-medium">
          {t.errorConexion}
        </p>
      )}

      {!dias && cargando && (
        <div className="mt-6 flex flex-col gap-4" aria-hidden="true">
          <Skeleton className="h-32 rounded-2xl" />
          <Skeleton className="h-32 rounded-2xl" />
        </div>
      )}

      {dias && (
        <div className={cn('mt-6 flex flex-col gap-4 transition-opacity', cargando && 'opacity-60')} aria-busy={cargando}>
          {dias.map((dia) => (
            <Dia key={dia.fecha} dia={dia} hoy={hoy} />
          ))}
        </div>
      )}
    </div>
  )
}

// Agenda del comercio: solo lectura y sin enlace desde la web pública
export default function AgendaPage() {
  const [credencial, setCredencial] = useState(leerCredencial)

  const salir = useCallback(() => {
    borrarCredencial()
    setCredencial(null)
  }, [])

  function entrar(nueva) {
    guardarCredencial(nueva)
    setCredencial(nueva)
  }

  return (
    <div className="min-h-svh">
      <title>{`${t.titulo} · ${business.nombre}`}</title>
      <meta name="robots" content="noindex" />
      <header className="sticky top-0 z-40 border-b border-line bg-surface/95 backdrop-blur supports-[backdrop-filter]:bg-surface/80">
        <div className="mx-auto flex h-14 max-w-2xl items-center gap-2 px-4">
          <img src={business.logo} alt="" width="28" height="28" className="size-7 shrink-0" />
          <span className="mr-auto truncate font-display font-semibold">{business.nombre}</span>
          {credencial && (
            <Button variant="ghost" className="h-10 rounded-full px-3" onClick={salir}>
              <LogOut aria-hidden="true" />
              {t.salir}
            </Button>
          )}
        </div>
      </header>
      <main>{credencial ? <Semana credencial={credencial} onNoAutorizado={salir} /> : <Acceso onEntrar={entrar} />}</main>
    </div>
  )
}
