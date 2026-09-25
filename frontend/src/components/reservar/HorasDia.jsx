import texts from '@/texts/es'
import { formatDiaSemanaNumero, formatHora } from '@/lib/format'
import { cn } from '@/lib/utils'

// Nombre de cada grupo: con uno o dos tramos, "Mañana" y "Tarde"; con más, su rango
function nombreGrupos(tramos) {
  if (tramos.length > 2) {
    return tramos.map((t) => `${formatHora(t.apertura)} – ${formatHora(t.cierre)}`)
  }
  return tramos.map((t) => (t.apertura < '14:00' ? texts.reservar.manana : texts.reservar.tarde))
}

// Agrupa las horas libres por tramo. Sin horario (si falla /api/negocio), un solo grupo sin título.
function agrupar(horas, tramos) {
  if (!tramos?.length) return [{ nombre: null, horas }]
  const nombres = nombreGrupos(tramos)
  return tramos
    .map((t, i) => ({
      nombre: nombres[i],
      horas: horas.filter((h) => h >= t.apertura.slice(0, 5) && h < t.cierre.slice(0, 5)),
    }))
    .filter((g) => g.horas.length > 0)
}

export default function HorasDia({ dia, tramos, horaElegida, onElegir }) {
  return (
    <section aria-labelledby="horas-titulo" className="scroll-mt-20">
      <h2 id="horas-titulo" tabIndex={-1} className="font-display text-xl font-semibold outline-none">
        {texts.reservar.horasDel(formatDiaSemanaNumero(dia.fecha))}
      </h2>
      {!horaElegida && <p className="mt-1 text-sm text-muted">{texts.reservar.elegirHora}</p>}

      <div className="mt-4 flex flex-col gap-5">
        {agrupar(dia.horas, tramos).map((grupo) => (
          <div key={grupo.nombre ?? 'todas'}>
            {grupo.nombre && <h3 className="mb-2 text-sm font-semibold text-muted">{grupo.nombre}</h3>}
            <ul className="grid grid-cols-3 gap-2 sm:grid-cols-4 lg:grid-cols-6">
              {grupo.horas.map((hora) => (
                <li key={hora}>
                  <button
                    type="button"
                    aria-pressed={hora === horaElegida}
                    onClick={() => onElegir(hora)}
                    className={cn(
                      'h-11 w-full rounded-xl border-2 text-sm font-medium tabular-nums transition-colors',
                      hora === horaElegida
                        ? 'border-primary bg-primary text-primary-contrast'
                        : 'border-line bg-card hover:border-ink/25',
                    )}
                  >
                    {formatHora(hora)}
                  </button>
                </li>
              ))}
            </ul>
          </div>
        ))}
      </div>
    </section>
  )
}
