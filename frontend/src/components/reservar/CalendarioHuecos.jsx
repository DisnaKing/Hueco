import { useState } from 'react'
import { es } from 'react-day-picker/locale'
import texts from '@/texts/es'
import { formatFechaLarga, parseFecha, toIso } from '@/lib/format'
import { Calendar } from '@/components/ui/calendar'

// Calendario del mes con el estado de cada día según /api/huecos:
// libre, completo (tachado), cerrado (gris) y fuera de plazo o pasado (muy tenue)
export default function CalendarioHuecos({ dias, fechaElegida, onElegir }) {
  const porFecha = new Map(dias.map((d) => [d.fecha, d]))
  const estado = (date) => porFecha.get(toIso(date))?.estado ?? 'FUERA'
  // "Hoy" es el del comercio, que es el primer día que devuelve el backend
  const hoy = parseFecha(dias[0].fecha)
  const ultimo = parseFecha(dias.at(-1).fecha)

  // Se abre en el mes del día elegido, que puede ser el siguiente
  const [mes, setMes] = useState(() => (fechaElegida ? parseFecha(fechaElegida) : hoy))

  return (
    <div>
      <Calendar
        mode="single"
        locale={es}
        weekStartsOn={1}
        today={hoy}
        showOutsideDays={false}
        month={mes}
        onMonthChange={setMes}
        startMonth={hoy}
        endMonth={ultimo}
        selected={fechaElegida ? parseFecha(fechaElegida) : undefined}
        onSelect={(date) => date && onElegir(toIso(date))}
        disabled={(date) => estado(date) !== 'LIBRE'}
        modifiers={{
          completo: (date) => estado(date) === 'COMPLETO',
          cerrado: (date) => estado(date) === 'CERRADO',
          fuera: (date) => estado(date) === 'FUERA',
        }}
        modifiersClassNames={{
          completo: 'line-through',
          cerrado: 'text-muted',
          fuera: 'opacity-30',
        }}
        classNames={{
          // Los estados de arriba sustituyen al atenuado genérico de los días deshabilitados
          // (el de DayPicker y el disabled:opacity-50 del Button)
          disabled: '',
          day_button: 'disabled:opacity-100',
          today:
            'after:pointer-events-none after:absolute after:bottom-1 after:left-1/2 after:size-1 after:-translate-x-1/2 after:rounded-full after:bg-primary',
        }}
        labels={{
          labelDayButton: (date) => `${formatFechaLarga(toIso(date))}, ${texts.reservar.estadoDia[estado(date)]}`,
        }}
        className="p-0 [--cell-size:--spacing(11)]"
      />
      <p className="mt-3 text-xs text-muted">{texts.reservar.leyenda}</p>
    </div>
  )
}
