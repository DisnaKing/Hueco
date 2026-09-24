import { useCallback } from 'react'
import { useLocation, useNavigate } from 'react-router'
import { scrollToId } from '@/lib/scroll'

// "Reservar": en el home baja al selector; desde otra página vuelve al home
// y HomePage baja cuando los servicios ya se han cargado.
export function useScrollToSelector() {
  const { pathname } = useLocation()
  const navigate = useNavigate()

  return useCallback(() => {
    if (pathname === '/') {
      scrollToId('servicios')
    } else {
      navigate('/#servicios')
    }
  }, [pathname, navigate])
}
