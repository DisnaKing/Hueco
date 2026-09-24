import { Clock, ExternalLink, Mail, MapPin, Phone } from 'lucide-react'
import business from '@/business.config'
import texts from '@/texts/es'
import { formatHora } from '@/lib/format'
import { cn } from '@/lib/utils'
import { Button } from '@/components/ui/button'
import { Skeleton } from '@/components/ui/skeleton'

const telHref = (telefono) => `tel:${telefono.replace(/\s/g, '')}`

// Día de hoy en el formato del backend (MONDAY…), con la hora del navegador
const hoy = () => new Intl.DateTimeFormat('en-US', { weekday: 'long' }).format(new Date()).toUpperCase()

function Dato({ icono: Icono, titulo, children }) {
  return (
    <div className="flex gap-3">
      <Icono className="mt-0.5 size-5 shrink-0 text-primary" aria-hidden="true" />
      <div className="min-w-0 flex-1">
        <h3 className="text-sm font-semibold text-muted">{titulo}</h3>
        <div className="mt-0.5">{children}</div>
      </div>
    </div>
  )
}

function Horario({ horario }) {
  const diaHoy = hoy()
  return (
    <table className="w-full text-left">
      <tbody>
        {horario.map(({ dia, tramos }) => {
          const esHoy = dia === diaHoy
          return (
            <tr key={dia} className={cn(esHoy && 'bg-accent font-semibold')} aria-current={esHoy ? 'date' : undefined}>
              <th scope="row" className="rounded-l-lg py-1.5 pl-2 font-[inherit] capitalize">
                {texts.dias[dia]}
                {esHoy && <span className="ml-2 text-xs font-medium text-primary">({texts.contacto.hoy})</span>}
              </th>
              <td className="rounded-r-lg py-1.5 pr-2 text-right tabular-nums">
                {tramos.length === 0
                  ? <span className="text-muted">{texts.contacto.cerrado}</span>
                  : tramos.map((t) => `${formatHora(t.apertura)}–${formatHora(t.cierre)}`).join(', ')}
              </td>
            </tr>
          )
        })}
      </tbody>
    </table>
  )
}

export default function Contacto({ negocio }) {
  const { data, loading } = negocio

  return (
    <section id="contacto" aria-labelledby="contacto-titulo" className="scroll-mt-16 border-t border-line bg-card">
      <div className="mx-auto max-w-5xl px-4 py-12 md:py-16">
        <h2 id="contacto-titulo" className="font-display text-2xl font-semibold md:text-3xl">
          {texts.contacto.titulo}
        </h2>

        {loading && (
          <div className="mt-6 grid gap-6 md:grid-cols-2">
            <Skeleton className="h-40 rounded-2xl" />
            <Skeleton className="h-40 rounded-2xl" />
          </div>
        )}

        {!loading && !data && (
          <div className="mt-6">
            <Dato icono={Phone} titulo={texts.contacto.telefono}>
              <a href={telHref(business.telefono)} className="font-medium hover:text-primary">
                {business.telefono}
              </a>
            </Dato>
          </div>
        )}

        {data && (
          <div className="mt-6 grid gap-10 md:grid-cols-2">
            <div className="flex flex-col gap-6">
              {data.direccion && (
                <Dato icono={MapPin} titulo={texts.contacto.direccion}>
                  <p>{data.direccion}</p>
                  <Button asChild variant="outline" className="mt-3 h-10 rounded-full px-5">
                    <a
                      href={`https://www.google.com/maps/dir/?api=1&destination=${encodeURIComponent(data.direccion)}`}
                      target="_blank"
                      rel="noopener noreferrer"
                    >
                      {texts.contacto.comoLlegar}
                      <ExternalLink aria-hidden="true" />
                    </a>
                  </Button>
                </Dato>
              )}
              <Dato icono={Phone} titulo={texts.contacto.telefono}>
                <a href={telHref(data.telefono ?? business.telefono)} className="font-medium hover:text-primary">
                  {data.telefono ?? business.telefono}
                </a>
              </Dato>
              {data.email && (
                <Dato icono={Mail} titulo={texts.contacto.email}>
                  <a href={`mailto:${data.email}`} className="font-medium break-all hover:text-primary">
                    {data.email}
                  </a>
                </Dato>
              )}
              {data.redesSociales.length > 0 && (
                <div>
                  <h3 className="text-sm font-semibold text-muted">{texts.contacto.redes}</h3>
                  <ul className="mt-2 flex flex-wrap gap-2">
                    {data.redesSociales.map((red) => (
                      <li key={red.url}>
                        <a
                          href={red.url}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="inline-flex h-10 items-center rounded-full border border-line px-4 text-sm font-medium hover:border-primary hover:text-primary"
                        >
                          {texts.contacto.redNombres[red.tipo] ?? red.tipo}
                        </a>
                      </li>
                    ))}
                  </ul>
                </div>
              )}
            </div>

            <Dato icono={Clock} titulo={texts.contacto.horario}>
              <Horario horario={data.horario} />
            </Dato>
          </div>
        )}
      </div>
    </section>
  )
}
