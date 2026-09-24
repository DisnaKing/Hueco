import { useRef, useState } from 'react'
import { Link, useNavigate } from 'react-router'
import { Menu, UserRound, X } from 'lucide-react'
import texts from '@/texts/es'
import { Button } from '@/components/ui/button'
import {
  Drawer,
  DrawerClose,
  DrawerContent,
  DrawerDescription,
  DrawerHeader,
  DrawerTitle,
  DrawerTrigger,
} from '@/components/ui/drawer'
import { navLinks } from '@/components/navLinks'

// El Drawer (Radix Dialog por debajo) ya gestiona Esc, el clic en el fondo,
// el bloqueo del scroll, aria-expanded/aria-controls en ☰ y el foco.
export default function MobileMenu() {
  const [open, setOpen] = useState(false)
  const pendiente = useRef(null)
  const navigate = useNavigate()

  // Navega cuando el panel ya se ha cerrado: mientras está abierto el scroll de la página está bloqueado
  function irA(event, to) {
    event.preventDefault()
    pendiente.current = to
    setOpen(false)
  }

  function onAnimationEnd(abierto) {
    if (!abierto && pendiente.current) {
      navigate(pendiente.current)
      pendiente.current = null
    }
  }

  return (
    <Drawer direction="right" open={open} onOpenChange={setOpen} onAnimationEnd={onAnimationEnd}>
      <DrawerTrigger asChild>
        <Button variant="ghost" size="icon" className="size-10 md:hidden" aria-label={texts.nav.abrirMenu}>
          <Menu className="size-6" aria-hidden="true" />
        </Button>
      </DrawerTrigger>

      <DrawerContent className="pb-[env(safe-area-inset-bottom)]">
        <DrawerHeader className="flex-row items-center justify-between border-b border-line">
          <DrawerTitle className="font-display text-lg">{texts.nav.menu}</DrawerTitle>
          <DrawerDescription className="sr-only">{texts.nav.menu}</DrawerDescription>
          <DrawerClose asChild>
            <Button variant="ghost" size="icon" className="size-10" aria-label={texts.nav.cerrarMenu}>
              <X className="size-5" aria-hidden="true" />
            </Button>
          </DrawerClose>
        </DrawerHeader>

        <nav aria-label={texts.nav.menu} className="flex flex-col p-4">
          <Link
            to="/login"
            onClick={(event) => irA(event, '/login')}
            className="mb-4 flex items-center gap-3 rounded-xl border border-line bg-card p-3 font-medium"
          >
            <span className="flex size-10 items-center justify-center rounded-full bg-accent text-muted">
              <UserRound className="size-5" aria-hidden="true" />
            </span>
            {texts.nav.iniciarSesion}
          </Link>

          {navLinks.map((link) => (
            <Link
              key={link.to}
              to={link.to}
              onClick={(event) => irA(event, link.to)}
              className="rounded-lg px-3 py-3 text-lg font-medium hover:bg-accent"
            >
              {link.label}
            </Link>
          ))}
        </nav>
      </DrawerContent>
    </Drawer>
  )
}
