// @vitest-environment jsdom
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { cleanup, render, screen, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter, Route, Routes } from 'react-router'
import ServiciosPage from './ServiciosPage'
import { mockFetch, normalizar, stubNavegador } from '@/test/utils'

function renderPaso1(url = '/reservar') {
  return render(
    <MemoryRouter initialEntries={[url]}>
      <Routes>
        <Route path="/reservar" element={<ServiciosPage />} />
      </Routes>
    </MemoryRouter>,
  )
}

const tarjeta = (nombre) => screen.findByRole('button', { name: new RegExp(nombre) })
const barra = () => screen.getByTestId('barra-reserva')

describe('Paso 1: elegir servicios', () => {
  beforeEach(stubNavegador)
  afterEach(() => {
    cleanup()
    vi.unstubAllGlobals()
  })

  it('elegir dos muestra los totales y Continuar lleva al paso 2; quitarlos oculta la barra', async () => {
    mockFetch()
    const user = userEvent.setup()
    renderPaso1()

    expect(barra().dataset.visible).toBe('false')

    await user.click(await tarjeta('Servicio A'))
    await user.click(await tarjeta('Servicio B'))

    expect(barra().dataset.visible).toBe('true')
    expect(normalizar(within(barra()).getByText(/servicios/).closest('p').textContent)).toBe(
      '2 servicios1 h 15 min · 40,00 €',
    )
    // En el orden de la carta, no en el de elección
    expect(within(barra()).getByRole('link', { name: 'Continuar' }).getAttribute('href')).toBe(
      '/reservar/horario?servicios=2,1',
    )

    await user.click(await tarjeta('Servicio A'))
    await user.click(await tarjeta('Servicio B'))

    expect(barra().dataset.visible).toBe('false')
  })

  it('si falla la API muestra el error con el enlace para llamar', async () => {
    mockFetch({ fallos: ['/api/servicios'] })
    renderPaso1()

    const alerta = await screen.findByRole('alert')
    expect(alerta.textContent).toContain('No podemos mostrar los servicios ahora mismo')
    expect(within(alerta).getByRole('link', { name: /Llamar para reservar/ }).getAttribute('href')).toBe(
      'tel:+34600000000',
    )
  })

  it('con ids inválidos en la URL solo marca los servicios activos', async () => {
    mockFetch()
    renderPaso1('/reservar?servicios=2,99,abc')

    expect((await tarjeta('Servicio A')).getAttribute('aria-pressed')).toBe('true')
    expect((await tarjeta('Servicio B')).getAttribute('aria-pressed')).toBe('false')
    expect((await tarjeta('Servicio C')).getAttribute('aria-pressed')).toBe('false')
    expect(within(barra()).getByRole('link', { name: 'Continuar' }).getAttribute('href')).toBe(
      '/reservar/horario?servicios=2',
    )
  })
})
