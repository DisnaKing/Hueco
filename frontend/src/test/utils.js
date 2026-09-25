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

// fetch simulado: cada ruta de la API (sin la query) devuelve su JSON, o un 502 si está en fallos
export function mockFetch({ fallos = [], respuestas: otras = {} } = {}) {
  const respuestas = { '/api/servicios': servicios, '/api/negocio': negocio, '/api/huecos': huecos, ...otras }
  vi.stubGlobal(
    'fetch',
    vi.fn(async (path) => {
      const ruta = path.split('?')[0]
      if (fallos.includes(ruta)) return { ok: false, status: 502, json: async () => ({}) }
      return { ok: true, status: 200, json: async () => respuestas[ruta] }
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
  vi.stubGlobal('requestAnimationFrame', (fn) => setTimeout(fn, 0))
  Element.prototype.scrollIntoView = () => {}
}

// Intl separa número y € con un espacio duro
export const normalizar = (texto) => texto.replace(/ /g, ' ').replace(/\s+/g, ' ').trim()
