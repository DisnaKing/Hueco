import { useEffect, useRef, useState } from 'react'
import { getJson } from './client'

// path null: no se pide nada (por ejemplo, sin servicios que consultar).
// recargarAlVolverTrasMs: al volver a la pestaña, se pide de nuevo si los datos tienen más de ese tiempo.
export function useFetch(path, { recargarAlVolverTrasMs } = {}) {
  const [state, setState] = useState({ data: null, loading: path !== null, error: null })
  const [version, setVersion] = useState(0)
  const cargadoEn = useRef(0)

  useEffect(() => {
    if (path === null) return
    const controller = new AbortController()
    getJson(path, { signal: controller.signal })
      .then((data) => {
        cargadoEn.current = Date.now()
        setState({ data, loading: false, error: null })
      })
      .catch((error) => {
        if (!controller.signal.aborted) setState({ data: null, loading: false, error })
      })
    return () => controller.abort()
  }, [path, version])

  useEffect(() => {
    if (!recargarAlVolverTrasMs) return
    function alVolver() {
      const viejos = Date.now() - cargadoEn.current > recargarAlVolverTrasMs
      if (document.visibilityState === 'visible' && viejos) setVersion((v) => v + 1)
    }
    document.addEventListener('visibilitychange', alVolver)
    return () => document.removeEventListener('visibilitychange', alVolver)
  }, [recargarAlVolverTrasMs])

  return state
}

export const useNegocio = () => useFetch('/api/negocio')
export const useServicios = () => useFetch('/api/servicios')

// Las horas libres caducan rápido: si el cliente deja la pestaña más de 2 minutos, se vuelven a pedir
const HUECOS_CADUCAN_MS = 2 * 60 * 1000
export const useHuecos = (ids) =>
  useFetch(ids ? `/api/huecos?servicios=${ids}` : null, { recargarAlVolverTrasMs: HUECOS_CADUCAN_MS })
