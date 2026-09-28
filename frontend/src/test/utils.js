import { vi } from 'vitest'

export const servicios = [
  { id: 2, nombre: 'Servicio A', descripcion: 'Descripción A', duracionMinutos: 30, precio: 15, categoria: 'Corte' },
  { id: 1, nombre: 'Servicio B', descripcion: 'Descripción B', duracionMinutos: 45, precio: 25, categoria: 'Corte' },
  { id: 4, nombre: 'Servicio C', descripcion: null, duracionMinutos: 60, precio: 32.5, categoria: 'Color' },
]

export const negocio = {
  eslogan: 'Eslogan de prueba',
  sobreNosotros: 'Texto',
  direccion: 'Calle Prueba 1',
  telefono: '+34 600 111 222',
  email: 'prueba@example.com',
  horario: [],
  redesSociales: [],
  testimonios: [],
  estadoHoy: { estado: 'ABIERTO', hora: '20:00:00', dia: 'MONDAY' },
  hoy: 'MONDAY',
  cierres: [],
}

// Jueves 1/10/2026 con mañana y tarde; viernes 2 completo; sábado 3 cerrado; lunes 5 con una hora
export const huecos = [
  { fecha: '2026-10-01', estado: 'LIBRE', horas: ['09:00', '09:15', '16:00'] },
  { fecha: '2026-10-02', estado: 'COMPLETO', horas: [] },
  { fecha: '2026-10-03', estado: 'CERRADO', horas: [] },
  { fecha: '2026-10-04', estado: 'CERRADO', horas: [] },
  { fecha: '2026-10-05', estado: 'LIBRE', horas: ['10:30'] },
]

const respuesta = (status, body) => ({
  ok: status < 300,
  status,
  json: async () => body,
  text: async () => (body === undefined ? '' : JSON.stringify(body)),
})

// fetch simulado: cada ruta de la API (sin la query) devuelve su JSON, o un 502 si está en fallos.
// posts: { ruta: { status, body } } para las peticiones POST.
export function mockFetch({ fallos = [], respuestas: otras = {}, posts = {} } = {}) {
  const respuestas = { '/api/servicios': servicios, '/api/negocio': negocio, '/api/huecos': huecos, ...otras }
  const fetch = vi.fn(async (path, init) => {
    const ruta = path.split('?')[0]
    if (init?.method === 'POST') {
      const r = posts[ruta] ?? { status: 500 }
      return respuesta(r.status, r.body)
    }
    if (fallos.includes(ruta)) return respuesta(502, {})
    if (!(ruta in respuestas)) return respuesta(404, {})
    return respuesta(200, respuestas[ruta])
  })
  vi.stubGlobal('fetch', fetch)
  return fetch
}

// APIs del navegador que jsdom no trae
export function stubNavegador() {
  vi.stubGlobal(
    'IntersectionObserver',
    class {
      observe() {}
      disconnect() {}
    },
  )
  vi.stubGlobal('matchMedia', () => ({ matches: false }))
  vi.stubGlobal('requestAnimationFrame', (fn) => setTimeout(fn, 0))
  Element.prototype.scrollIntoView = () => {}
}

// Intl separa número y € con un espacio duro
export const normalizar = (texto) => texto.replace(/ /g, ' ').replace(/\s+/g, ' ').trim()
