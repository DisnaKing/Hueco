import texts from '@/texts/es'

const precioFormatter = new Intl.NumberFormat('es-ES', { style: 'currency', currency: 'EUR' })

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

// Convierte estadoHoy del backend en la frase del hero
export function textoEstadoHoy(estadoHoy) {
  if (!estadoHoy) return null
  const { estado, hora, dia } = estadoHoy
  switch (estado) {
    case 'ABIERTO':
      return texts.estadoHoy.abierto(formatHora(hora))
    case 'ABRE_HOY':
      return texts.estadoHoy.abreHoy(formatHora(hora))
    case 'CERRADO_HOY':
      if (!dia || !hora) return texts.estadoHoy.cerrado
      return texts.estadoHoy.proximaApertura(texts.dias[dia], formatHora(hora))
    default:
      return null
  }
}
