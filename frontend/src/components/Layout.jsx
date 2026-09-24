import { useEffect, useState } from 'react'
import { Outlet, useLocation } from 'react-router'
import Header from '@/components/Header'
import Footer from '@/components/Footer'
import { cn } from '@/lib/utils'

export default function Layout() {
  const { pathname, hash } = useLocation()
  // HomePage lo activa mientras se ve la barra de reserva, para que no tape el pie
  const [barraVisible, setBarraVisible] = useState(false)

  // Al cambiar de página se empieza arriba; los anclas los gestiona HomePage
  useEffect(() => {
    if (!hash) window.scrollTo(0, 0)
  }, [pathname, hash])

  return (
    <div
      className={cn(
        'flex min-h-svh flex-col transition-[padding]',
        barraVisible && 'pb-[calc(5rem+env(safe-area-inset-bottom))]',
      )}
    >
      <Header />
      <main className="flex-1">
        <Outlet context={{ setBarraVisible }} />
      </main>
      <Footer />
    </div>
  )
}
