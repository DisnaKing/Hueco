// Credencial del comercio (HTTP Basic) guardada en la pestaña: se borra al cerrarla o con "Salir"
const CLAVE = 'hueco.agenda'

export const leerCredencial = () => sessionStorage.getItem(CLAVE)
export const guardarCredencial = (credencial) => sessionStorage.setItem(CLAVE, credencial)
export const borrarCredencial = () => sessionStorage.removeItem(CLAVE)

// "usuario:clave" en base64 a partir de UTF-8, que es lo que espera Spring (btoa solo admite Latin-1)
export function crearCredencial(usuario, clave) {
  const bytes = new TextEncoder().encode(`${usuario}:${clave}`)
  return btoa(String.fromCharCode(...bytes))
}

// GET /api/agenda: siete días desde `desde` (sin él, desde hoy). Devuelve { status, data }; el 401 lo trata quien llama.
export async function pedirSemana(credencial, desde, { signal } = {}) {
  const response = await fetch(`/api/agenda${desde ? `?desde=${desde}` : ''}`, {
    signal,
    headers: { Accept: 'application/json', Authorization: `Basic ${credencial}` },
  })
  return { status: response.status, data: response.ok ? await response.json() : null }
}
