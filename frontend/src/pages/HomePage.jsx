import { useEffect, useState } from 'react'
import { useLocation, useOutletContext } from 'react-router'
import { useNegocio, useServicios } from '@/api/useFetch'
import { scrollToId } from '@/lib/scroll'
import Hero from '@/components/home/Hero'
import BarraReserva from '@/components/home/BarraReserva'
import SobreNosotros from '@/components/home/SobreNosotros'
import Opiniones from '@/components/home/Opiniones'
import Contacto from '@/components/home/Contacto'

export default function HomePage() {
  const negocio = useNegocio()
  const servicios = useServicios()
  const [seleccion, setSeleccion] = useState(() => new Set())
  const { setBarraVisible } = useOutletContext()
  const location = useLocation()

  const elegidos = (servicios.data ?? []).filter((s) => seleccion.has(s.id))

  function toggle(id) {
    setSeleccion((actual) => {
      const nueva = new Set(actual)
      if (nueva.has(id)) nueva.delete(id)
      else nueva.add(id)
      return nueva
    })
  }

  useEffect(() => {
    setBarraVisible(elegidos.length > 0)
  }, [elegidos.length, setBarraVisible])

  useEffect(() => () => setBarraVisible(false), [setBarraVisible])

  // Baja al ancla cuando ya ha cargado todo: los bloques de carga tienen otra altura
  const cargado = !servicios.loading && !negocio.loading
  useEffect(() => {
    if (cargado && location.hash) scrollToId(location.hash.slice(1))
  }, [cargado, location.hash, location.key])

  return (
    <>
      <Hero negocio={negocio} servicios={servicios} seleccion={seleccion} onToggle={toggle} />
      <SobreNosotros negocio={negocio} />
      <Opiniones testimonios={negocio.data?.testimonios} />
      <Contacto negocio={negocio} />
      <BarraReserva elegidos={elegidos} />
    </>
  )
}
