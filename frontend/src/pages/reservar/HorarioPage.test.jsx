// @vitest-environment jsdom
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { cleanup, render, screen, waitFor, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter, Route, Routes, useLocation } from 'react-router'
import HorarioPage from './HorarioPage'
import { mockFetch, normalizar, stubNavegador } from '@/test/utils'

// Muestra la URL actual para comprobar lo que queda en ?fecha= y ?hora=
function Url() {
  const { search } = useLocation()
  return <output data-testid="url">{search}</output>
}

function renderPaso2(url) {
  return render(
    <MemoryRouter initialEntries={[url]}>
      <Routes>
        <Route
          path="/reservar/horario"
          element={
            <>
              <HorarioPage />
              <Url />
            </>
          }
        />
        <Route path="/reservar" element={<p>Paso 1</p>} />
      </Routes>
    </MemoryRouter>,
  )
}

const barra = () => screen.getByTestId('barra-reserva')
const hora = (texto) => screen.findByRole('button', { name: texto })
const dia = (nombre) => screen.findByRole('button', { name: new RegExp(nombre) })

describe('Paso 2: elegir fecha y hora', () => {
  beforeEach(stubNavegador)
  afterEach(() => {
    cleanup()
    vi.unstubAllGlobals()
  })

  it('se preselecciona el primer día libre; elegir hora muestra la barra y Continuar lleva al paso 3', async () => {
    mockFetch()
    const user = userEvent.setup()
    renderPaso2('/reservar/horario?servicios=2')

    expect(await screen.findByRole('heading', { name: 'Horas del jueves 1' })).toBeTruthy()
    expect(barra().dataset.visible).toBe('false')

    await user.click(await hora('16:00'))

    expect(barra().dataset.visible).toBe('true')
    expect(normalizar(within(barra()).getByText(/Jueves/).textContent)).toBe('Jueves 1 oct · 16:00')
    expect(within(barra()).getByRole('link', { name: 'Continuar' }).getAttribute('href')).toBe(
      '/reservar/datos?servicios=2&fecha=2026-10-01&hora=16:00',
    )

    // Cambiar de día quita la hora
    await user.click(await dia('lunes 5 de octubre'))
    expect(await screen.findByRole('heading', { name: 'Horas del lunes 5' })).toBeTruthy()
    expect(barra().dataset.visible).toBe('false')
    expect(screen.getByTestId('url').textContent).toBe('?servicios=2&fecha=2026-10-05')
  })

  it('un día completo o cerrado no se puede elegir', async () => {
    mockFetch()
    renderPaso2('/reservar/horario?servicios=2')

    const completo = await dia('viernes 2 de octubre, completo')
    const cerrado = await dia('sábado 3 de octubre, cerrado')
    expect(completo.hasAttribute('disabled')).toBe(true)
    expect(cerrado.hasAttribute('disabled')).toBe(true)
  })

  it('una hora de la URL que ya no está libre se quita sin avisar', async () => {
    mockFetch()
    renderPaso2('/reservar/horario?servicios=2&fecha=2026-10-01&hora=23:00')

    await hora('9:00')
    expect(screen.queryAllByRole('button', { pressed: true })).toHaveLength(0)
    expect(barra().dataset.visible).toBe('false')
    await waitFor(() => expect(screen.getByTestId('url').textContent).toBe('?servicios=2&fecha=2026-10-01'))
  })

  it('si falla la API de huecos muestra el error con el enlace para llamar', async () => {
    mockFetch({ fallos: ['/api/huecos'] })
    renderPaso2('/reservar/horario?servicios=2')

    const alerta = await screen.findByRole('alert')
    expect(alerta.textContent).toContain('No podemos mostrar las horas ahora mismo')
    expect(within(alerta).getByRole('link', { name: /Llamar para reservar/ }).getAttribute('href')).toBe(
      'tel:+34600000000',
    )
  })

  it('sin ningún día libre en el plazo lo dice y ofrece llamar', async () => {
    mockFetch({ respuestas: { '/api/huecos': [{ fecha: '2026-10-01', estado: 'COMPLETO', horas: [] }] } })
    renderPaso2('/reservar/horario?servicios=2')

    const alerta = await screen.findByRole('alert')
    expect(alerta.textContent).toContain('No quedan horas libres en los próximos 1 días')
  })
})
