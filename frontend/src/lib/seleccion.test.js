import { describe, expect, it } from 'vitest'
import { anclaCategoria, parseIds, serviciosElegidos } from './seleccion'

describe('parseIds', () => {
  it('lee enteros positivos separados por comas', () => {
    expect(parseIds('1,3')).toEqual([1, 3])
  })
  it('descarta basura, ceros, negativos y repetidos', () => {
    expect(parseIds('2,abc,,0,-4,2, 5 ,1.5')).toEqual([2, 5])
  })
  it('sin valor devuelve una lista vacía', () => {
    expect(parseIds(null)).toEqual([])
    expect(parseIds('')).toEqual([])
  })
})

describe('serviciosElegidos', () => {
  const servicios = [{ id: 2 }, { id: 1 }, { id: 4 }]

  it('ignora los ids que no están entre los activos', () => {
    expect(serviciosElegidos([2, 99], servicios)).toEqual([{ id: 2 }])
  })
  it('devuelve los servicios en el orden de la carta', () => {
    expect(serviciosElegidos([4, 2], servicios)).toEqual([{ id: 2 }, { id: 4 }])
  })
})

describe('anclaCategoria', () => {
  it('quita acentos y espacios', () => {
    expect(anclaCategoria('Coloración y Mechas')).toBe('cat-coloracion-y-mechas')
  })
  it('usa "otros" sin categoría', () => {
    expect(anclaCategoria(null)).toBe('cat-otros')
  })
})
