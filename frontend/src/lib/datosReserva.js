import texts from '@/texts/es'

export const MAX_NOMBRE = 100
export const MAX_EMAIL = 150
export const MAX_NOTAS = 300
const TELEFONO_ESPANOL = /^(\+34|0034)?[6789]\d{8}$/
const EMAIL = /^[^@\s]+@[^@\s]+\.[^@\s]+$/

// Misma validación que el backend, para avisar antes de enviar. Devuelve { campo: mensaje }.
export function validarDatos({ nombre, telefono, email, notas }) {
  const errores = {}
  if (!nombre.trim()) errores.nombre = texts.reservar.errores.nombre
  if (!TELEFONO_ESPANOL.test(telefono.replace(/[\s.\-()]/g, ''))) errores.telefono = texts.reservar.errores.telefono
  if (email.trim() && !EMAIL.test(email.trim())) errores.email = texts.reservar.errores.email
  if (notas.length > MAX_NOTAS) errores.notas = texts.reservar.errores.notas(MAX_NOTAS)
  return errores
}

// Lo escrito en el paso 3 se guarda en la pestaña: si la hora se ocupa (409) y el cliente elige
// otra, no tiene que volver a teclearlo. Se borra al confirmar.
const CLAVE = 'hueco.datosReserva'
export const DATOS_VACIOS = { nombre: '', telefono: '', email: '', notas: '' }

export function leerDatosGuardados() {
  try {
    return { ...DATOS_VACIOS, ...JSON.parse(sessionStorage.getItem(CLAVE) ?? '{}') }
  } catch {
    return DATOS_VACIOS
  }
}

export function guardarDatos(datos) {
  sessionStorage.setItem(CLAVE, JSON.stringify(datos))
}

export function borrarDatosGuardados() {
  sessionStorage.removeItem(CLAVE)
}
