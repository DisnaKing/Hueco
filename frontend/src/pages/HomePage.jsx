import { useEffect } from 'react'
import { useLocation } from 'react-router'
import { useNegocio, useServicios } from '@/api/useFetch'
import { scrollToId } from '@/lib/scroll'
import Hero from '@/components/home/Hero'
import CartaServicios from '@/components/home/CartaServicios'
import SobreNosotros from '@/components/home/SobreNosotros'
import Opiniones from '@/components/home/Opiniones'
import Contacto from '@/components/home/Contacto'

export default function HomePage() {
  const negocio = useNegocio()
  const servicios = useServicios()
  const location = useLocation()

  // Baja al ancla cuando ya ha cargado todo: los bloques de carga tienen otra altura
  const cargado = !servicios.loading && !negocio.loading
  useEffect(() => {
    if (cargado && location.hash) scrollToId(location.hash.slice(1))
  }, [cargado, location.hash, location.key])

  return (
    <>
      <Hero negocio={negocio} />
      <CartaServicios servicios={servicios} />
      <SobreNosotros negocio={negocio} />
      <Opiniones testimonios={negocio.data?.testimonios} />
      <Contacto negocio={negocio} />
    </>
  )
}
