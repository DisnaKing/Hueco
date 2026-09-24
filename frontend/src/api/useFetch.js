import { useEffect, useState } from 'react'
import { getJson } from './client'

export function useFetch(path) {
  const [state, setState] = useState({ data: null, loading: true, error: null })

  useEffect(() => {
    const controller = new AbortController()
    getJson(path, { signal: controller.signal })
      .then((data) => setState({ data, loading: false, error: null }))
      .catch((error) => {
        if (!controller.signal.aborted) setState({ data: null, loading: false, error })
      })
    return () => controller.abort()
  }, [path])

  return state
}

export const useNegocio = () => useFetch('/api/negocio')
export const useServicios = () => useFetch('/api/servicios')
