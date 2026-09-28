import { describe, expect, it } from 'vitest'
import { MAX_NOTAS, validarDatos } from './datosReserva'

const validos = { nombre: 'Ana', telefono: '600 111 222', email: '', notas: '' }

describe('validarDatos', () => {
  it('acepta los datos mínimos', () => {
    expect(validarDatos(validos)).toEqual({})
  })
  it('acepta el teléfono con prefijo y separadores', () => {
    expect(validarDatos({ ...validos, telefono: '+34 600-111-222' })).toEqual({})
    expect(validarDatos({ ...validos, telefono: '0034 912 345 678' })).toEqual({})
  })
  it('marca cada campo inválido', () => {
    const errores = validarDatos({ nombre: '  ', telefono: '12345', email: 'ana', notas: 'x'.repeat(MAX_NOTAS + 1) })
    expect(Object.keys(errores).sort()).toEqual(['email', 'nombre', 'notas', 'telefono'])
  })
  it('no acepta teléfonos que no son españoles', () => {
    expect(validarDatos({ ...validos, telefono: '+44 7700 900123' })).toHaveProperty('telefono')
    expect(validarDatos({ ...validos, telefono: '512345678' })).toHaveProperty('telefono')
  })
})
