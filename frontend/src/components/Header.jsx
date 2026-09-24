import { Link } from 'react-router'
import { UserRound } from 'lucide-react'
import business from '@/business.config'
import texts from '@/texts/es'
import { Button } from '@/components/ui/button'
import MobileMenu from '@/components/MobileMenu'
import { navLinks } from '@/components/navLinks'
import { useScrollToSelector } from '@/hooks/useScrollToSelector'

export default function Header() {
  const scrollToSelector = useScrollToSelector()

  return (
    <header className="sticky top-0 z-40 border-b border-line bg-surface/95 backdrop-blur supports-[backdrop-filter]:bg-surface/80">
      <div className="mx-auto flex h-16 max-w-5xl items-center gap-2 px-4">
        <Link to="/" className="mr-auto flex min-w-0 items-center gap-2 rounded-md">
          <img src={business.logo} alt="" width="36" height="36" className="size-9 shrink-0" />
          <span className="truncate font-display text-base font-semibold sm:text-lg">{business.nombre}</span>
        </Link>

        <nav aria-label={texts.nav.menu} className="mr-4 hidden items-center gap-6 md:flex">
          {navLinks.map((link) => (
            <Link
              key={link.to}
              to={link.to}
              className="text-sm font-medium text-ink/80 transition-colors hover:text-primary"
            >
              {link.label}
            </Link>
          ))}
        </nav>

        <Button onClick={scrollToSelector} className="h-10 rounded-full px-4 sm:px-5">
          {texts.nav.reservar}
        </Button>

        <Link
          to="/login"
          aria-label={texts.nav.iniciarSesion}
          title={texts.nav.iniciarSesion}
          className="hidden size-10 items-center justify-center rounded-full border border-line bg-card text-muted transition-colors hover:text-primary md:inline-flex"
        >
          <UserRound className="size-5" aria-hidden="true" />
        </Link>

        <MobileMenu />
      </div>
    </header>
  )
}
