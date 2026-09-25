// @vitest-environment jsdom
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { cleanup, render, screen, waitFor } from '@testing-library/react'
import { MemoryRouter } from 'react-router'
import HomePage from './HomePage'
import { mockFetch, stubNavegador } from '@/test/utils'

function renderHome() {
  return render(
    <MemoryRouter>
      <HomePage />
    </MemoryRouter>,
  )
}

describe('Home: carta de servicios', () => {
  beforeEach(stubNavegador)
  afterEach(() => {
    cleanup()
    vi.unstubAllGlobals()
  })

  it('cada fila empieza la reserva con ese servicio', async () => {
    mockFetch()
    renderHome()

    const fila = await screen.findByRole('link', { name: /Servicio B/ })
    expect(fila.getAttribute('href')).toBe('/reservar?servicios=1')
  })

  it('si falla la API la sección no aparece', async () => {
    mockFetch({ fallos: ['/api/servicios'] })
    renderHome()

    // El hero sí carga (el negocio responde bien)
    await screen.findByRole('heading', { name: 'Eslogan de prueba' })
    await waitFor(() => expect(screen.queryByRole('heading', { name: 'Servicios' })).toBeNull())
    expect(document.getElementById('servicios')).toBeNull()
  })
})
