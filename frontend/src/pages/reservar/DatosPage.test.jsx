// @vitest-environment jsdom
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { cleanup, render, screen, waitFor, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter, Route, Routes, useParams } from 'react-router'
import DatosPage from './DatosPage'
import { mockFetch, stubNavegador } from '@/test/utils'

// El jueves 1/10/2026 a las 16:00 está libre en los huecos de prueba
const URL_PASO3 = '/reservar/datos?servicios=2&fecha=2026-10-01&hora=16:00'

function Confirmada() {
  const { token } = useParams()
  return <p>Confirmada {token}</p>
}

function renderPaso3() {
  return render(
    <MemoryRouter initialEntries={[URL_PASO3]}>
      <Routes>
        <Route path="/reservar/datos" element={<DatosPage />} />
        <Route path="/reservar/confirmada/:token" element={<Confirmada />} />
        <Route path="/reservar/horario" element={<p>Paso 2</p>} />
      </Routes>
    </MemoryRouter>,
  )
}

async function rellenar(user, { nombre = 'Ana', telefono = '600 111 222' } = {}) {
  await user.type(await screen.findByLabelText('Nombre'), nombre)
  await user.type(screen.getByLabelText('Teléfono'), telefono)
}

const confirmar = () => screen.getByRole('button', { name: 'Confirmar cita' })

describe('Paso 3: confirmar la cita', () => {
  beforeEach(() => {
    stubNavegador()
    sessionStorage.clear()
  })
  afterEach(() => {
    cleanup()
    vi.unstubAllGlobals()
  })

  it('con los datos bien, reserva y pasa a la confirmación', async () => {
    const fetch = mockFetch({ posts: { '/api/reservas': { status: 201, body: { token: 'abc-123' } } } })
    const user = userEvent.setup()
    renderPaso3()

    await rellenar(user)
    await user.click(confirmar())

    expect(await screen.findByText('Confirmada abc-123')).toBeTruthy()
    const [, init] = fetch.mock.calls.find(([, i]) => i?.method === 'POST')
    expect(JSON.parse(init.body)).toMatchObject({
      servicios: [2],
      fecha: '2026-10-01',
      hora: '16:00',
      nombre: 'Ana',
      telefono: '600 111 222',
      website: '',
    })
    expect(sessionStorage.getItem('hueco.datosReserva')).toBeNull()
  })

  it('no envía si faltan datos y marca los campos', async () => {
    const fetch = mockFetch()
    const user = userEvent.setup()
    renderPaso3()

    await screen.findByLabelText('Nombre')
    await user.click(confirmar())

    expect(screen.getByLabelText('Nombre').getAttribute('aria-invalid')).toBe('true')
    expect(screen.getByText('Escribe tu nombre')).toBeTruthy()
    expect(screen.getByText('Escribe un teléfono de 9 cifras')).toBeTruthy()
    expect(fetch.mock.calls.some(([, i]) => i?.method === 'POST')).toBe(false)
  })

  it('muestra junto a cada campo los errores del servidor', async () => {
    mockFetch({ posts: { '/api/reservas': { status: 400, body: { errores: { email: 'Revisa el email' } } } } })
    const user = userEvent.setup()
    renderPaso3()

    await rellenar(user)
    await user.type(screen.getByLabelText(/Email/), 'ana@ejemplo.es')
    await user.click(confirmar())

    const email = screen.getByLabelText(/Email/)
    await waitFor(() => expect(email.getAttribute('aria-invalid')).toBe('true'))
    expect(document.getElementById(email.getAttribute('aria-describedby').split(' ')[0]).textContent).toBe(
      'Revisa el email',
    )
  })

  it('si la hora se ocupa, ofrece elegir otra y conserva lo escrito', async () => {
    mockFetch({ posts: { '/api/reservas': { status: 409, body: { motivo: 'HORA_OCUPADA' } } } })
    const user = userEvent.setup()
    renderPaso3()

    await rellenar(user)
    await user.click(confirmar())

    const alerta = await screen.findByRole('alert')
    expect(alerta.textContent).toContain('Esa hora se acaba de ocupar')
    expect(within(alerta).getByRole('link', { name: 'Elegir otra hora' }).getAttribute('href')).toBe(
      '/reservar/horario?servicios=2&fecha=2026-10-01',
    )
    // Reintentar daría otro 409: el botón de confirmar desaparece
    expect(screen.queryByRole('button', { name: 'Confirmar cita' })).toBeNull()

    // Al volver al paso 3 (otra hora), los datos siguen ahí
    cleanup()
    renderPaso3()
    expect((await screen.findByLabelText('Nombre')).value).toBe('Ana')
    expect(screen.getByLabelText('Teléfono').value).toBe('600 111 222')
  })

  it('el límite por teléfono lo explica y ofrece llamar', async () => {
    mockFetch({ posts: { '/api/reservas': { status: 429, body: { motivo: 'LIMITE_TELEFONO' } } } })
    const user = userEvent.setup()
    renderPaso3()

    await rellenar(user)
    await user.click(confirmar())

    const alerta = await screen.findByRole('alert')
    expect(alerta.textContent).toContain('el máximo de citas pendientes')
    expect(within(alerta).getByRole('link', { name: /Llamar para reservar/ }).getAttribute('href')).toBe(
      'tel:+34600000000',
    )
  })

  it('el campo trampa no se ve ni se anuncia', async () => {
    mockFetch()
    renderPaso3()

    await screen.findByLabelText('Nombre')
    const trampa = document.querySelector('input[name=website]')
    expect(trampa.tabIndex).toBe(-1)
    expect(trampa.closest('[aria-hidden=true]')).not.toBeNull()
    expect(screen.queryByRole('textbox', { name: 'Web' })).toBeNull()
  })

  it('el formulario se anuncia por su título y contiene los campos y el botón', async () => {
    mockFetch()
    renderPaso3()

    const formulario = await screen.findByRole('form', { name: '¿A nombre de quién?' })
    for (const etiqueta of ['Nombre', 'Teléfono', /Email/, /Notas para el comercio/]) {
      expect(within(formulario).getByLabelText(etiqueta)).toBeTruthy()
    }
    expect(within(formulario).getByRole('button', { name: 'Confirmar cita' })).toBeTruthy()
  })

  it('el teléfono no lleva ayuda y las notas sí', async () => {
    mockFetch()
    renderPaso3()

    const telefono = await screen.findByLabelText('Teléfono')
    expect(telefono.hasAttribute('aria-describedby')).toBe(false)
    const notas = screen.getByLabelText(/Notas para el comercio/)
    expect(document.getElementById(notas.getAttribute('aria-describedby')).textContent).toBe(
      'Por ejemplo: pelo muy largo, vengo con mi hija…',
    )
  })
})
