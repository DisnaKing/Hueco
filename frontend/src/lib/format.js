import texts from '@/texts/es'

const precioFormatter = new Intl.NumberFormat('es-ES', { style: 'currency', currency: 'EUR' })
const diaMesFormatter = new Intl.DateTimeFormat('es-ES', { day: 'numeric', month: 'long' })
const diaSemanaFormatter = new Intl.DateTimeFormat('es-ES', { weekday: 'long' })
const mesCortoFormatter = new Intl.DateTimeFormat('es-ES', { month: 'short' })
const MS_DIA = 24 * 60 * 60 * 1000

// 75 → "1 h 15 min"; 30 → "30 min"; 60 → "1 h"
export function formatDuracion(minutos) {
  const horas = Math.floor(minutos / 60)
  const resto = minutos % 60
  if (horas === 0) return `${resto} min`
  if (resto === 0) return `${horas} h`
  return `${horas} h ${resto} min`
}

// 12.5 → "12,50 €"
export function formatPrecio(precio) {
  return precioFormatter.format(precio)
}

// "09:00:00" → "9:00"
export function formatHora(hora) {
  const [h, m] = hora.split(':')
  return `${Number(h)}:${m}`
}

// "2026-12-24" → fecha local, sin el desfase de new Date("2026-12-24"), que la toma en UTC
export function parseFecha(iso) {
  const [y, m, d] = iso.split('-').map(Number)
  return new Date(y, m - 1, d)
}

// "2026-12-24" → "24 de diciembre"
export function formatDiaMes(iso) {
  return diaMesFormatter.format(parseFecha(iso))
}

// Date local → "2026-10-02", el formato del backend
export function toIso(fecha) {
  const mes = String(fecha.getMonth() + 1).padStart(2, '0')
  const dia = String(fecha.getDate()).padStart(2, '0')
  return `${fecha.getFullYear()}-${mes}-${dia}`
}

// "2026-10-02" → "viernes 2"
export function formatDiaSemanaNumero(iso) {
  const fecha = parseFecha(iso)
  return `${diaSemanaFormatter.format(fecha)} ${fecha.getDate()}`
}

// "2026-10-02" → "viernes 2 de octubre"
export function formatFechaLarga(iso) {
  return `${diaSemanaFormatter.format(parseFecha(iso))} ${formatDiaMes(iso)}`
}

// "2026-10-02" → "Viernes 2 oct"
export function formatFechaCorta(iso) {
  const fecha = parseFecha(iso)
  const texto = `${diaSemanaFormatter.format(fecha)} ${fecha.getDate()} ${mesCortoFormatter.format(fecha).replace('.', '')}`
  return texto.charAt(0).toUpperCase() + texto.slice(1)
}

// "Cerrado el 24 de diciembre · Navidad", "Cerrado del 24 al 26 de diciembre · Navidad"
// o "Cerrado del 30 de diciembre al 2 de enero · Navidad"
export function textoCierre({ desde, hasta, motivo }) {
  let texto
  if (desde === hasta) {
    texto = texts.contacto.cierreUnDia(formatDiaMes(desde))
  } else if (desde.slice(0, 7) === hasta.slice(0, 7)) {
    texto = texts.contacto.cierreVariosDias(String(parseFecha(desde).getDate()), formatDiaMes(hasta))
  } else {
    texto = texts.contacto.cierreVariosDias(formatDiaMes(desde), formatDiaMes(hasta))
  }
  return motivo ? `${texto} · ${motivo}` : texto
}

// Agrupa en el orden en que aparece cada categoría; los servicios ya vienen ordenados del backend
export function agruparPorCategoria(servicios) {
  const grupos = new Map()
  for (const servicio of servicios) {
    const categoria = servicio.categoria ?? null
    if (!grupos.has(categoria)) grupos.set(categoria, [])
    grupos.get(categoria).push(servicio)
  }
  return [...grupos].map(([categoria, lista]) => ({ categoria, servicios: lista }))
}

// Suma en céntimos para no arrastrar errores de coma flotante
export function calcularTotales(servicios) {
  const centimos = servicios.reduce((total, s) => total + Math.round(s.precio * 100), 0)
  return {
    duracionMinutos: servicios.reduce((total, s) => total + s.duracionMinutos, 0),
    precio: centimos / 100,
  }
}

// "1 h 15 min · 40,00 €"
export function textoTotales(servicios) {
  const { duracionMinutos, precio } = calcularTotales(servicios)
  return `${formatDuracion(duracionMinutos)} · ${formatPrecio(precio)}`
}

// Convierte estadoHoy del backend en la frase del hero.
// La próxima apertura se nombra por el día de la semana si es dentro de una semana y por la fecha si no.
export function textoEstadoHoy(estadoHoy, hoy = new Date()) {
  if (!estadoHoy) return null
  const { estado, hora, dia } = estadoHoy
  switch (estado) {
    case 'ABIERTO':
      return texts.estadoHoy.abierto(formatHora(hora))
    case 'ABRE_HOY':
      return texts.estadoHoy.abreHoy(formatHora(hora))
    case 'CERRADO_HOY':
      if (!dia || !hora) return texts.estadoHoy.cerrado
      return texts.estadoHoy.proximaApertura(nombreDia(estadoHoy, hoy), formatHora(hora))
    default:
      return null
  }
}

function nombreDia({ dia, fecha }, hoy) {
  if (!fecha) return texts.dias[dia]
  const inicioHoy = new Date(hoy.getFullYear(), hoy.getMonth(), hoy.getDate())
  const dias = Math.round((parseFecha(fecha) - inicioHoy) / MS_DIA)
  return dias <= 6 ? texts.dias[dia] : formatDiaMes(fecha)
}
