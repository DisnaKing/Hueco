// @vitest-environment jsdom
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { cleanup, render, screen } from '@testing-library/react'
import { MemoryRouter, Route, Routes } from 'react-router'
import ConfirmadaPage from './ConfirmadaPage'
import { mockFetch, normalizar, stubNavegador } from '@/test/utils'

const cita = {
  fecha: '2026-10-01',
  hora: '16:00:00',
  duracionMinutos: 75,
  precioTotal: 40,
  estado: 'CONFIRMADA',
  servicios: [
    { nombre: 'Servicio A', duracionMinutos: 30, precio: 15 },
    { nombre: 'Servicio B', duracionMinutos: 45, precio: 25 },
  ],
}

function renderConfirmada(token = 'abc-123') {
  return render(
    <MemoryRouter initialEntries={[`/reservar/confirmada/${token}`]}>
      <Routes>
        <Route path="/reservar/confirmada/:token" element={<ConfirmadaPage />} />
      </Routes>
    </MemoryRouter>,
  )
}

describe('Confirmación de la cita', () => {
  beforeEach(() => stubNavegador())
  afterEach(() => {
    cleanup()
    vi.unstubAllGlobals()
  })

  it('muestra la cita, el calendario, cómo llegar y el teléfono', async () => {
    mockFetch({ respuestas: { '/api/reservas/abc-123': cita } })
    renderConfirmada()

    expect(await screen.findByRole('heading', { name: '¡Cita confirmada!' })).toBeTruthy()
    expect(screen.getByText('Servicio A')).toBeTruthy()
    expect(normalizar(screen.getByText(/1 h 15 min/).textContent)).toBe('1 h 15 min · 40,00 €')
    expect(screen.getByRole('link', { name: 'Añadir a mi calendario' }).getAttribute('href')).toBe(
      '/api/reservas/abc-123/cita.ics?nombre=Peluquer%C3%ADa%20Ejemplo',
    )
    expect((await screen.findByRole('link', { name: /Cómo llegar/ })).getAttribute('href')).toContain(
      'destination=Calle%20Prueba%201',
    )
    expect(screen.getByRole('link', { name: '+34 600 111 222' }).getAttribute('href')).toBe('tel:+34600111222')
  })

  it('con un token que no existe, muestra la página de no encontrado', async () => {
    mockFetch()
    renderConfirmada('no-existe')

    expect(await screen.findByRole('heading', { name: 'Página no encontrada' })).toBeTruthy()
    expect(screen.queryByRole('heading', { name: '¡Cita confirmada!' })).toBeNull()
  })
})
