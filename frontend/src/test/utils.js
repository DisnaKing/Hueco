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
}

// fetch simulado: cada ruta de la API devuelve su JSON, o un 502 si está en fallos
export function mockFetch({ fallos = [] } = {}) {
  const respuestas = { '/api/servicios': servicios, '/api/negocio': negocio }
  vi.stubGlobal(
    'fetch',
    vi.fn(async (path) => {
      if (fallos.includes(path)) return { ok: false, status: 502, json: async () => ({}) }
      return { ok: true, status: 200, json: async () => respuestas[path] }
    }),
  )
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
  Element.prototype.scrollIntoView = () => {}
}

// Intl separa número y € con un espacio duro
export const normalizar = (texto) => texto.replace(/ /g, ' ').replace(/\s+/g, ' ').trim()
