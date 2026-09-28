// @vitest-environment jsdom
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { cleanup, render, screen, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router'
import AgendaPage from './AgendaPage'
import { sumarDias } from '@/lib/format'
import { stubNavegador } from '@/test/utils'

const HOY = '2026-09-28'
const CREDENCIAL = btoa('comercio:hueco-dev')

// Como el seed de desarrollo: martes con Lucía (con email) y Javier (con notas), miércoles cerrado por vacaciones
function semana(desde) {
  return Array.from({ length: 7 }, (_, i) => {
    const fecha = sumarDias(desde, i)
    const citas =
      fecha === '2026-09-29'
        ? [
            {
              inicio: '09:00:00',
              fin: '11:00:00',
              estado: 'CONFIRMADA',
              servicios: ['Mechas'],
              total: 65,
              cliente: { nombre: 'Lucía Martín', telefono: '+34611000001', email: 'lucia@example.com' },
              notas: null,
            },
            {
              inicio: '11:15:00',
              fin: '12:00:00',
              estado: 'PENDIENTE',
              servicios: ['Corte', 'Barba'],
              total: 25,
              cliente: { nombre: 'Javier Ruiz', telefono: '+34622000002', email: null },
              notas: 'Prefiere máquina del 2',
            },
          ]
        : []
    const cerrado = fecha === '2026-09-30'
    return { fecha, cerrado, motivoCierre: cerrado ? 'Vacaciones' : null, citas }
  })
}

function stubAgenda() {
  const fetch = vi.fn(async (path, init) => {
    if (init.headers.Authorization !== `Basic ${CREDENCIAL}`) return { ok: false, status: 401 }
    const desde = new URL(path, 'http://localhost').searchParams.get('desde') ?? HOY
    return { ok: true, status: 200, json: async () => semana(desde) }
  })
  vi.stubGlobal('fetch', fetch)
  return fetch
}

const renderAgenda = () =>
  render(
    <MemoryRouter initialEntries={['/agenda']}>
      <AgendaPage />
    </MemoryRouter>,
  )

async function entrar(user, clave = 'hueco-dev') {
  await user.type(screen.getByLabelText('Usuario'), 'comercio')
  await user.type(screen.getByLabelText('Clave'), clave)
  await user.click(screen.getByRole('button', { name: 'Entrar' }))
}

describe('Agenda del comercio', () => {
  beforeEach(() => {
    stubNavegador()
    sessionStorage.clear()
    vi.useFakeTimers({ now: new Date(2026, 8, 28, 10), toFake: ['Date'] })
  })
  afterEach(() => {
    cleanup()
    vi.useRealTimers()
    vi.unstubAllGlobals()
  })

  it('con la clave incorrecta avisa y no guarda nada', async () => {
    stubAgenda()
    const user = userEvent.setup()
    renderAgenda()

    await entrar(user, 'otra')

    expect((await screen.findByRole('alert')).textContent).toBe('Usuario o clave incorrectos')
    expect(screen.getByLabelText('Clave').getAttribute('aria-invalid')).toBe('true')
    expect(sessionStorage.getItem('hueco.agenda')).toBeNull()
  })

  it('con la clave correcta muestra las citas de la semana', async () => {
    stubAgenda()
    const user = userEvent.setup()
    renderAgenda()

    await entrar(user)

    const martes = await screen.findByRole('region', { name: 'Martes 29 de septiembre' })
    expect(within(martes).getByText('9:00–11:00')).toBeTruthy()
    expect(within(martes).getByText('Lucía Martín')).toBeTruthy()
    expect(within(martes).getByRole('link', { name: '+34 611 000 001' }).getAttribute('href')).toBe('tel:+34611000001')
    expect(within(martes).getByRole('link', { name: 'lucia@example.com' }).getAttribute('href')).toBe(
      'mailto:lucia@example.com',
    )
    expect(within(martes).getByText('Pendiente')).toBeTruthy()
    expect(within(martes).getByText('Prefiere máquina del 2')).toBeTruthy()
    expect(within(screen.getByRole('region', { name: /Miércoles 30/ })).getByText('Cerrado · Vacaciones')).toBeTruthy()
    expect(within(screen.getByRole('region', { name: /Lunes 28/ })).getByText('hoy')).toBeTruthy()
    expect(sessionStorage.getItem('hueco.agenda')).toBe(CREDENCIAL)
    expect(document.querySelector('meta[name=robots]').getAttribute('content')).toBe('noindex')
  })

  it('navega entre semanas y vuelve a hoy', async () => {
    sessionStorage.setItem('hueco.agenda', CREDENCIAL)
    const fetch = stubAgenda()
    const user = userEvent.setup()
    renderAgenda()

    expect(await screen.findByRole('heading', { name: '28 de septiembre – 4 de octubre' })).toBeTruthy()

    await user.click(screen.getByRole('button', { name: 'Semana siguiente' }))
    expect(await screen.findByRole('heading', { name: '5 de octubre – 11 de octubre' })).toBeTruthy()
    expect(fetch).toHaveBeenLastCalledWith('/api/agenda?desde=2026-10-05', expect.anything())

    await user.click(screen.getByRole('button', { name: 'Semana anterior' }))
    await user.click(await screen.findByRole('button', { name: 'Semana anterior' }))
    expect(await screen.findByRole('heading', { name: '21 de septiembre – 27 de septiembre' })).toBeTruthy()

    await user.click(screen.getByRole('button', { name: 'Hoy' }))
    expect(await screen.findByRole('heading', { name: '28 de septiembre – 4 de octubre' })).toBeTruthy()
    expect(fetch).toHaveBeenLastCalledWith('/api/agenda', expect.anything())
  })

  it('Salir borra la credencial y vuelve al acceso', async () => {
    sessionStorage.setItem('hueco.agenda', CREDENCIAL)
    stubAgenda()
    const user = userEvent.setup()
    renderAgenda()

    await user.click(await screen.findByRole('button', { name: 'Salir' }))

    expect(screen.getByLabelText('Usuario')).toBeTruthy()
    expect(sessionStorage.getItem('hueco.agenda')).toBeNull()
  })

  it('si la credencial guardada ya no vale, vuelve al acceso', async () => {
    sessionStorage.setItem('hueco.agenda', btoa('comercio:vieja'))
    stubAgenda()
    renderAgenda()

    expect(await screen.findByLabelText('Usuario')).toBeTruthy()
    expect(sessionStorage.getItem('hueco.agenda')).toBeNull()
  })
})
