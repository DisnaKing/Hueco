import { describe, expect, it } from 'vitest'
import {
  agruparPorCategoria,
  calcularTotales,
  formatDuracion,
  formatHora,
  formatPrecio,
  formatDiaMes,
  formatDiaSemanaNumero,
  formatFechaCorta,
  formatFechaLarga,
  toIso,
  textoCierre,
  textoEstadoHoy,
} from './format'

// Intl separa el número y el símbolo con un espacio duro
const normalizar = (texto) => texto.replace(/ /g, ' ')

describe('formatDuracion', () => {
  it('muestra horas y minutos', () => {
    expect(formatDuracion(75)).toBe('1 h 15 min')
  })
  it('muestra solo minutos por debajo de una hora', () => {
    expect(formatDuracion(30)).toBe('30 min')
  })
  it('omite los minutos en horas exactas', () => {
    expect(formatDuracion(120)).toBe('2 h')
  })
})

describe('formatPrecio', () => {
  it('usa coma decimal y euro al final', () => {
    expect(normalizar(formatPrecio(12.5))).toBe('12,50 €')
  })
  it('muestra dos decimales en precios enteros', () => {
    expect(normalizar(formatPrecio(32))).toBe('32,00 €')
  })
})

describe('formatHora', () => {
  it('quita los segundos y el cero inicial', () => {
    expect(formatHora('09:00:00')).toBe('9:00')
    expect(formatHora('20:30')).toBe('20:30')
  })
})

describe('agruparPorCategoria', () => {
  it('agrupa en el orden de aparición', () => {
    const servicios = [
      { id: 1, categoria: 'Corte' },
      { id: 2, categoria: 'Color' },
      { id: 3, categoria: 'Corte' },
    ]
    expect(agruparPorCategoria(servicios)).toEqual([
      { categoria: 'Corte', servicios: [servicios[0], servicios[2]] },
      { categoria: 'Color', servicios: [servicios[1]] },
    ])
  })
  it('devuelve un solo grupo con categoria null si no hay categorías', () => {
    const servicios = [{ id: 1, categoria: null }, { id: 2 }]
    expect(agruparPorCategoria(servicios)).toEqual([{ categoria: null, servicios }])
  })
  it('devuelve una lista vacía sin servicios', () => {
    expect(agruparPorCategoria([])).toEqual([])
  })
})

describe('calcularTotales', () => {
  it('suma duración y precio sin errores de redondeo', () => {
    const totales = calcularTotales([
      { duracionMinutos: 45, precio: 0.1 },
      { duracionMinutos: 30, precio: 0.2 },
    ])
    expect(totales).toEqual({ duracionMinutos: 75, precio: 0.3 })
  })
})

describe('textoEstadoHoy', () => {
  it('abierto', () => {
    expect(textoEstadoHoy({ estado: 'ABIERTO', hora: '20:00:00', dia: 'MONDAY' })).toBe(
      'Abierto hoy hasta las 20:00',
    )
  })
  it('abre hoy más tarde', () => {
    expect(textoEstadoHoy({ estado: 'ABRE_HOY', hora: '16:00:00', dia: 'MONDAY' })).toBe(
      'Hoy abrimos a las 16:00',
    )
  })
  it('cerrado hoy con próxima apertura', () => {
    expect(textoEstadoHoy({ estado: 'CERRADO_HOY', hora: '09:00:00', dia: 'MONDAY' })).toBe(
      'Cerrado · Abrimos el lunes a las 9:00',
    )
  })
  it('cerrado sin horario', () => {
    expect(textoEstadoHoy({ estado: 'CERRADO_HOY', hora: null, dia: null })).toBe('Cerrado')
  })
  it('sin datos', () => {
    expect(textoEstadoHoy(null)).toBeNull()
  })
})

describe('textoEstadoHoy con fecha', () => {
  // Lunes 21/09/2026
  const hoy = new Date(2026, 8, 21, 10, 0)

  it('dentro de una semana usa el día de la semana', () => {
    const estado = { estado: 'CERRADO_HOY', hora: '09:00:00', dia: 'SUNDAY', fecha: '2026-09-27' }
    expect(textoEstadoHoy(estado, hoy)).toBe('Cerrado · Abrimos el domingo a las 9:00')
  })
  it('el mismo día de la semana que viene usa la fecha, para no confundirlo con hoy', () => {
    const estado = { estado: 'CERRADO_HOY', hora: '09:00:00', dia: 'MONDAY', fecha: '2026-09-28' }
    expect(textoEstadoHoy(estado, hoy)).toBe('Cerrado · Abrimos el 28 de septiembre a las 9:00')
  })
  it('a más de una semana usa la fecha', () => {
    const estado = { estado: 'CERRADO_HOY', hora: '09:00:00', dia: 'MONDAY', fecha: '2026-10-05' }
    expect(textoEstadoHoy(estado, hoy)).toBe('Cerrado · Abrimos el 5 de octubre a las 9:00')
  })
})

describe('formatDiaMes', () => {
  it('no se desplaza un día por la zona horaria', () => {
    expect(formatDiaMes('2026-12-24')).toBe('24 de diciembre')
  })
})

describe('textoCierre', () => {
  it('un solo día', () => {
    expect(textoCierre({ desde: '2026-12-25', hasta: '2026-12-25', motivo: 'Navidad' })).toBe(
      'Cerrado el 25 de diciembre · Navidad',
    )
  })
  it('varios días del mismo mes', () => {
    expect(textoCierre({ desde: '2026-12-24', hasta: '2026-12-26', motivo: 'Navidad' })).toBe(
      'Cerrado del 24 al 26 de diciembre · Navidad',
    )
  })
  it('entre dos meses y sin motivo', () => {
    expect(textoCierre({ desde: '2026-12-30', hasta: '2027-01-02', motivo: null })).toBe(
      'Cerrado del 30 de diciembre al 2 de enero',
    )
  })
})

describe('fechas del calendario', () => {
  it('toIso usa la fecha local', () => {
    expect(toIso(new Date(2026, 9, 2, 23, 30))).toBe('2026-10-02')
  })
  it('formatos de un día', () => {
    expect(formatDiaSemanaNumero('2026-10-02')).toBe('viernes 2')
    expect(formatFechaLarga('2026-10-02')).toBe('viernes 2 de octubre')
    expect(formatFechaCorta('2026-10-02')).toBe('Viernes 2 oct')
  })
})
